//
//  UnittJSONSerializerTests.m
//  UnittJSONSerializerTests
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

#import "UnittJSONSerializerTests.h"
#import "JSONPropertyInfo.h"


@implementation UnittJSONSerializerTests

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}

void printProperties(id aClass)
{
    unsigned int outCount, i;
    objc_property_t *properties = class_copyPropertyList(aClass, &outCount);
    for (i = 0; i < outCount; i++) 
    {
        objc_property_t property = properties[i];
        fprintf(stdout, "%s %s\n", property_getName(property), property_getAttributes(property));
    }
}


- (void) fillPropertiesForClass:(Class) aClass
{
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
            
            //print property
            NSLog(@"Property: name=%@, type=%i, customClass=%@", propertyInfo.name, propertyInfo.dataType, propertyInfo.customClass); //);
        }
    }
}

- (void) testExample
{
    Class nsObjectClass = [NSObject class];
    Class classToPrint = [TestTransportObject class];
    while (nsObjectClass) 
    {
        if (nsObjectClass != classToPrint)
        {
            [self fillPropertiesForClass:classToPrint];
        }
        classToPrint = [classToPrint superclass];
    }
    
    //STFail(@"Unit tests are not implemented yet in UnittJSONSerializerTests");
}

@end
