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
@synthesize config;


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
    NSLog(@"Error connecting: %@", aError.localizedDescription);
}

- (void) didReceiveTextMessage: (NSString*) aMessage
{
    //do nothing, we only use binary messages
}

- (void) didReceiveBinaryMessage: (NSData*) aMessage
{
    if (self.client)
    {
        NSLog(@"Received message from service!");
        [self.client responseFromService:aMessage];
    }
}

- (void) didSendPong:(NSData*) aMessage
{
    //do nothing
}


#pragma mark Lifecycle
+ (id) transportWithConfig:(WebSocketConnectConfig*) aConfig
{
    return [[[[self class] alloc] initWithConfig:aConfig] autorelease];
}

- (id) initWithConfig:(WebSocketConnectConfig*) aConfig
{    
    self = [super init];
    if (self)
    {
        websocket = [self createWebSocketWithConfig:aConfig];
    }
    return self;
}

- (WebSocket*) createWebSocketWithConfig:(WebSocketConnectConfig*) aConfig
{
    return [[WebSocket webSocketWithConfig:aConfig delegate:self] retain];
}

- (void) dealloc 
{
    [config release];
    [websocket release];
    [super dealloc];
}

@end
