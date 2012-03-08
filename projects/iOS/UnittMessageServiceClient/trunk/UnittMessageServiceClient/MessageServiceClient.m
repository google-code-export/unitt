//
//  MessageServiceClient.m
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

#import "MessageServiceClient.h"
#import "PendingRequest.h"


@implementation MessageServiceClient

@synthesize sessionId;
@synthesize transport;
@synthesize timeToLiveInMillis;
@synthesize serializer;
@synthesize isOpen;

NSString* const UnittMessageServiceException = @"MessageServiceClientException";


#pragma mark Properties
- (void) setSessionId:(NSString*) aSessionId {
    if (sessionId != aSessionId) {
        [sessionId release];
        sessionId = [aSessionId copy];
    }
}

- (BOOL) hasPendingRequests {
    if (pendingRequests) {
        return [pendingRequests count] > 0;
    }

    return false;
}


#pragma mark Service Logic
- (void) requestForService:(NSString*) aServiceName methodSignature:(NSString*) aMethodSignature parameters:(NSArray*) aParameters returnType:(ReferenceType*) aReturnType callback:(id <ServiceCallback>) aCallback {
    //if we do not have a transport or a serializer, raise exception
    if (self.serializer) {
        if (self.transport) {
            //create message routing info
            MessageRoutingInfo* header = [MessageRoutingInfo routingWithService:aServiceName method:aMethodSignature];
            header.timeToLiveInMillis = self.timeToLiveInMillis;
            header.sessionId = self.sessionId;
            header.serializerType = SerializerTypeJson;
            ServiceMessage* message = [ServiceMessage messageWithHeader:header contents:aParameters];
            PendingRequest* request = [PendingRequest requestWithReturnType:aReturnType message:message callback:aCallback];

            //add to pending requests (keyed by request id)
            [pendingRequests setObject:request forKey:header.uid];

            //send message
            if (self.isOpen) {
                //serialize into actual message and send
                NSData* messageData = [self.serializer serializeMessage:message];
//                NSLog(@"Requesting:\n%@", [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding]);
                [self.transport send:messageData];
            }
            else {
                //queue message
                [queuedRequests enqueue:message];
            }
        }
        else {
            [NSException raise:UnittMessageServiceException format:@"Missing Transport"];
        }
    }
    else {
        [NSException raise:UnittMessageServiceException format:@"Missing Serializer"];
    }
}

- (void) responseFromService:(NSData*) aMessageData {
    if (self.serializer) {
//        NSLog(@"Received response from service:\n%@", [[NSString alloc] initWithData:aMessageData encoding:NSUTF8StringEncoding]);
        //grab header to determine uid
        MessageRoutingInfo* header = [self.serializer deserializeMessageHeader:aMessageData];

        //grab callback for message
//        NSLog(@"Looking for pending request (%@) in %i requests.", header.uid, pendingRequests.count);
        PendingRequest* request = [pendingRequests objectForKey:header.uid];
        if (request) {
            //deserialize back into message
//            NSLog(@"Deserializing: \n%@", [[NSString alloc] initWithData:aMessageData encoding:NSUTF8StringEncoding]);
            ServiceMessage* messageResponse = [self.serializer deserializeMessage:aMessageData routing:header returnType:request.returnType];

            //leave on queue if it is not complete
            if (messageResponse.header.resultType == MessageResultTypeCompleteSuccess || messageResponse.header.resultType == MessageResultTypeError)
            {
                //retain/autorelease & remove from pending
                [[request retain] autorelease];
                [pendingRequests removeObjectForKey:header.uid];
            }

            //call appropriate method on the callback
            id <ServiceCallback> callback = request.callback;
            if (callback) {
                [self handleCallback:callback result:messageResponse];
            }
        }
        else {
            NSLog(@"Did not find pending request.");
        }
    }
}

// TODO: handle errors, retries, and failing out
- (void) transportDidClose:(NSError*) aError {
    isOpen = NO;
    //if we have closed, handle errors and retry
    //if we can't retry anymore, fail out and call errors on all callbacks
}

- (void) transportDidOpen {
    //send all queued messages
    ServiceMessage* message = [queuedRequests dequeue];
    while (message) {
//        NSLog(@"Serialized message:\n%@", [[NSString alloc] initWithData:[self.serializer serializeMessage:message] encoding:NSUTF8StringEncoding]);
        //serialize into actual message and send
        [self.transport send:[self.serializer serializeMessage:message]];

        //grab next on queue
        message = [queuedRequests dequeue];
    }

    isOpen = YES;
}

- (void) handleCallback:(id<ServiceCallback>) aCallback result:(ServiceMessage*) aResult
{
    switch (aResult.header.resultType) {
        case MessageResultTypeError:
            [aCallback onError:(NSError*) aResult.contents];
        case MessageResultTypeCompleteSuccess:
            [aCallback onComplete:aResult.contents];
        case MessageResultTypePartialSuccess:
            [aCallback onPartial:aResult.contents];
            break;
    }
}

// TODO: figure out threading

#pragma mark Lifecycle
- (id) init {
    self = [super init];
    if (self) {
        isOpen = NO;
        timeToLiveInMillis = 30 * 1000;
        pendingRequests = [[NSMutableDictionary alloc] init];
        queuedRequests = [[MutableQueue alloc] init];
    }
    return self;
}

- (void) dealloc {
    [sessionId release];
    [pendingRequests release];
    [transport release];

    [serializer release];
    [super dealloc];
}


@end
