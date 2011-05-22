//
//  HomeModel.h
//  UnittUIWidgets
//
//  Created by Josh Morris on 4/22/11.
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

#import "UrlManager.h"


@implementation UrlManager

@synthesize urlHandlers;

#pragma mark UrlHandler Convenience Methods
- (BOOL) canHandleUrl: (NSURL*) aUrl
{
    //loop through handlers, if one can handle it - we are done
    for (id<UrlHandler> handler in self.urlHandlers) 
    {
        if ([handler canHandleUrl:aUrl])
        {
            return true;
        }
    }
    
    return false;
}

- (UIViewController<UIViewControllerHasUrl>*) handleUrl: (NSURL*) aUrl
{
    //loop through handlers, if one can handle it - return the results
    for (id<UrlHandler> handler in self.urlHandlers) 
    {
        if ([handler canHandleUrl:aUrl])
        {
            return [handler handleUrl:aUrl];
        }
    }
    
    return nil;
}

#pragma mark Lifecycle
+ (id) managerWithUrlHandlers: (NSArray*) aUrlHandlers
{
    return [[[UrlManager alloc] initWithUrlHandlers:aUrlHandlers] autorelease];
}

- (id) initWithUrlHandlers: (NSArray*) aUrlHandlers
{
    self = [super init];
    if (self) 
    {
        self.urlHandlers = [NSMutableArray arrayWithArray:aUrlHandlers];
    }
    return self;
}

- (id) init 
{
    self = [super init];
    if (self) 
    {
        self.urlHandlers = [NSMutableArray array];
    }
    return self;
}

- (void) dealloc 
{
    self.urlHandlers = nil;
    
    [super dealloc];
}

@end
