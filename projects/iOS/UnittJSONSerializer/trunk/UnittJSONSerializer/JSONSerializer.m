//
//  JSONSerializer.m
//  UnittJSONSerializer
//
//  Created by Josh Morris on 5/27/11.
//  Copyright 2011 UnitT Software. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not
//  use this file except in compliance with the License. You may obtain a copy of
//  the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
// 
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//  License for the specific language governing permissions and limitations under
//  the License.
//

#import "JSONSerializer.h"


@interface JSONSerializer()

@property (retain) NSMutableDictionary* classDefs;

- (JSONPropertyInfo*) getPropertyInfoForObject:(id) aObject name:(NSString*) aName;
- (id) getPropertyValueForObject:(id) aObject name:(NSString*) aName;
- (void) setPropertyValueForObject:(id) aObject name:(NSString*) aName value:(id) aValue;
- (void) fillPropertiesForClass:(Class) aClass;
- (void) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject;
- (id) performSelectorSafelyForObject:(id) aObject selector:(SEL) aSelector argument:(id) argument;
- (NSDictionary*) objectToDictionary:(id) aObject;

@end

#pragma mark -

@implementation JSONSerializer

@synthesize classDefs;


#pragma mark Property Handling
- (id) getPropertyValueForObject:(id) aObject name:(NSString*) aName
{    
    JSONPropertyInfo* info = [self getPropertyInfoForObject:aObject name:aName];
    
    //if we have a property, set it
    if (info)
    {
        //verify we are not write only
        if (info.getter)
        {
            //return getter value
            return [self performSelectorSafelyForObject:aObject selector:info.getter argument:nil];
        }
    }
    
    return nil;
}

- (void) setPropertyValueForObject:(id) aObject name:(NSString*) aName value:(id) aValue
{
    JSONPropertyInfo* info = [self getPropertyInfoForObject:aObject name:aName];
    
    //if we have a property, set it
    if (info)
    {
        //verify we are not read-only
        if (info.setter)
        {
            //apply setter
            [self performSelectorSafelyForObject:aObject selector:info.setter argument:aValue];
        }
    }
}

- (id) performSelectorSafelyForObject:(id) aObject selector:(SEL) aSelector argument:(id) argument
{
    if (aSelector != nil && aObject != nil)
    {
        NSMethodSignature* methodSig = [aObject methodSignatureForSelector:aSelector];
        if(methodSig == nil)
        {
            return nil;
        }
        
        const char* retType = [methodSig methodReturnType];
        if(strcmp(retType, @encode(id)) == 0 || strcmp(retType, @encode(void)) == 0)
        {
            if (argument)
            {
                return [aObject performSelector:aSelector withObject:argument];
            }
            else
            {
                return [aObject performSelector:aSelector];
            }
        } 
        else 
        {
            NSLog(@"-[%@ performSelector:@selector(%@)] shouldn't be used. The selector doesn't return an object or void", NSStringFromClass([aObject class]), NSStringFromSelector(aSelector));
        }
    }
    
    return nil;
}

- (JSONPropertyInfo*) getPropertyInfoForObject:(id) aObject name:(NSString*) aName
{
    NSString* className = NSStringFromClass([aObject class]);
    id value = [classDefs objectForKey:className];
    
    //create missing class definition
    if (!value)
    {
        [self fillPropertiesForClass:[aObject class]];
        value = [classDefs objectForKey:className];
    }
    
    //use class definition
    if (value)
    {
        if ([value isKindOfClass:[NSDictionary class]])
        {
            //get property info
            NSDictionary* classDef = (NSDictionary*) value;
            value =  [classDef objectForKey:aName];
            if (value && [value isKindOfClass:[JSONPropertyInfo class]])
            {
                return (JSONPropertyInfo*) value;
            }
        }
    }
    
    return nil;
}

- (Class) readConcreteClassFromDictionary:(NSDictionary*) aData
{
    //default implementation - do nothing
    return nil;
}

