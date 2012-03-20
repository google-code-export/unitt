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
- (void) testSerializeObjectWithNestedObjectsWithPrimitiveArrays
{
    long long longValue = 104;
    TestTransportObject* deserialized = [[TestTransportObject alloc] initWithTestInt:100 testDouble:102.10 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:nil testBool:NO testLong:longValue supertInt:105 readOnlyInt:106];
    TestTransportObject *child = [[TestTransportObject alloc] initWithTestInt:100 testDouble:102.10 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:[NSDate dateWithTimeIntervalSince1970:1330875712] testBool:NO testLong:longValue supertInt:105 readOnlyInt:106];
    child.testArray = [NSArray arrayWithObjects:@"one", @"two", nil];
    deserialized.testArray = [NSArray arrayWithObjects:child,[[TestTransportObject alloc] initWithTestInt:100 testDouble:102.1 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:nil testBool:NO testLong:longValue supertInt:105 readOnlyInt:106],nil];
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* output = [serializer serializeToStringFromObject:deserialized];
    NSString* expectedOutput = @"{\n"
            "  \"readOnlyInt\": 106,\n"
            "  \"superInt\": 105,\n"
            "  \"testArray\": [\n"
            "    {\n"
            "      \"readOnlyInt\": 106,\n"
            "      \"superInt\": 105,\n"
            "      \"testArray\": [\n"
            "        \"one\",\n"
            "        \"two\"\n"
            "      ],\n"
            "      \"testBool\": \"false\",\n"
            "      \"testDate\": 1330875712,\n"
            "      \"testDictionary\": {\n"
            "        \n"
            "      },\n"
            "      \"testDouble\": 102.09999999999999,\n"
            "      \"testInt\": 100,\n"
            "      \"testLong\": 104,\n"
            "      \"testNumber\": 102,\n"
            "      \"testString\": \"Blue\"\n"
            "    },\n"
            "    {\n"
            "      \"readOnlyInt\": 106,\n"
            "      \"superInt\": 105,\n"
            "      \"testArray\": [\n"
            "        \n"
            "      ],\n"
            "      \"testBool\": \"false\",\n"
            "      \"testDictionary\": {\n"
            "        \n"
            "      },\n"
            "      \"testDouble\": 102.09999999999999,\n"
            "      \"testInt\": 100,\n"
            "      \"testLong\": 104,\n"
            "      \"testNumber\": 102,\n"
            "      \"testString\": \"Blue\"\n"
            "    }\n"
            "  ],\n"
            "  \"testBool\": \"false\",\n"
            "  \"testDictionary\": {\n"
            "    \n"
            "  },\n"
            "  \"testDouble\": 102.09999999999999,\n"
            "  \"testInt\": 100,\n"
            "  \"testLong\": 104,\n"
            "  \"testNumber\": 102,\n"
            "  \"testString\": \"Blue\"\n"
            "}";
    STAssertEqualObjects(output, expectedOutput, @"Output was different than expected.");
}

- (void) testSerialize
{
    long long longValue = 104;
    TestTransportObject* deserialized = [[TestTransportObject alloc] initWithTestInt:100 testDouble:102.10 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:nil testBool:NO testLong:longValue supertInt:105 readOnlyInt:106];
    deserialized.testArray = [NSArray arrayWithObjects:[[TestTransportObject alloc] initWithTestInt:100 testDouble:102.10 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:[NSDate dateWithTimeIntervalSince1970:1330875712] testBool:NO testLong:longValue supertInt:105 readOnlyInt:106],[[TestTransportObject alloc] initWithTestInt:100 testDouble:102.1 testString:@"Blue" testNumber:[NSNumber numberWithInt:102] testDate:nil testBool:NO testLong:longValue supertInt:105 readOnlyInt:106],nil];
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* output = [serializer serializeToStringFromObject:deserialized];
    NSString* expectedOutput = @"{\n"
            "  \"readOnlyInt\": 106,\n"
            "  \"superInt\": 105,\n"
            "  \"testArray\": [\n"
            "    {\n"
            "      \"readOnlyInt\": 106,\n"
            "      \"superInt\": 105,\n"
            "      \"testArray\": [\n"
            "        \n"
            "      ],\n"
            "      \"testBool\": \"false\",\n"
            "      \"testDate\": 1330875712,\n"
            "      \"testDictionary\": {\n"
            "        \n"
            "      },\n"
            "      \"testDouble\": 102.09999999999999,\n"
            "      \"testInt\": 100,\n"
            "      \"testLong\": 104,\n"
            "      \"testNumber\": 102,\n"
            "      \"testString\": \"Blue\"\n"
            "    },\n"
            "    {\n"
            "      \"readOnlyInt\": 106,\n"
            "      \"superInt\": 105,\n"
            "      \"testArray\": [\n"
            "        \n"
            "      ],\n"
            "      \"testBool\": \"false\",\n"
            "      \"testDictionary\": {\n"
            "        \n"
            "      },\n"
            "      \"testDouble\": 102.09999999999999,\n"
            "      \"testInt\": 100,\n"
            "      \"testLong\": 104,\n"
            "      \"testNumber\": 102,\n"
            "      \"testString\": \"Blue\"\n"
            "    }\n"
            "  ],\n"
            "  \"testBool\": \"false\",\n"
            "  \"testDictionary\": {\n"
            "    \n"
            "  },\n"
            "  \"testDouble\": 102.09999999999999,\n"
            "  \"testInt\": 100,\n"
            "  \"testLong\": 104,\n"
            "  \"testNumber\": 102,\n"
            "  \"testString\": \"Blue\"\n"
            "}";
    STAssertEqualObjects(output, expectedOutput, @"Output was different than expected.");
}

