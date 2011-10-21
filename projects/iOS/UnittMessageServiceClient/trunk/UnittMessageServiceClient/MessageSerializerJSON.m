//
//  MessageSerializerJSON.m
//  UnittMessageServiceClient
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

#import "MessageSerializerJSON.h"

@implementation MessageSerializerJSON


@synthesize serializer;


#pragma mark - MessageSerializer
- (NSData*) serializeMessage:(ServiceMessage*) aMessage
{
    //serialize header
    NSData* headerBytes = [self.serializer serializeToDataFromObject:aMessage.header];
    
    //serialize body
    NSData* bodyBytes = [self.serializer serializeToDataFromArray:aMessage.contents];
    
    //write data
    NSMutableData* output = [NSMutableData data];
    
    //write header length
    unsigned char bytes[2];
    bytes[0] = (int)((headerBytes.length >> 8) & 0XFF);
    bytes[1] = (int)(headerBytes.length & 0XFF);
    [output appendBytes:bytes length:2];
    
    //write content type
    bytes[0] = (int)((aMessage.header.serializerType >> 8) & 0XFF);
    bytes[1] = (int)(aMessage.header.serializerType & 0XFF);
    [output appendBytes:bytes length:2];
    
    //write data
    [output appendData:headerBytes];
    [output appendData:bodyBytes];
    
    return output;
}

- (ServiceMessage*) deserializeMessage:(NSData*) aData
{
    ServiceMessage* result = [ServiceMessage message];
    
    //read header length
    NSUInteger headerLength = 0;
    unsigned char buffer[2];
    [aData getBytes:&buffer length:2];
    unsigned short len;
    memcpy(&len, &buffer[0], sizeof(len));
    headerLength = ntohs(len);
        
    //read header bytes
    NSData* headerData = [aData subdataWithRange:NSMakeRange(4, headerLength)];
    
    //read body bytes
    NSData* bodyData = [aData subdataWithRange:NSMakeRange(4 + headerLength, aData.length - 4 - headerLength)];
    
    //deserialize header
    MessageRoutingInfo* header = [self.serializer deserializeObjectFromData:headerData type:[MessageRoutingInfo class]];
    result.header = header;
    
    //deserialize body
    id body = [self.serializer deserializeObjectFromData:bodyData type:nil];
    result.contents = body;
    
    return result;
}


#pragma mark - Lifecycle
- (id) init 
{
    self = [super init];
    if (self) 
    {
        serializer = [[JSONSerializer serializerWithParseOptions:JSParseOptionsStrict serializeOptions:JSSerializeOptionNone] retain];
    }
    return self;
}

- (void)dealloc 
{
    [serializer release];
    [super dealloc];
}


@end
