//
//  TestTransportObject.h
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

#import <Foundation/Foundation.h>
#import "TestSuperTransportObject.h"


@interface TestTransportObject : TestSuperTransportObject
{
@private
    int testInt;
    double testDouble;
    NSString* testString;
    NSNumber* testNumber;
    NSDate* testDate;
    BOOL testBool;
}

@property (assign) int testInt;
@property (assign) double testDouble;
@property (retain) NSString* testString;
@property (retain) NSNumber* testNumber;
@property (retain) NSDate* testDate;
@property (assign) BOOL testBool;

- (id) init;
- (id) initWithTestInt: (int) aTestInt  testDouble: (double) aTestDouble  testString: (NSString *) aTestString  testNumber: (NSNumber *) aTestNumber  testDate: (NSDate *) aTestDate testBool: (BOOL) aTestBool supertInt: (int) aSuperInt  readOnlyInt:(int) aReadOnlyInt;

@end
