//
//  JSONPropertyInfo.h
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


enum 
{
    JSDataTypeNSString = 0,
    JSDataTypeNSDate = 1,
    JSDataTypeNSNumber = 2,
    JSDataTypeNSArray = 3,
    JSDataTypeNSDictionary = 4,
    JSDataTypeInt = 5,
    JSDataTypeLong = 6,
    JSDataTypeDouble = 7,
    JSDataTypeBoolean = 8,
    JSDataTypeCustomClass = 9
};
typedef NSUInteger JSDataType;


@interface JSONPropertyInfo : NSObject 
{
@private
    NSString* name;
    SEL setter;
    SEL getter;
    BOOL isComplex;
    JSDataType dataType;
    Class customClass;
}

@property (copy) NSString* name;
@property (assign) SEL setter;
@property (assign) SEL getter;
@property (assign, getter=isComplex) BOOL isComplex;
@property (assign) JSDataType dataType;
@property (assign) Class customClass;

@end
