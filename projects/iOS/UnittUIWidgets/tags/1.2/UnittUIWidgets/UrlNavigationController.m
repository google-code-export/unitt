//
//  UrlNavigationController.m
//  UnittUIWidgets
//
//  Created by Josh Morris on 5/17/11.
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

#import "UrlNavigationController.h"


@implementation UrlNavigationController

@synthesize urlManager;

#pragma mark Properties
- (NSURL*) getUrlFromController:(UIViewController*) aViewController
{
    if (aViewController && [aViewController conformsToProtocol:@protocol(UIViewControllerHasUrl)])
    {
        return [(UIViewController<UIViewControllerHasUrl>*) aViewController currentUrl];
    }
    
    return nil;
}

- (NSURL*) topUrl
{
    return [self getUrlFromController:self.topViewController];
}        

- (NSArray*) urls
{
    NSMutableArray* results = [NSMutableArray array];
    for (UIViewController* controller in self.viewControllers) 
    {
        NSURL* url = [self getUrlFromController:controller];
        if (url)
        {
            [results addObject:url];
        }
    }
    return results;
}


#pragma mark Url Logic
- (BOOL) pushUrl:(NSURL*) aUrl animated:(BOOL) aAnimated
{
    if (self.urlManager)
    {
        UIViewController<UIViewControllerHasUrl>* controller = [self.urlManager handleUrl: aUrl];
        if (controller)
        {
            [self pushViewController:controller animated:aAnimated];
            return YES;
        }
    }
    
    return NO;
}

- (BOOL) pushUrls:(NSArray*) aUrls animated:(BOOL) aAnimated
{
    if (self.urlManager)
    {
        NSMutableArray* controllers = [NSMutableArray array];
        
        //grab controller for each url, if it exists
        for (NSURL* url in aUrls) 
        {            
            UIViewController<UIViewControllerHasUrl>* controller = [self.urlManager handleUrl: url];
            if (controller)
            {
                [controllers addObject:controller];
            }
        }
        
        //if we have a single controller - set it
        if ([controllers lastObject])
        {
            [self setViewControllers:controllers animated:aAnimated];
            return YES;
        }
    }
    
    return NO;
}


#pragma mark Lifecycle
+ (id) controllerWithUrlManager: (UrlManager*) aUrlManager
{
    return [[[UrlNavigationController alloc] initWithUrlManager:aUrlManager] autorelease];
}

- (id) initWithUrlManager: (UrlManager*) aUrlManager
{
    self = [super init];
    if (self) 
    {
        self.urlManager = aUrlManager;
    }
    return self;
}

- (void) dealloc 
{
    self.urlManager = nil;
    
    [super dealloc];
}

@end
