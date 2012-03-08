//
//  ServiceCallbackWithBlocks.h
//  UnittMessageServiceClient
//
//  Created by Josh Morris on 12/11/11.
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
#import "ServiceCallback.h"

typedef void (^HandlesNoResult) ();
typedef void (^HandlesResult) (id);
typedef void (^HandlesError) (NSError*);

@interface ServiceCallbackWithBlocks : NSObject<ServiceCallback> {
    HandlesNoResult onEmptyComplete;
    HandlesResult onComplete;
    HandlesResult onPartial;
    HandlesError onError;
}

@property (nonatomic, copy) void (^onEmptyComplete)();
@property (nonatomic, copy) void (^onComplete)(id);
@property (nonatomic, copy) void (^onPartial)(id);
@property (nonatomic, copy) void (^onError)(NSError*);

+ (id) callbackWithOnEmptyComplete:(HandlesNoResult) aOnComplete onError:(HandlesError) aOnError;
+ (id) callbackWithOnComplete:(HandlesResult) aOnComplete onPartial:(HandlesResult) aOnPartial onError:(HandlesError) aOnError;

- (id) initWithOnEmptyComplete:(HandlesNoResult) aOnComplete onError:(HandlesError) aOnError;
- (id) initWithOnComplete:(HandlesResult) aOnComplete onPartial:(HandlesResult) aOnPartial onError:(HandlesError) aOnError;

@end