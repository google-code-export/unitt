//
//  DefaultObjectHandler.m
//  UnittJSONSerializer
//
//  Created by Josh Morris on 9/10/11.
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

#import "ObjectHandler.h"


@interface ObjectHandler ()

- (NSDictionary*) getPropertiesForClass:(Class) aClass;

- (void) writeConcreteClass:(Class) aType dictionary:(NSDictionary*) aData;

@end


@implementation ObjectHandler


@synthesize fieldHandler;
@synthesize classDefs;


#pragma mark Deserialize
- (void) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject {
    //get class def
    NSDictionary* classDef = [self getPropertiesForClass:[aObject class]];

    //loop through dictionary, applying values
    for (NSString* key in aData) {
        JSONPropertyInfo* property = [classDef objectForKey:key];

        //if we have the property - use it to set the property - if possible
        if (property && property.setter) {
            id value = [aData objectForKey:key];

            //verify we have a value to set
            id newValue = value;

            //handle custom objects
            if ([value isKindOfClass:[NSDictionary class]]) {
                Class valueConcreteClass = [self readConcreteClassFromDictionary:value];
                if (valueConcreteClass == nil) {
                    valueConcreteClass = property.customClass;
                }
                newValue = [[[valueConcreteClass alloc] init] autorelease];
                [self fillObjectFromDictionary:value object:newValue];
            }

            //set value
            [self performSelectorSafelyForObject:aObject selector:property.setter argument:newValue type:property.dataType];
        }
    }
}


#pragma mark Serialize
- (NSDictionary*) objectToDictionary:(id) aObject {
    NSMutableDictionary* results = [NSMutableDictionary dictionary];

    //convert the object to a nested dictionary using properties
    Class type = [aObject class];
    NSDictionary* properties = [self getPropertiesForClass:type];
    if (properties) {
        for (NSString* key in properties) {
            JSONPropertyInfo* property = [properties objectForKey:key];
            if (property && property.getter) {
                id value = [self performSelectorSafelyForObject:aObject selector:property.getter argument:nil type:property.dataType];
                switch (property.dataType) {
                    case JSDataTypeCustomClass:
                        value = [self objectToDictionary:value];
                        break;
                    case JSDataTypeNSArray:
                        value = [self toArrayFromArray:value];
                        break;
                    case JSDataTypeNSDictionary:
                        value = [self toDictionaryFromDictionary:value];
                        break;
                }
                if (value != nil) {
                    [results setObject:value forKey:property.name];
                }
            }
        }
    }
    [self writeConcreteClass:type dictionary:results];

    return results;
}

- (NSArray*) toArrayFromArray:(id) aObject {
    NSMutableArray* temp = [NSMutableArray array];

    if ([aObject isKindOfClass:[NSArray class]]) {
        NSArray* source = (NSArray*) aObject;
        for (id item in source) {
            if ([item isKindOfClass:[NSArray class]]) {
                [temp addObject:[self toArrayFromArray:(NSArray*) item]];
            }
            else if ([item isKindOfClass:[NSString class]]) {
                [temp addObject:item];
            }
            else if ([item isKindOfClass:[NSDate class]]) {
                NSDate* dateItem = item;
                [temp addObject:[NSNumber numberWithLongLong:(long long) [dateItem timeIntervalSince1970]]];
            }
            else if ([item isKindOfClass:[NSNumber class]]) {
                [temp addObject:item];
            }
            else {
                [temp addObject:[self objectToDictionary:item]];
            }
        }
    }

    return [NSArray arrayWithArray:temp];
}

- (NSDictionary*) toDictionaryFromDictionary:(id) aObject {
    NSMutableDictionary* temp = [NSMutableDictionary dictionary];

    if ([aObject isKindOfClass:[NSDictionary class]]) {
        NSDictionary* source = (NSDictionary*) aObject;
        for (id key in source) {
            id value = [self objectToDictionary:[source objectForKey:key]];
            [temp setObject:value forKey:key];
        }
    }

    return [NSDictionary dictionaryWithDictionary:temp];
}


