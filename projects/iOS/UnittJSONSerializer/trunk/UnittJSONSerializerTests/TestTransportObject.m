//
//  TestTransportObject.m
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

#import "TestTransportObject.h"


@implementation TestTransportObject

@synthesize testInt;
@synthesize testDouble;
@synthesize testString;
@synthesize testNumber;
@synthesize testDate;
@synthesize testBool;
@synthesize testLong;
@synthesize testArray;
@synthesize testDictionary;


- (void) setTestInt:(int)aTestInt
{
    NSLog(@"Setting Test Int to %i", aTestInt);
    testInt = aTestInt;
}

- (void) setTestDouble:(double) aTestDouble
{
    NSLog(@"Setting Test Double to %f", aTestDouble);
    testDouble = aTestDouble;
}


#pragma mark Lifecycle
- (id) init
{
    self = [super init];
    if (self)
    {
    }
    return self;
}

- (id) initWithTestInt:  (int) aTestInt  testDouble: (double) aTestDouble  testString: (NSString *) aTestString  testNumber: (NSNumber *) aTestNumber  testDate: (NSDate *) aTestDate testBool: (BOOL) aTestBool testLong: (long long) aTestLong supertInt: (int) aSuperInt  readOnlyInt:(int) aReadOnlyInt
{
    self = [super initWithTestInt:aSuperInt readOnlyInt:aReadOnlyInt];
    if (self)
    {
        self.testInt = aTestInt;
        self.testDouble = aTestDouble;
        self.testString = aTestString;
        self.testNumber = aTestNumber;
        self.testDate = aTestDate;
        self.testBool = aTestBool;
        self.testLong = aTestLong;
        self.testArray = [NSArray array];
        self.testDictionary = [NSDictionary dictionary];
    }
    return self;
}

- (void) dealloc
{
    self.testString = nil;
    self.testNumber = nil;
    self.testDate = nil;
    
    [super dealloc];
}

@end