- (void) testDeserializeWithStrings
{
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* serialized = @"{\"testString\":\"testStringValue\",\"testInt\":\"100\",\"superInt\":\"1001\",\"testLong\":\"103\",\"testDouble\":\"100.1\",\"testNumber\":\"102\",\"testDate\":\"1315153697500\",\"testBool\":\"true\"}";
    TestTransportObject* result = [serializer deserializeObjectFromString:serialized type:[TestTransportObject class]];
    long long longValue = 103;
    STAssertEqualObjects(result.testString, @"testStringValue", @"Did not have the correct string value: expected=%@, actual=%@",@"testStringValue", result.testString);
    STAssertEquals(result.testInt, 100, @"Did not have the correct int value");
    STAssertEquals(result.superInt, 1001, @"Did not have the correct super int value");
    STAssertTrue(result.testBool, @"Did not have the correct bool value");
    STAssertEquals(result.testDouble, 100.1, @"Did not have the correct double value");
    STAssertEquals(result.testLong, longValue, @"Did not have the correct long long value");
    STAssertNotNil(result.testDate, @"Did not have a test date value");
    STAssertEqualObjects(result.testNumber, [NSNumber numberWithInt:102], @"Did not have the correct number value");
}

- (void) testDeserializeWithValues
{
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* serialized = @"{\"testString\":\"testStringValue\",\"testInt\":100,\"superInt\":1001,\"testLong\":103,\"testDouble\":100.1,\"testNumber\":102,\"testDate\":1315153697500,\"testBool\":\"true\"}";
    TestTransportObject* result = [serializer deserializeObjectFromString:serialized type:[TestTransportObject class]];
    long long longValue = 103;
    STAssertEqualObjects(result.testString, @"testStringValue", @"Did not have the correct string value: expected=%@, actual=%@",@"testStringValue", result.testString);
    STAssertEquals(result.testInt, 100, @"Did not have the correct int value");
    STAssertEquals(result.superInt, 1001, @"Did not have the correct super int value");
    STAssertTrue(result.testBool, @"Did not have the correct bool value");
    STAssertEquals(result.testDouble, 100.1, @"Did not have the correct double value");
    STAssertEquals(result.testLong, longValue, @"Did not have the correct long long value");
    STAssertNotNil(result.testDate, @"Did not have a test date value");
    STAssertEqualObjects(result.testNumber, [NSNumber numberWithInt:102], @"Did not have the correct number value");
}


- (void) testDeserializeWithValuesInArray
{
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* serialized = @"[{\"testString\":\"testStringValue\",\"testInt\":100,\"superInt\":1001,\"testLong\":103,\"testDouble\":100.1,\"testNumber\":102,\"testDate\":1315153697500,\"testBool\":\"true\"}]";
    NSArray* results = [serializer deserializeArrayFromType:[TestTransportObject class] string:serialized];
    TestTransportObject* result = [results objectAtIndex:0];
    long long longValue = 103;
    STAssertEqualObjects(result.testString, @"testStringValue", @"Did not have the correct string value: expected=%@, actual=%@",@"testStringValue", result.testString);
    STAssertEquals(result.testInt, 100, @"Did not have the correct int value");
    STAssertEquals(result.superInt, 1001, @"Did not have the correct super int value");
    STAssertTrue(result.testBool, @"Did not have the correct bool value");
    STAssertEquals(result.testDouble, 100.1, @"Did not have the correct double value");
    STAssertEquals(result.testLong, longValue, @"Did not have the correct long long value");
    STAssertNotNil(result.testDate, @"Did not have a test date value");
    STAssertEqualObjects(result.testNumber, [NSNumber numberWithInt:102], @"Did not have the correct number value");
    [serializer release];
}

- (void) testSerializePrimitiveArray
{
    NSArray* deserialized = [NSArray arrayWithObjects:@"trouble", @"other", [NSDate dateWithTimeIntervalSince1970:1330874917], nil]; //[NSDate date]];
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* output = [serializer serializeToStringFromArray:deserialized];
    NSString* expected = @"[\n  \"trouble\",\n  \"other\",\n  1330874917\n]";
    STAssertEqualObjects(output, expected, @"Output was different than expected.");
    [serializer release];
}

- (void) testSerializeNestedPrimitiveArray
{
    NSArray* deserialized = [NSArray arrayWithObjects:@"trouble", @"other", [NSDate dateWithTimeIntervalSince1970:1330874917], [NSArray arrayWithObjects:@"one", @"two", nil], nil]; //[NSDate date]];
    JSONSerializer* serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionPretty] retain];
    NSString* output = [serializer serializeToStringFromArray:deserialized];
    NSString* expected = @"[\n"
            "  \"trouble\",\n"
            "  \"other\",\n"
            "  1330874917,\n"
            "  [\n"
            "    \"one\",\n"
            "    \"two\"\n"
            "  ]\n"
            "]";
    STAssertEqualObjects(output, expected, @"Output was different than expected.");
    [serializer release];
}

@end
