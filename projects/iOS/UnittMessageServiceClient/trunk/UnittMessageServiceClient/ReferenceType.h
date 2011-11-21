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


#import <Foundation/Foundation.h>


@interface ReferenceType : NSObject
{
    Class objectType;
    Class arrayContentType;
    Class dictionaryKeyType;
    Class dictionaryValueType;
}

@property (nonatomic, retain) Class objectType;
@property (nonatomic, retain) Class arrayContentType;
@property (nonatomic, retain) Class dictionaryKeyType;
@property (nonatomic, retain) Class dictionaryValueType;


+ (id) referenceWithObjectType:(Class) anObjectType;

+ (id) referenceWithArrayContentType:(Class) anArrayContentType;

+ (id) referenceWithDictionaryKeyType:(Class) aDictionaryKeyType dictionaryValueType:(Class) aDictionaryValueType;

- (id) initWithObjectType:(Class) anObjectType;

- (id) initWithArrayContentType:(Class) anArrayContentType;

- (id) initWithDictionaryKeyType:(Class) aDictionaryKeyType dictionaryValueType:(Class) aDictionaryValueType;


@end