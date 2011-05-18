//
//  UnittUIWidgetsTests.m
//  UnittUIWidgetsTests
//
//  Created by Josh Morris on 4/3/11.
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

#import "UrlManagerTests.h"
#import "UrlManager.h"
#import "PrefixUrlHandler.h"


@implementation UrlManagerTests

UrlManager* manager;
NSURL* workingUrl;
NSURL* brokenUrlScheme;
NSURL* brokenUrlResource;

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
    NSArray* handlers = [NSArray arrayWithObjects:[PrefixUrlHandler handlerWithControllerClass:[UINavigationController class] urlPrefix:[NSURL URLWithString:@"myScheme://test/now"]], nil];
    manager = [[UrlManager managerWithUrlHandlers:handlers] retain];
    workingUrl = [[NSURL URLWithString:@"myscheme://test/now/ish?almost=2"] retain];
    brokenUrlScheme = [[NSURL URLWithString:@"myscheme2://test/now/ish?almost=2"] retain];
    brokenUrlResource = [[NSURL URLWithString:@"myscheme://test/notnow/ish?almost=2"] retain];
}

- (void)tearDown
{
    // Tear-down code here.
    [manager release];
    [workingUrl release];
    [brokenUrlScheme release];
    [brokenUrlResource release];
    
    [super tearDown];
}

- (void) testConvenience
{
    STAssertTrue([manager canHandleUrl:workingUrl], @"Did not correctly detect that the url could be handled: %@", workingUrl);
    STAssertNotNil([manager handleUrl:workingUrl], @"Did not correctly handle the url: %@", workingUrl);
    STAssertFalse([manager canHandleUrl:brokenUrlScheme], @"Did not correctly detect that the url could not be handled: %@", brokenUrlScheme);
    STAssertNil([manager handleUrl:brokenUrlScheme], @"Did not correctly handle the url: %@", brokenUrlScheme);
    STAssertFalse([manager canHandleUrl:brokenUrlResource], @"Did not correctly detect that the url could not be handled: %@", brokenUrlResource);
    STAssertNil([manager handleUrl:brokenUrlResource], @"Did not correctly handle the url: %@", brokenUrlResource);
}

@end
