//
//  ServiceMessage.m
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

#import "ServiceMessage.h"


@implementation ServiceMessage

@synthesize header;
@synthesize contents;
@synthesize callback;


- (NSString*) uid
{
    if (self.header)
    {
        return self.header.uid;
    }
    
    return nil;
}


+ (id) message
{
    return [[[[self class] alloc] init] autorelease];
}

+ (id) messageWithHeader:(MessageRoutingInfo*) aHeader contents:(id) aContents callback:(id) aCallback
{
    return [[[[self class] alloc] initWithHeader:aHeader contents:aContents callback:aCallback] autorelease];
}

- (id) initWithHeader:(MessageRoutingInfo*) aHeader contents:(id) aContents callback:(id) aCallback
{
    self = [super init];
    if (self)
    {
        self.header = aHeader;
        self.contents = aContents;
        self.callback = aCallback;
    }
    return self;
}

@end
