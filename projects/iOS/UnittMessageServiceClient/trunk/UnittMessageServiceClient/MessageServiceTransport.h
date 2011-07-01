//
//  MessageServiceTransport.h
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

#import <Foundation/Foundation.h>
#import "WebSocket07.h"
#import "MessageServiceClient.h"

@class MessageServiceClient;


@interface MessageServiceTransport : NSObject <WebSocket07Delegate>
{
@private
    WebSocket07* websocket;
    MessageServiceClient* client;
}

/**
 * Client that manages and handles all requests and response callbacks.
 */
@property(retain) MessageServiceClient* client;

/**
 * Max size of the payload. Any messages larger will be sent as fragments.
 **/
@property(nonatomic,readonly) NSUInteger maxPayloadSize;

/**
 * Timeout used for sending messages, not establishing the socket connection. A
 * value of -1 will result in no timeouts being applied.
 **/
@property(nonatomic,readonly) NSTimeInterval timeout;

/**
 * URL of the websocket
 **/
@property(nonatomic,readonly) NSURL* url;

/**
 * Settings for securing the connection using SSL/TLS.
 * 
 * The possible keys and values for the TLS settings are well documented.
 * Some possible keys are:
 * - kCFStreamSSLLevel
 * - kCFStreamSSLAllowsExpiredCertificates
 * - kCFStreamSSLAllowsExpiredRoots
 * - kCFStreamSSLAllowsAnyRoot
 * - kCFStreamSSLValidatesCertificateChain
 * - kCFStreamSSLPeerName
 * - kCFStreamSSLCertificates
 * - kCFStreamSSLIsServer
 * 
 * Please refer to Apple's documentation for associated values, as well as other possible keys.
 * 
 * If the value is nil or an empty dictionary, then the websocket cannot be secured.
 **/
@property(nonatomic,readonly) NSDictionary* tlsSettings;

/**
 * True if the client should verify the handshake values sent by the server. Since many of
 * the web socket servers may not have been updated to support this, set to false to ignore
 * and simply accept the connection to the server.
 **/
@property(nonatomic,readonly) BOOL verifyHandshake;


- (void) open;
- (void) close;


- (void) send:(NSData*) aData;


+ (id) transportWithUrlString:(NSString*) aUrl;
+ (id) transportWithUrlString:(NSString*) aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake;
- (id) initWithUrlString:(NSString*) aUrl;
- (id) initWithUrlString:(NSString*) aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake;

/**
 * Override to customize how your websocket is created
 */
- (WebSocket07*) createWebSocketWithUrlString:(NSString*) aUrl;
- (WebSocket07*) createWebSocketWithUrlString:(NSString*) aUrl timeout:(NSTimeInterval) aTimeout maxPayloadSize:(NSUInteger) aMaxPayloadSize tlsSettings:(NSDictionary*) aTlsSettings verifyHandshake:(BOOL) aVerifyHandshake;

@end
