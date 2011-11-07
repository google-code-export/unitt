//
//  HomeView.m
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

#import "HomeViewController.h"
#import "IconModel.h"
#import "UrlNavigationController.h"


@interface HomeViewController()
@property (readonly) HomeModel* model;
@property (readonly) HomeView* homeView;
@property (assign) BOOL isDirty;
@end

@implementation HomeViewController

@synthesize isDirty;


#pragma mark Properties
- (HomeModel*) model
{
    if (!model) 
    {
        model= [[HomeModel alloc] init];
    }
    
    return model;
}

- (HomeView*) homeView
{
    return (HomeView*) self.view;
}


#pragma mark Model
- (void) handleModelChange
{
    //tell view to reload if needed, else mark dirty to draw later
    if (self.view && self.view.window)
    {
        [self.homeView setNeedsLayout];
        self.isDirty = NO;
    }
    else
    {
        self.isDirty = YES;
    }
}

- (void) addItem: (NSString*) aKey controller: (UIViewController*) aController icon: (UIImage*) aIcon label: (NSString*) aLabelText
{        
    //add to model
    [self.model addItem:aKey controller:aController icon:aIcon label:aLabelText];
    [self handleModelChange];
}

- (void) addItem: (NSString*) aKey url: (NSURL*) aUrl icon: (UIImage*) aIcon label: (NSString*) aLabelText
{        
    //add to model
    [self.model addItem:aKey url:aUrl icon:aIcon label:aLabelText];
    [self handleModelChange];
}

- (void) removeItem: (NSString*) aKey
{
    //add to model
    [self.model removeItem:aKey];
    [self handleModelChange];
}


#pragma mark Controller
- (void) didSelectItem: (UIView*) aSender
{
    if (self.navigationController)
    {
        NSArray* items = self.model.iconModels;
        if (items.count > aSender.tag)
        {
            IconModel* item = [items objectAtIndex:aSender.tag];
            if (item)
            {
                @try
                {
                    //if it has a url and we can handle it, use that
                    if (item.url)
                    {
                        if ([self.navigationController isKindOfClass:[UrlNavigationController class]])
                        {
                            UrlNavigationController* urlNav = (UrlNavigationController*) self.navigationController;
                            [urlNav pushUrl:item.url animated:YES];
                            return;
                        }
                    }
                    
                    //push controller
                    [self.navigationController pushViewController:item.viewController animated:YES];
                }
                @catch (NSException* e)
                {
                    NSLog(@"Missing view controller");
                    UIAlertView* view = [[[UIAlertView alloc] initWithTitle:@"Missing Item" message:@"Could not find item to show" delegate:nil cancelButtonTitle:nil otherButtonTitles:nil] autorelease];
                    [view show];
                }
            }
        }
    }
}

- (NSArray*) getHomeItems
{
    return self.model.iconModels;
}


#pragma mark Lifecycle
- (void) loadView
{
    HomeView* myView = [[HomeView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] delegate:self datasource:self];
    self.view = myView;
    [myView release];
    
    // defaults 
    myView.startColor = [UIColor whiteColor];
    myView.endColor = [UIColor blackColor];
    myView.itemSize = CGSizeMake(92, 120);
    myView.margin = CGSizeMake(10, 10);
    myView.useToolbar = NO;
    myView.toolbarHeight = 32;
    myView.showShadow = YES;
    myView.useToolbar = self.toolbarItems !=nil;


}

- (void) viewWillAppear: (BOOL) aAnimated
{
    if (self.isDirty)
    {
        self.isDirty = NO;
    }
    [super viewWillAppear:aAnimated];
}

- (void) didRotateFromInterfaceOrientation: (UIInterfaceOrientation) aFromInterfaceOrientation
{
    [super didRotateFromInterfaceOrientation:aFromInterfaceOrientation];
}

- (BOOL) shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)aToInterfaceOrientation
{
    return YES;
}


- (id)init 
{
    self = [super init];
    if (self) 
    {
    }
    return self;
}

- (id) initWithToolbarItems:(NSArray *)aItems
{
    self = [super init];
    if (self) 
    {
        self.toolbarItems = aItems;
    }
    return self;
}

- (void)dealloc 
{
    [toolbarItems release];
    [startColor release];
    [endColor release];
    [model release];
    [super dealloc];
}

@end
