//
//  MessageServiceTransport.m
//  UnittMessageServiceClient
//
//  Created by Josh Morris on 6/29/11.
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

#import "MessageServiceTransport.h"


@implementation MessageServiceTransport

@synthesize client;


#pragma mark Properties
- (NSUInteger) maxPayloadSize
{
    if (websocket)
    {
        return websocket.maxPayloadSize;
    }
    
    return 0;
}

- (NSTimeInterval) timeout
{
    if (websocket)
    {
        return websocket.timeout;
    }
    
    return 0;
}

-(NSURL*) url
{
    if (websocket)
    {
        return websocket.url;
    }
    
    return nil;
}

- (NSDictionary*) tlsSettings
{
    if (websocket)
    {
        return websocket.tlsSettings;
    }
    
    return nil;
}

-(BOOL) verifyHandshake
{
    if (websocket)
    {
        return websocket.verifyHandshake;
    }
    
    return YES;
}


#pragma mark Transport
- (void) send:(NSData *)aData
{
    if (websocket)
    {
        [websocket sendBinary:aData];
    }
}

- (void) open
{
    if (websocket)
    {
        [websocket open];
    }
}

- (void) close
{
    if (websocket)
    {
        [websocket close];
    }    
}


#pragma mark WebSocket Delegate
- (void) didOpen
{
    if (self.client)
    {
        [self.client transportDidOpen];
    }
}

- (void) didClose:(NSUInteger) aStatusCode message:(NSString*) aMessage error:(NSError*) aError
{
    if (self.client)
    {
        [self.client transportDidClose:aError];
    }
}

// TODO: figure out what to do here
- (void) didReceiveError: (NSError*) aError
{
}

- (void) didReceiveTextMessage: (NSString*) aMessage
{
    //do nothing, we only use binary messages
}

- (void) didReceiveBinaryMessage: (NSData*) aMessage
{
    if (self.client)
    {
        [self.client responseFromService:aMessage];
    }
}


#pragma mark Lifecycle
+ (id) transportWithUrlString:(NSString*) aUrl
{
    return [[[[self class] alloc] initWithUrlString:aUrl] autorelease];
}

+ (id) transportWithUrlString:(NSString*) aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake
{
    return [[[[self class] alloc] initWithUrlString:aUrl timeout:aTimeout maxPayloadSize:aMaxPayloadSize tlsSettings:aTlsSettings verifyHandshake:aVerifyHandshake] autorelease];
}

- (id) initWithUrlString:(NSString*) aUrl
{    
    self = [super init];
    if (self)
    {
        websocket = [self createWebSocketWithUrlString:aUrl];
    }
    return self;
}

- (id) initWithUrlString:(NSString*) aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake
{
    self = [super init];
    if (self)
    {
        websocket = [self createWebSocketWithUrlString:aUrl timeout:aTimeout maxPayloadSize:aMaxPayloadSize tlsSettings:aTlsSettings verifyHandshake:aVerifyHandshake];
    }
    return self;
}

- (WebSocket07*) createWebSocketWithUrlString:(NSString*) aUrl
{
    return [self createWebSocketWithUrlString:aUrl timeout:30 maxPayloadSize:32*1024 tlsSettings:nil verifyHandshake:YES];
}
            
- (WebSocket07*) createWebSocketWithUrlString:(NSString *)aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake
{
    WebSocket07* result = [WebSocket07 webSocketWithURLString:aUrl delegate:self origin:nil protocols:[NSArray arrayWithObject:@"unitt-message-service"] tlsSettings:aTlsSettings verifyHandshake:aVerifyHandshake];
    result.maxPayloadSize = aMaxPayloadSize;
    result.timeout = aTimeout;
    return [result retain];
}

@end
