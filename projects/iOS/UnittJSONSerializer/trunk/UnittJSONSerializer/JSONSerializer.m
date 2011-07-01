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

@property (readonly) JSONDecoder* decoder;
@property (readonly) JKSerializeOptionFlags serializeOptions;
@property (retain) NSMutableDictionary* classDefs;

- (JSONPropertyInfo*) getPropertyInfoForObject:(id) aObject name:(NSString*) aName;
- (id) getPropertyValueForObject:(id) aObject name:(NSString*) aName;
- (void) setPropertyValueForObject:(id) aObject name:(NSString*) aName value:(id) aValue;
- (void) fillPropertiesForClass:(Class) aClass;
- (id) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject;
- (id) performSelectorSafelyForObject:(id) aObject selector:(SEL) aSelector argument:(id) argument;
- (NSDictionary*) objectToDictionary:(id) aObject;

@end

#pragma mark -

@implementation JSONSerializer

@synthesize decoder;
@synthesize serializeOptions;
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

- (JSONPropertyInfo*) getPropertyInfoForObject:(id)aObject name:(NSString *)aName
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
                    propertyInfo.dataType = JSDouble;
                    break;
                case 'l' : //long
                    propertyInfo.dataType = JSLong;
                    break;
                case 'i' : //int
                    propertyInfo.dataType = JSInt;
                    break;
                case 'c' : //BOOL
                    propertyInfo.dataType = JSBoolean;
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
                                propertyInfo.dataType = JSNSArray;
                            }
                            else if ([NSDictionary class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSNSDictionary;
                            }
                            else if ([NSNumber class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSNSNumber;
                            }
                            else if ([NSDate class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSNSDate;
                            }
                            else if ([NSString class] == propertyInfo.customClass)
                            {
                                propertyInfo.dataType = JSNSString;
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
// TODO: finish implementation
- (id) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject
{
    //convert each property
    return nil;
}

// TODO: finish implementation
- (void) fillObjectFromData:(NSData*) aData object:(id) aObject
{
    //create dictionary from data
    //fill object using dictionary
}

// TODO: finish implementation
- (void) fillObjectFromString:(NSString*) aData object:(id) aObject
{
    //create dictionary from string
    //fill object using dictionary
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

// TODO: finish implementation
- (NSArray*) deserializeArrayFromType:(Class) aClass data:(NSData*) aData
{
    //create array from data
    //loop through array
    //create a new object of the specified type
    //fill object using dictionary
    return nil;
}

// TODO: finish implementation
- (NSArray*) deserializeArrayFromType:(Class) aClass string:(NSString*) aData
{
    //create array from string
    //loop through array
    //create a new object of the specified type
    //fill object using dictionary
    return nil;
}


#pragma mark Serialize
// TODO: finish implementation
- (NSDictionary*) objectToDictionary:(id) aObject
{
    //convert the object to a nested dictionary using properties
    return nil;
}

// TODO: finish implementation
- (NSData*) serializeToData:(id) aObject
{
    //convert the object to a JSON data stream
    return nil;
}

// TODO: finish implementation
- (NSString*) serializeToString:(id) aObject
{
    //convert the object to a JSON string
    return nil;
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
        decoder = [JSONDecoder decoderWithParseOptions:aParseOptions];
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
        decoder = [JSONDecoder decoderWithParseOptions:JSParseOptionsStrict];
        serializeOptions = JSSerializeOptionNone;
        self.classDefs = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void) dealloc 
{
    [decoder release];
    [classDefs release];
    
    [super dealloc];
}


@end