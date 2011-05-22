//
//  PrefixUrlHandler.m
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


#import "PrefixUrlHandler.h"


@implementation PrefixUrlHandler

@synthesize controllerClass;
@synthesize urlPrefix;
@synthesize useNib;
@synthesize nibName;
@synthesize nibBundle;


#pragma mark UrlHandler
- (BOOL) canHandleUrl: (NSURL*) aUrl
{
    //see if the schemes match & the specified url's resourceSpecifier starts with the url prefix's resourceSpecifier
    if (NSOrderedSame == [self.urlPrefix.scheme compare:aUrl.scheme options:NSCaseInsensitiveSearch]) 
    {
        return [aUrl.resourceSpecifier hasPrefix:urlPrefix.resourceSpecifier];
    }
    
    return NO;
}

- (UIViewController<UIViewControllerHasUrl>*) handleUrl: (NSURL*) aUrl
{
    UIViewController<UIViewControllerHasUrl>* controller = nil;
    
    //if we have a controller class and can handle the url - return a new instance of the controller class
    if (self.controllerClass && [self canHandleUrl:aUrl])
    {
        if (self.useNib)
        {
            controller = [[self.controllerClass alloc] initWithNibName:self.nibName bundle:self.nibBundle];
        }
        
        controller = [[self.controllerClass alloc] init];
        controller.currentUrl = aUrl;
    }
    
    return [controller autorelease];
}


#pragma mark Lifecycle
+ (id) handlerWithControllerClass: (Class) aControllerClass urlPrefix: (NSURL*) aUrlPrefix
{
    return [[[PrefixUrlHandler alloc] initWithControllerClass:aControllerClass urlPrefix:aUrlPrefix] autorelease];
}

+ (id) handlerWithControllerClass: (Class) aControllerClass nibName: (NSString*) aNibName nibBundle: (NSBundle*) aNibBundle urlPrefix: (NSURL*) aUrlPrefix
{
    return [[[PrefixUrlHandler alloc] initWithControllerClass:aControllerClass nibName:aNibName nibBundle:aNibBundle urlPrefix:aUrlPrefix] autorelease];
}

- (id) initWithControllerClass: (Class) aControllerClass urlPrefix: (NSURL*)  aUrlPrefix
{
    self = [super init];
    if (self) 
    {
        self.controllerClass = aControllerClass;
        self.urlPrefix = aUrlPrefix;
    }
    return self;
}

- (id) initWithControllerClass: (Class) aControllerClass nibName: (NSString*) aNibName nibBundle: (NSBundle*) aNibBundle urlPrefix: (NSURL*) aUrlPrefix
{
    self = [super init];
    if (self) 
    {
        self.controllerClass = aControllerClass;
        self.useNib = true;
        self.nibName = aNibName;
        self.nibBundle = aNibBundle;
        self.urlPrefix = aUrlPrefix;
    }
    return self;
}

- (void) dealloc 
{
    self.controllerClass = nil;
    self.urlPrefix = nil;
    
    [super dealloc];
}

@end
