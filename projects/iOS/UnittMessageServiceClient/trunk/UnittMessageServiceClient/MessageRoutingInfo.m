//
//  MessageRoutingInfo.m
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

#import "MessageRoutingInfo.h"


static int currentRequestId;


@implementation MessageRoutingInfo

@synthesize sessionId;
@synthesize requestId;
@synthesize serviceName;
@synthesize methodSignature;
@synthesize timeToLiveInMillis;
@synthesize sent;
@synthesize serializerType;
@synthesize resultType;


- (NSString*) uid {
    return [NSString stringWithFormat:@"%@::%@", self.sessionId, self.requestId];
}


+ (NSString*) nextRequestId {
    return [NSString stringWithFormat:@"%i", currentRequestId++];
}


+ (id) routing {
    return [[[[self class] alloc] init] autorelease];
}

+ (id) routingWithService:(NSString*) aServiceName method:(NSString*) aMethodSignature {
    return [[[[self class] alloc] initWithService:aServiceName method:aMethodSignature] autorelease];
}

- (id) initWithService:(NSString*) aServiceName method:(NSString*) aMethodSignature {
    self = [super init];
    if (self) {
        self.serviceName = aServiceName;
        self.methodSignature = aMethodSignature;
        self.requestId = [[self class] nextRequestId];
    }
    return self;
}

- (void) dealloc {
    [sessionId release];
    [requestId release];
    [serviceName release];
    [methodSignature release];
    [sent release];

    [super dealloc];
}

@end
