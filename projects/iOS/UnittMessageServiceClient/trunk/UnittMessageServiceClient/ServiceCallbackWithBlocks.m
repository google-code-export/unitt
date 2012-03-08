//
//  ServiceCallbackWithBlocks.m
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


#import "ServiceCallbackWithBlocks.h"


@implementation ServiceCallbackWithBlocks
@synthesize onComplete;
@synthesize onPartial;
@synthesize onError;
@synthesize onEmptyComplete;

#pragma mark - Callback logic
- (void)onError:(NSError *)aError {
    self.onError(aError);
}

- (void)onComplete:(id)aResult {
    if (aResult) {
        self.onComplete(aResult);
    }
    else if (self.onEmptyComplete) {
        self.onEmptyComplete();
    }

    //default to oncomplete
    self.onComplete(aResult);
}

- (void)onPartial:(id)aResult {
    self.onPartial(aResult);
}


#pragma mark - Lifecycle
+ (id) callbackWithOnComplete:(HandlesResult) aOnComplete onPartial:(HandlesResult) aOnPartial onError:(HandlesError) aOnError {
    return [[[[self class] alloc] initWithOnComplete:aOnComplete onPartial:aOnPartial onError:aOnError] autorelease];
}

+ (id) callbackWithOnEmptyComplete:(HandlesNoResult) aOnComplete onError:(HandlesError) aOnError {
    return [[[[self class] alloc] initWithOnEmptyComplete:aOnComplete onError:aOnError] autorelease];
}

- (id) initWithOnComplete:(HandlesResult) anOnComplete onPartial:(HandlesResult) anOnPartial onError:(HandlesError) anOnError {
    self = [super init];
    if (self) {
        onComplete = [anOnComplete copy];
        onPartial = [anOnPartial copy];
        onError = [anOnError copy];
    }

    return self;
}

- (id) initWithOnEmptyComplete:(HandlesNoResult) anOnComplete onError:(HandlesError) anOnError {
    self = [super init];
    if (self) {
        onEmptyComplete = [anOnComplete copy];
        onError = [anOnError copy];
    }

    return self;
}

- (void) dealloc {
    [onEmptyComplete release];
    [onComplete release];
    [onPartial release];
    [onError release];
    [super dealloc];
}
@end