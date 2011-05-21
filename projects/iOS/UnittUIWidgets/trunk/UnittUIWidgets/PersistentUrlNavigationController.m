//
//  PersistentUrlNavigationController.m
//  UnittUIWidgets
//
//  Created by Josh Morris on 5/19/11.
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

#import "PersistentUrlNavigationController.h"


@implementation PersistentUrlNavigationController


@synthesize persistentUrlKey;


#pragma mark Persistence
- (void) saveActiveUrls
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSArray* urlsToSave = self.urls;
    NSMutableArray* urlsWeWillSave = [NSMutableArray array];
    if (urlsToSave && [urlsToSave lastObject])
    {
        if (!self.persistentUrlKey)
        {
            self.persistentUrlKey = @"DefaultPersistentUrlSet";
        }
        for (NSURL* urlToSave in urlsToSave) 
        {
            [urlsWeWillSave addObject:[urlToSave absoluteString]];
        }
        [prefs setValue:urlsWeWillSave forKey:self.persistentUrlKey];
    }
}

- (void) loadActiveUrls: (BOOL) aAnimated
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    if (!self.persistentUrlKey)
    {
        self.persistentUrlKey = @"DefaultPersistentUrlSet";
    }
    NSArray* urlsToLoad = [prefs arrayForKey:self.persistentUrlKey];
    if (urlsToLoad && [urlsToLoad lastObject])
    {
        NSMutableArray* activeUrls = [NSMutableArray array];
        for (NSString* urlValue in urlsToLoad) 
        {
            [activeUrls addObject:[NSURL URLWithString:urlValue]];
        }
        [self pushUrls:activeUrls animated:aAnimated];
    }
}


#pragma mark Lifecycle
+ (id) controllerWithUrlManager: (UrlManager*) aUrlManager urlKey: (NSString*) aUrlKey
{
    return [[[PersistentUrlNavigationController alloc] initWithUrlManager:aUrlManager urlKey:aUrlKey] autorelease];
}

- (id) initWithUrlManager: (UrlManager*) aUrlManager urlKey: (NSString*) aUrlKey
{
    self = [super init];
    if (self) 
    {
        self.urlManager = aUrlManager;
        if (aUrlKey) 
        {
            self.persistentUrlKey = aUrlKey;
        }
    }
    return self;
}

- (void) dealloc 
{
    self.persistentUrlKey = nil;
    
    [super dealloc];
}

@end
