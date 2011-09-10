//
//  MessageRoutingInfo.h
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
#import "ServiceMessage.h"


@interface MessageRoutingInfo : NSObject 
{
    NSString* sessionId;
    NSString* requestId;
    NSString* serviceName;
    NSString* methodSignature;
    NSUInteger timeToLiveInMillis;
    NSDate* sent;
    NSUInteger serializerType;
    MessageResultType resultType;
}

@property (copy) NSString* sessionId;
@property (copy) NSString* requestId;
@property (copy) NSString* serviceName;
@property (copy) NSString* methodSignature;
@property (assign) NSUInteger timeToLiveInMillis;
@property (copy) NSDate* sent;
@property (readonly) NSString* uid;
@property (assign) NSUInteger serializerType;
@property (assign) MessageResultType resultType;

+ (NSString*) nextRequestId;

+ (id) routing;
+ (id) routingWithService:(NSString*) aServiceName method:(NSString*) aMethodSignature;
- (id) initWithService:(NSString*) aServiceName method:(NSString*) aMethodSignature;

@end