#pragma mark Property Handling
- (id) performSelectorSafelyForObject:(id) aObject selector:(SEL) aSelector argument:(id) aArgument type:(JSDataType) aType {
    if (aSelector != nil && aObject != nil) {
        NSMethodSignature* methodSig = [aObject methodSignatureForSelector:aSelector];
        if (methodSig == nil) {
            methodSig = [[aObject class] methodSignatureForSelector:aSelector];
            if (methodSig == nil) {
                return nil;
            }
        }

        NSInvocation* invocation = [NSInvocation invocationWithMethodSignature:methodSig];
        [invocation setSelector:aSelector];
        [invocation setTarget:aObject];
        if (aArgument) {
            //handle data types for setters
            [self.fieldHandler setFieldValueForInvocation:invocation datatype:aType value:aArgument];
            return nil;
        }
        else {
            //handle data types for getters
            return [self.fieldHandler getFieldValueForInvocation:invocation datatype:aType];
        }
    }

    return nil;
}

- (JSONPropertyInfo*) getPropertyInfoForObject:(id) aObject name:(NSString*) aName {
    Class objectClass = [aObject class];
    id value = [classDefs objectForKey:objectClass];

    //create missing class definition
    if (!value) {
        [self fillPropertiesForClass:[aObject class]];
        value = [classDefs objectForKey:objectClass];
    }

    //use class definition
    if (value) {
        if ([value isKindOfClass:[NSDictionary class]]) {
            //get property info
            NSDictionary* classDef = (NSDictionary*) value;
            value = [classDef objectForKey:aName];
            if (value && [value isKindOfClass:[JSONPropertyInfo class]]) {
                return (JSONPropertyInfo*) value;
            }
        }
    }

    return nil;
}

- (Class) readConcreteClassFromDictionary:(NSDictionary*) aData {
    //default implementation - do nothing
    return nil;
}

- (void) writeConcreteClass:(Class) aType dictionary:(NSDictionary*) aData {
    //default implementation - do nothing
}

- (NSDictionary*) getPropertiesForClass:(Class) aClass {
    NSDictionary* properties = [classDefs objectForKey:aClass];
    if (!properties) {
        [self fillPropertiesForClass:aClass];
    }
    return [classDefs objectForKey:aClass];
}

