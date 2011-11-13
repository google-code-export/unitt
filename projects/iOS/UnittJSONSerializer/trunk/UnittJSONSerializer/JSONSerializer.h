//
//  JSONSerializer.h
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
#import "JSONKit.h"
#import <Foundation/NSObjCRuntime.h>
#import <objc/runtime.h>
#import "JSONPropertyInfo.h"
#import "ObjectHandler.h"
#import "FieldHandler.h"

enum 
{
    JSParseOptionsStrict = 0, //default
    JSParseOptionComments = (1 << 0), //allow comments (//, /*...*/)
    JSParseOptionUnicodeNewlines = (1 << 1), //allow unicode recommended newlines,
                                             //(?:\r\n|[\n\v\f\r\x85\p{Zl}\p{Zp}])
    JSParseOptionLooseUnicode = (1 << 2), //allows JSON with malformed Unicode to be
                                          //parsed without reporting an error. Any 
                                          //malformed Unicode is replaced with \uFFFD, 
                                          //or "REPLACEMENT CHARACTER"
    JSParseOptionPermitTextAfterValidJSON = (1 << 3) //don't throw an error if there 
                                                     //is text after the JSON
};
typedef NSUInteger JSParseOptionFlags;

enum 
{
    JSSerializeOptionNone = 0, //default
    JSSerializeOptionPretty = (1 << 0), //more useful for debugging
    JSSerializeOptionEscapeUnicode  = (1 << 1) //encode Unicode code points that can
                                               //be encoded as a single UTF16 code unit
                                               //as \uXXXX, and will encode Unicode code
                                               //points that require UTF16 surrogate 
                                               //pairs as \uhigh\ulow
};
typedef NSUInteger JSSerializeOptionFlags;


@class ObjectHandler;


/**
 * JSONSerializer uses JSONKit to convert to/from JSON. For more information, 
 * see https://github.com/johnezang/JSONKit
 */
@interface JSONSerializer : NSObject 
{
@private
    JKParseOptionFlags parseOptions;
    JKSerializeOptionFlags serializeOptions;
    ObjectHandler* objectHandler;
}


/**
 * Custom serializer that can handle converting objects to/from the values in the JSON data.
 */
@property (retain) ObjectHandler* objectHandler;


/**
 * Takes a JSON data stream and fills the specified object's properties with
 * its values. Only Objective-C properties of the class will be populated with the
 * appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (void) fillObjectFromData:(NSData*) aData object:(id) aObject;

/**
 * Takes a JSON unicode string and fills the specified object's properties with
 * its values. Only Objective-C properties of the class will be populated with the
 * appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (void) fillObjectFromString:(NSString*) aData object:(id) aObject;

/**
 * Takes a JSON data stream and deserializes it into an object of the specified 
 * type. Only Objective-C properties of the class will be populated with the
 * appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (id) deserializeObjectFromData:(NSData*) aData type:(Class) aClass;

/**
 * Takes a JSON unicode string and deserializes it into an object of the specified 
 * type. Only Objective-C properties of the class will be populated with the
 * appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (id) deserializeObjectFromString:(NSString*) aData type:(Class) aClass;

/**
 * Takes a JSON data stream and deserializes it into an array of objects of the 
 * specified type. Only Objective-C properties of the class will be populated with 
 * the appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (NSArray*) deserializeArrayFromType:(NSArray*) aClass data:(NSData*) aData;

/**
 * Takes a JSON unicode string and deserializes it into an array of objects of the 
 * specified type. Only Objective-C properties of the class will be populated with 
 * the appropriate data whose property names are the unicode equivalent of the keys
 * in the JSON data. This is a deep deserialization process that does not handle
 * circular references.
 */
- (NSArray*) deserializeArrayFromType:(NSArray*) aClass string:(NSString*) aData;

/**
 * Serializes the specified object into a JSON data stream.
 */
- (NSData*) serializeToDataFromObject:(id) aObject;

/**
 * Serializes the specified object into a JSON unicode (default UTF8) string.
 */
- (NSString*) serializeToStringFromObject:(id) aObject;

/**
 * Serializes the specified array into a JSON data stream.
 */
- (NSData*) serializeToDataFromArray:(id) aObject;

/**
 * Serializes the specified array into a JSON unicode (default UTF8) string.
 */
- (NSString*) serializeToStringFromArray:(id) aObject;


+ (id) serializerWithParseOptions:(JSParseOptionFlags) aParseOptions serializeOptions:(JSSerializeOptionFlags) aSerializeOptions;
+ (id) serializerWithParseOptions:(JSParseOptionFlags) aParseOptions serializeOptions:(JSSerializeOptionFlags) aSerializeOptions objectHandler:(ObjectHandler*) aObjectHandler;
- (id) initWithParseOptions:(JSParseOptionFlags) aParseOptions serializeOptions:(JSSerializeOptionFlags) aSerializeOptions;
- (id) initWithParseOptions:(JSParseOptionFlags) aParseOptions serializeOptions:(JSSerializeOptionFlags) aSerializeOptions objectHandler:(ObjectHandler*) aObjectHandler;

@end
