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

NSString* urlPrefixKey = @"UnittPersistentUrlSet";

@synthesize persistentUrlKeySuffix;

#pragma mark Properties
- (NSString*) persistentUrlKey
{
    return [NSString stringWithFormat:@"%@:%@", urlPrefixKey, self.persistentUrlKeySuffix];
}


#pragma mark Persistence
- (void) saveActiveUrls
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setValue:self.urls forKey:self.persistentUrlKey];
}


- (void) loadActiveUrls: (BOOL) aAnimated
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSArray* activeUrls = [prefs stringArrayForKey:self.persistentUrlKey];
    [self pushUrls:activeUrls animated:aAnimated];
}

#pragma mark Lifecycle
+ (id) controllerWithUrlManager: (UrlManager*) aUrlManager
{
    return [[[PersistentUrlNavigationController alloc] initWithUrlManager:aUrlManager] autorelease];
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

- (id) init 
{
    return [super init];
}

- (void) dealloc 
{
    self.urlManager = nil;
    
    [super dealloc];
}

@end
