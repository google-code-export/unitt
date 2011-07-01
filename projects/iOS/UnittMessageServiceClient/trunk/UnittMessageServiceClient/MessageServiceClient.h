//
//  MessageServiceClient.h
//  UnittMessageServiceClient
//
//  Created by Josh Morris on 6/11/11.
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
#import "MessageRoutingInfo.h"
#import "ServiceMessage.h"
#import "ServiceCallback.h"
#import "MessageServiceTransport.h"
#import "MutableQueue.h"

@class MessageServiceTransport;

@interface MessageServiceClient : NSObject
{
    int requestCounter;
    NSString* sessionId;
    NSUInteger timeToLiveInMillis;
    MutableQueue* queuedRequests;
    NSMutableDictionary* pendingRequests;
    MessageServiceTransport* transport;
    id<MessageSerializer> serializer;
    BOOL isOpen;
}

@property (nonatomic,copy) NSString* sessionId;
@property (assign) NSUInteger timeToLiveInMillis;
@property (retain) MessageServiceTransport* transport;
@property (readonly) BOOL hasPendingRequests;
@property (retain) id<MessageSerializer> serializer;
@property (nonatomic,readonly) BOOL isOpen;

- (void) requestForService:(NSString*) aServiceName methodSignature:(NSString*) aMethodSignature parameters:(NSArray*) aParameters callback:(id<ServiceCallback>) aCallback;
- (void) responseFromService:(NSData*) aMessageData;
- (void) transportDidClose: (NSError*) aError;
- (void) transportDidOpen;
- (void) handleCallback:(id<ServiceCallback>) aCallback result:(id) aResult;

extern NSString *const MessageServiceException;

@end