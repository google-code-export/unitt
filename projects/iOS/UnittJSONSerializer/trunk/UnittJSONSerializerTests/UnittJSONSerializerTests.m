//
//  JsonSerializerTests.m
//  UnittMessageServiceClient
//
//  Created by Josh Morris on 9/4/11.
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

@implementation UnittJSONSerializerTests


#pragma mark - Tests
- (void) testSerialize
{
    
}

- (void) testDeserialize
{
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* serialized = @"{\"testString\":\"testStringValue\",\"testInt\":\"100\",\"testDouble\":\"100.1\",\"testNumber\":\"102\",\"testDate\":[\"java.util.Date\",1315153697500],\"testBool\":\"true\"}";
    TestTransportObject* result = [serializer deserializeObjectFromString:serialized type:[TestTransportObject class]];
    STAssertEqualObjects(result.testString, @"testStringValue", @"Did not have the correct method signature: expected=%@, actual=%@",@"testStringValue", result.testString);
}

@end
