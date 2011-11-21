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


#import "ReferenceType.h"


@implementation ReferenceType {

}
@synthesize objectType;
@synthesize arrayContentType;
@synthesize dictionaryKeyType;
@synthesize dictionaryValueType;

+ (id) referenceWithObjectType:(Class) aObjectType {
    return [[[[self class] alloc] initWithObjectType:aObjectType] autorelease];
}

+ (id) referenceWithArrayContentType:(Class) aArrayContentType {
    return [[[[self class] alloc] initWithArrayContentType:aArrayContentType] autorelease];
}

+ (id) referenceWithDictionaryKeyType:(Class) aDictionaryKeyType dictionaryValueType:(Class) aDictionaryValueType {
    return [[[[self class] alloc] initWithDictionaryKeyType:aDictionaryKeyType dictionaryValueType:aDictionaryValueType] autorelease];
}

- (id) initWithDictionaryKeyType:(Class) aDictionaryKeyType dictionaryValueType:(Class) aDictionaryValueType {
    self = [super init];
    if (self) {
        dictionaryKeyType = [aDictionaryKeyType retain];
        dictionaryValueType = [aDictionaryValueType retain];
    }

    return self;
}

- (id) initWithArrayContentType:(Class) aArrayContentType {
    self = [super init];
    if (self) {
        arrayContentType = [aArrayContentType retain];
    }

    return self;
}

- (id) initWithObjectType:(Class) aObjectType {
    self = [super init];
    if (self) {
        objectType = [aObjectType retain];
    }

    return self;
}

- (void) dealloc {
    [objectType release];
    [arrayContentType release];
    [dictionaryKeyType release];
    [dictionaryValueType release];
    [super dealloc];
}


@end