- (void) writeConcreteClass:(Class) aType dictionary:(NSDictionary*) aData
{
    //default implementation - do nothing
}

- (NSDictionary*) getPropertiesForClass:(Class) aClass
{
    NSDictionary* properties = [classDefs objectForKey:aClass];
    if (!properties)
    {
        [self fillPropertiesForClass: aClass];
    }
    return [classDefs objectForKey:aClass];
}

- (void) fillPropertiesForClass:(Class) aClass
{
    NSMutableDictionary* classDef = [NSMutableDictionary dictionary];
    //loop through properties - creating metadata for getters/setters
    unsigned int outCount, i;
    objc_property_t *properties = class_copyPropertyList(aClass, &outCount);
    for (i = 0; i < outCount; i++) 
    {
        //grab property
        objc_property_t property = properties[i];
        
        if (property != NULL)
        {
            //init property info
            JSONPropertyInfo* propertyInfo = [[[JSONPropertyInfo alloc] init] autorelease];
            const char *propertyName = property_getName(property);
            const char *propertyAttr = property_getAttributes(property);
            propertyInfo.name = [NSString stringWithCString:propertyName encoding:[NSString defaultCStringEncoding]];
            
            //handle type
            BOOL isObject = false;
            switch(propertyAttr[1]) 
            {
                case 'd' : //double
                    propertyInfo.dataType = JSDataTypeDouble;
                    break;
                case 'l' : //long
                    propertyInfo.dataType = JSDataTypeLong;
                    break;
                case 'i' : //int
                    propertyInfo.dataType = JSDataTypeInt;
                    break;
                case 'c' : //BOOL
                    propertyInfo.dataType = JSDataTypeBoolean;
                    break;
                case '@' : //ObjC object
                    isObject = true;
                    break;
            }
            
            //handle custom class - if applicable
            if (isObject)
            {
                //determine custom class
                const char* typeInitialFragment = strstr( propertyAttr, "T@\"" );
                if ( typeInitialFragment != NULL )
                {   
                    typeInitialFragment += 3;
                    const char* typeSecondFragment = strstr( typeInitialFragment, "\"," );
                    if ( typeSecondFragment != NULL && typeInitialFragment != typeSecondFragment )
                    {
                        //we have a customer class - grab the name
                        int length = (int)(typeSecondFragment - typeInitialFragment);
                        char* typeName = malloc( length + 1 );
                        memcpy( typeName, typeInitialFragment, length );
                        typeName[length] = '\0';
                        
                        //get class and set type
                        propertyInfo.customClass = NSClassFromString([NSString stringWithCString:typeName encoding:[NSString defaultCStringEncoding]]);
                        if (propertyInfo.customClass) 
                        {
                            if ([NSArray class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSDataTypeNSArray;
                            }
                            else if ([NSDictionary class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSDataTypeNSDictionary;
                            }
                            else if ([NSNumber class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSDataTypeNSNumber;
                            }
                            else if ([NSDate class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSDataTypeNSDate;
                            }
                            else if ([NSString class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSDataTypeNSString;
                            }
                        }
                        free( typeName );
                    }
                }
            }
            
            //handle custom getter
            const char* custGetterInitialFragment = strstr( propertyAttr, ",G" );
            if ( custGetterInitialFragment != NULL )
            {   
                custGetterInitialFragment += 2;
                const char* custGetterSecondFragment = strchr( custGetterInitialFragment, ',' );
                if ( custGetterSecondFragment != NULL && custGetterInitialFragment != custGetterSecondFragment )
                {
                    //we have a customer getter, grab selector from name
                    int length = (int)(custGetterSecondFragment - custGetterInitialFragment);
                    char* custGetterName = malloc( length + 1 );
                    memcpy( custGetterName, custGetterInitialFragment, length );
                    custGetterName[length] = '\0';
                    propertyInfo.getter = sel_getUid( custGetterName );
                    free( custGetterName );
                }
            }
            
            //if missing getter - use default
            propertyInfo.getter = NSSelectorFromString(propertyInfo.name);
            
            //if not readonly - handle setter
            if (strstr(propertyAttr, ",R,") ==  NULL)
            {
                //handle custom setter
                const char* custSetterInitialFragment = strstr( propertyAttr, ",S" );
                if ( custSetterInitialFragment != NULL )
                {   
                    custSetterInitialFragment += 2;
                    const char* custSetterSecondFragment = strchr( custSetterInitialFragment, ',' );
                    if ( custSetterSecondFragment != NULL && custSetterInitialFragment != custSetterSecondFragment )
                    {
                        //we have a customer setter, grab selector from name
                        int length = (int)(custSetterSecondFragment - custSetterInitialFragment);
                        char* custSetterName = malloc( length + 1 );
                        memcpy( custSetterName, custSetterInitialFragment, length );
                        custSetterName[length] = '\0';
                        propertyInfo.setter = sel_getUid( custSetterName );
                        free( custSetterName );
                    }
                }
                
                //if missing Setter - use default
                propertyInfo.setter = NSSelectorFromString([NSString stringWithFormat:@"set%@", propertyInfo.name]);
            }
            
            //push property into class descripton
            [classDef setObject:propertyInfo forKey:propertyInfo.name];
        }
    }
    
    //if we have properties - save them
    if ([classDef count] > 0)
    {
        [self.classDefs setObject:classDef forKey:NSStringFromClass(aClass)];
    }
}


#pragma mark Deserialize
- (void) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject
{
    //get class def
    NSDictionary* classDef = [self getPropertiesForClass:[aObject class]];
    
    //loop through dictionary, applying values
    for (NSString* key in aData) 
    {
        JSONPropertyInfo* property = [classDef objectForKey:key];
        
        //if we have the property - use it to set the property - if possible
        if (property && property.setter)
        {
            id value = [aData objectForKey:key];
            id newValue = value;
            
            //deserialize custom object
            if (property.dataType != JSDataTypeCustomClass)
            {
                Class valueConcreteClass = [self readConcreteClassFromDictionary:value];
                if (valueConcreteClass == nil)
                {
                    valueConcreteClass = property.customClass;
                }
                newValue = [[valueConcreteClass alloc] init];
                [self fillObjectFromDictionary:value object:newValue];
            }
            
            //set value
            [self performSelectorSafelyForObject:aObject selector:property.setter argument:newValue];
        }
    }
}

- (void) fillObjectFromData:(NSData*) aData object:(id) aObject
{
    //create dictionary from data
    NSDictionary* data = [aData objectFromJSONDataWithParseOptions:parseOptions];
    
    //fill object using dictionary
    [self fillObjectFromDictionary:data object:aObject];
}

- (void) fillObjectFromString:(NSString*) aData object:(id) aObject
{
    //create dictionary from string
    NSDictionary* data = [aData objectFromJSONStringWithParseOptions:parseOptions];
    
    //fill object using dictionary
    [self fillObjectFromDictionary:data object:aObject];
}

- (id) deserializeObjectFromData:(NSData*) aData type:(Class) aClass
{
    //create object of the specified type
    id result = [[aClass alloc] init];
    
    //fill object using deserialized JSON
    [self fillObjectFromData:aData object:result];
    
    return result;
}

- (id) deserializeObjectFromString:(NSString*) aData type:(Class) aClass
{
    //create object of the specified type
    id result = [[aClass alloc] init];
    
    //fill object using deserialized JSON
    [self fillObjectFromString:aData object:result];
    
    return result;
}

- (NSArray*) deserializeArrayFromType:(NSArray*) aClasses dataArray:(NSArray*) aData
{
    //init
    NSMutableArray* results = [NSMutableArray array];
    
    //loop through array
    int length = aClasses.count;
    if (aData && aData.count < length)
    {
        length = aData.count;
    }
    for (int i = 0; i < length; i++) 
    {
        id result = [aData objectAtIndex:i];
        Class type = [aClasses objectAtIndex:i];
        [results addObject:[self deserializeObjectFromData:result type:type]];
    }
    
    return results;
}

- (NSArray*) deserializeArrayFromType:(NSArray*) aClasses data:(NSData*) aData
{
    //create array from data
    return [self deserializeArrayFromType:aClasses dataArray:[aData objectFromJSONDataWithParseOptions:parseOptions]];
}

- (NSArray*) deserializeArrayFromType:(NSArray*) aClasses string:(NSString*) aData
{
    //create array from string
    return [self deserializeArrayFromType:aClasses dataArray:[aData objectFromJSONStringWithParseOptions:parseOptions]];
}


#pragma mark Serialize
- (NSDictionary*) objectToDictionary:(id) aObject
{
    NSMutableDictionary* results = [NSMutableDictionary dictionary];
    
    //convert the object to a nested dictionary using properties
    Class type = [aObject class];
    NSDictionary* properties = [self getPropertiesForClass:type];
    if (properties)
    {
        for (NSString* key in properties) 
        {
            JSONPropertyInfo* property = [properties objectForKey:key];
            if (property && property.getter)
            {
                id value = [self performSelectorSafelyForObject:aObject selector:property.getter argument:nil];
                if (property.dataType == JSDataTypeCustomClass)
                {
                    value = [self objectToDictionary:value];
                }
                [results setObject:value forKey:property.name];
            }
        }
    }
    [self writeConcreteClass:type dictionary:results];
    
    return results;
}

- (NSData*) serializeToDataFromObject:(id) aObject
{
    //convert the object to JSON data
    return [[self objectToDictionary:aObject] JSONDataWithOptions:serializeOptions error:nil];
}

- (NSString*) serializeToStringFromObject:(id) aObject
{
    //convert the object to a JSON string
    return [[self objectToDictionary:aObject] JSONStringWithOptions:serializeOptions error:nil];
}

- (NSData*) serializeToDataFromArray:(NSArray*) aObjects
{
    //init
    NSMutableArray* arrayOfObjectDictionaries = [NSMutableArray array];
    
    //convert each item to dictionary
    for (id item in aObjects) 
    {
        [arrayOfObjectDictionaries addObject:[self objectToDictionary:item]];
    }
    
    //convert array of dictionaries to JSON data
    return [arrayOfObjectDictionaries JSONDataWithOptions:serializeOptions error:nil];
}

- (NSString*) serializeToStringFromArray:(NSArray*) aObjects
{
    //init
    NSMutableArray* arrayOfObjectDictionaries = [NSMutableArray array];
    
    //convert each item to dictionary
    for (id item in aObjects) 
    {
        [arrayOfObjectDictionaries addObject:[self objectToDictionary:item]];
    }
    
    //convert array of dictionaries to JSON data
    return [arrayOfObjectDictionaries JSONStringWithOptions:serializeOptions error:nil];
}


#pragma mark Lifecycle
+ (id) serializerWithParseOptions: (JSParseOptionFlags) aParseOptions serializeOptions: (JSSerializeOptionFlags) aSerializeOptions
{
    return [[[JSONSerializer alloc] initWithParseOptions:aParseOptions serializeOptions:aSerializeOptions] autorelease];
}

- (id) initWithParseOptions: (JSParseOptionFlags) aParseOptions serializeOptions: (JSSerializeOptionFlags) aSerializeOptions
{
    self = [super init];
    if (self) 
    {
        parseOptions = aParseOptions;
        serializeOptions = aSerializeOptions;
        self.classDefs = [NSMutableDictionary dictionary];
    }
    return self;
}

- (id) init
{
    self = [super init];
    if (self) 
    {
        parseOptions = JSParseOptionsStrict;
        serializeOptions = JSSerializeOptionNone;
        self.classDefs = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void) dealloc 
{
    [classDefs release];
    
    [super dealloc];
}


@end