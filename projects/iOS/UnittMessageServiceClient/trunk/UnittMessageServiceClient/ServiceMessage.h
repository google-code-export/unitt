//
//  ServiceMessage.h
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

@class ServiceMessage;
@class MessageRoutingInfo;

@protocol MessageSerializer <NSObject>

- (NSData*) serializeMessage:(ServiceMessage*) aMessage;
- (ServiceMessage*) deserializeMessage:(NSData*) aData;

@end

enum 
{
    MessageResultTypeError = 0, //result is an error condition.
    MessageResultTypeCompleteSuccess = 1 //result is a complete object, do not wait for more data.
};
typedef NSUInteger MessageResultType;


@interface ServiceMessage : NSObject 
{
@private
    MessageRoutingInfo* header;
    id contents;
    id<ServiceCallback> callback;
}

@property (retain) MessageRoutingInfo* header;
@property (retain) id contents;
@property (retain) id<ServiceCallback> callback;
@property (readonly) NSString* uid;

+ (id) message;
+ (id) messageWithHeader:(MessageRoutingInfo*) aHeader contents:(id) aContents callback:(id) aCallback;
- (id) initWithHeader:(MessageRoutingInfo*) aHeader contents:(id) aContents callback:(id) aCallback;

@end