- (void) fillPropertiesForClass:(Class) aClass {
    NSMutableDictionary* classDef = [NSMutableDictionary dictionary];

    //traverse class hierarchy, creating property info
    Class classToFillFor = aClass;
    while (classToFillFor != nil && classToFillFor != [NSObject class]) {
        //loop through properties - creating metadata for getters/setters
        unsigned int outCount, i;
        objc_property_t* properties = class_copyPropertyList(classToFillFor, &outCount);
        for (i = 0; i < outCount; i++) {
            //grab property
            objc_property_t property = properties[i];

            if (property != NULL) {
                //init property info
                JSONPropertyInfo* propertyInfo = [[[JSONPropertyInfo alloc] init] autorelease];
                const char* propertyName = property_getName(property);
                const char* propertyAttr = property_getAttributes(property);
                propertyInfo.name = [NSString stringWithCString:propertyName encoding:[NSString defaultCStringEncoding]];

                //handle type
                BOOL isObject = false;
                switch (propertyAttr[1]) {
                    case 'd' : //double
                        propertyInfo.dataType = JSDataTypeDouble;
                        break;
                    case 'l' : //long
                    case 'q' : //long long
                        propertyInfo.dataType = JSDataTypeLong;
                        break;
                    case 'i' : //int
                    case 'I' :
                        propertyInfo.dataType = JSDataTypeInt;
                        break;
                    case 'c' : //BOOL
                        propertyInfo.dataType = JSDataTypeBoolean;
                        break;
                    case '@' : //ObjC object
                        isObject = true;
                        break;
                }
                //NSLog(@"Name=%@, Datatype=%i, Char=%c", propertyInfo.name, propertyInfo.dataType, propertyAttr[1]);

                //handle custom class - if applicable
                if (isObject) {
                    //determine custom class
                    const char* typeInitialFragment = strstr(propertyAttr, "T@\"");
                    if (typeInitialFragment != NULL) {
                        typeInitialFragment += 3;
                        const char* typeSecondFragment = strstr(typeInitialFragment, "\",");
                        if (typeSecondFragment != NULL && typeInitialFragment != typeSecondFragment) {
                            //we have a customer class - grab the name
                            int length = (int) (typeSecondFragment - typeInitialFragment);
                            char* typeName = malloc(length + 1);
                            memcpy( typeName, typeInitialFragment, length );
                            typeName[length] = '\0';

                            //get class and set type
                            propertyInfo.customClass = NSClassFromString([NSString stringWithCString:typeName encoding:[NSString defaultCStringEncoding]]);
                            if (propertyInfo.customClass) {
                                if ([NSArray class] == propertyInfo.customClass) {
                                    propertyInfo.dataType = JSDataTypeNSArray;
                                }
                                else if ([NSDictionary class] == propertyInfo.customClass) {
                                    propertyInfo.dataType = JSDataTypeNSDictionary;
                                }
                                else if ([NSNumber class] == propertyInfo.customClass) {
                                    propertyInfo.dataType = JSDataTypeNSNumber;
                                }
                                else if ([NSDate class] == propertyInfo.customClass) {
                                    propertyInfo.dataType = JSDataTypeNSDate;
                                }
                                else if ([NSString class] == propertyInfo.customClass) {
                                    propertyInfo.dataType = JSDataTypeNSString;
                                }
                            }
                            free(typeName);
                        }
                    }
                }

                //handle custom getter
                const char* custGetterInitialFragment = strstr(propertyAttr, ",G");
                if (custGetterInitialFragment != NULL) {
                    custGetterInitialFragment += 2;
                    const char* custGetterSecondFragment = strchr(custGetterInitialFragment, ',');
                    if (custGetterSecondFragment != NULL && custGetterInitialFragment != custGetterSecondFragment) {
                        //we have a customer getter, grab selector from name
                        int length = (int) (custGetterSecondFragment - custGetterInitialFragment);
                        char* custGetterName = malloc(length + 1);
                        memcpy( custGetterName, custGetterInitialFragment, length );
                        custGetterName[length] = '\0';
                        propertyInfo.getter = sel_getUid(custGetterName);
                        free(custGetterName);
                    }
                }

                //if missing getter - use default
                propertyInfo.getter = NSSelectorFromString(propertyInfo.name);

                //if not readonly - handle setter
                if (strstr(propertyAttr, ",R,") == NULL) {
                    //handle custom setter
                    const char* custSetterInitialFragment = strstr(propertyAttr, ",S");
                    if (custSetterInitialFragment != NULL) {
                        custSetterInitialFragment += 2;
                        const char* custSetterSecondFragment = strchr(custSetterInitialFragment, ',');
                        if (custSetterSecondFragment != NULL && custSetterInitialFragment != custSetterSecondFragment) {
                            //we have a customer setter, grab selector from name
                            int length = (int) (custSetterSecondFragment - custSetterInitialFragment);
                            char* custSetterName = malloc(length + 1);
                            memcpy( custSetterName, custSetterInitialFragment, length );
                            custSetterName[length] = '\0';
                            propertyInfo.setter = sel_getUid(custSetterName);
                            free(custSetterName);
                        }
                    }

                    //if missing Setter - use default
                    propertyInfo.setter = NSSelectorFromString([NSString stringWithFormat:@"set%@:", [propertyInfo.name stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:[[propertyInfo.name substringToIndex:1] capitalizedString]]]);
                }

                //push property into class descripton, if not there
                if ([classDef objectForKey:propertyInfo.name] == nil) {
                    [classDef setObject:propertyInfo forKey:propertyInfo.name];
                }
            }
        }

        classToFillFor = class_getSuperclass(classToFillFor);
    }

    //if we have properties - save them
    if ([classDef count] > 0) {
        [self.classDefs setObject:classDef forKey:aClass];
    }
}


#pragma mark Lifecycle
+ (id) objectHandlerWithFieldHandler:(FieldHandler*) aFieldHandler {
    return [[[ObjectHandler alloc] initWithWithFieldHandler:aFieldHandler] autorelease];
}

+ (id) objectHandler {
    return [[[ObjectHandler alloc] init] autorelease];
}

- (id) initWithWithFieldHandler:(FieldHandler*) aFieldHandler {
    self = [super init];
    if (self) {
        fieldHandler = [aFieldHandler retain];
        self.classDefs = [NSMutableDictionary dictionary];
    }
    return self;
}

- (id) init {
    self = [super init];
    if (self) {
        fieldHandler = [[FieldHandler alloc] init];
        self.classDefs = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void) dealloc {
    [classDefs release];
    [fieldHandler release];

    [super dealloc];
}


@end
