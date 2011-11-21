//
//  JSONPropertyInfo.m
//  UnittJSONSerializer
//
//  Created by Josh Morris on 5/28/11.
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

#import "JSONPropertyInfo.h"


@implementation JSONPropertyInfo

@synthesize setter;
@synthesize getter;
@synthesize isComplex;
@synthesize dataType;
@synthesize name;
@synthesize customClass;

#pragma mark Lifecycle
- (id) init {
    self = [super init];
    if (self) {
        self.isComplex = false;
        self.dataType = JSDataTypeNSString;
    }
    return self;
}

- (void) dealloc {
    [name release];
    [super dealloc];
}

@end
