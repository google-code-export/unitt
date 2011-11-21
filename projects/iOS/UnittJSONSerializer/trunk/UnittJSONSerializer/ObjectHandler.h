//
//  DefaultObjectHandler.h
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

#import <Foundation/Foundation.h>
#import "JSONSerializer.h"
#import "FieldHandler.h"
#import "JSONKit.h"
#import <Foundation/NSObjCRuntime.h>
#import <objc/runtime.h>
#import "JSONPropertyInfo.h"


@interface ObjectHandler : NSObject {
@private
    NSMutableDictionary* classDefs;
    FieldHandler* fieldHandler;
}

@property (retain) NSMutableDictionary* classDefs;
@property (retain) FieldHandler* fieldHandler;

- (JSONPropertyInfo*) getPropertyInfoForObject:(id) aObject name:(NSString*) aName;

- (void) fillPropertiesForClass:(Class) aClass;

- (void) fillObjectFromDictionary:(NSDictionary*) aData object:(id) aObject;

- (id) performSelectorSafelyForObject:(id) aObject selector:(SEL) aSelector argument:(id) argument type:(JSDataType) aType;

- (NSDictionary*) objectToDictionary:(id) aObject;

- (NSArray*) toArrayFromArray:(NSArray*) aArray;

- (NSDictionary*) toDictionaryFromDictionary:(NSDictionary*) aDictionary;


+ (id) objectHandlerWithFieldHandler:(FieldHandler*) aFieldHandler;

+ (id) objectHandler;

- (id) initWithWithFieldHandler:(FieldHandler*) aFieldHandler;

- (id) init;

@end
