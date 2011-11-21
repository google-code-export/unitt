//
//  ServiceProxy.m
//  UnittMessageServiceClient
//
//  Created by Josh Morris on 10/1/11.
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

#import "ServiceProxy.h"

@implementation ServiceProxy

@synthesize client;
@synthesize serviceName;

+ (id) proxy {
    return [[[[self class] alloc] init] autorelease];
}

+ (id) proxyWithClient:(MessageServiceClient*) aClient {

    return [[[[self class] alloc] initWithClient:aClient] autorelease];
}

- (id) initWithClient:(MessageServiceClient*) aClient {
    self = [super init];
    if (self) {
        self.client = aClient;
    }
    return self;
}

- (void) dealloc {
    [client release];
    [serviceName release];
    [super dealloc];
}

@end
