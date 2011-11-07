//
//  HomeView.h
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

#import <UIKit/UIKit.h>
#import "GradientView.h"
#import "IconPageView.h"
#import <QuartzCore/QuartzCore.h>


#define SHADOW_HEIGHT 20.0
#define SHADOW_RATIO (SHADOW_INVERSE_HEIGHT / SHADOW_HEIGHT)


/**
 * Delegate used to provide the model for the home screen.
 */
@protocol HomeViewDatasource

/**
 * Ordered list of IconModel objects.
 */
- (NSArray*) getHomeItems;

@end


/**
 * Delegate responsible for configuring the appearance of the home screen 
 * and handling actions.
 */
@protocol HomeViewDelegate

/*
 * Called when the icon view is pressed. 
 */
- (void) didSelectItem: (UIView*) aSender;

@end


@class IconPageView;


@interface HomeView : GradientView <UIScrollViewDelegate>

/**
 * startColor inherited from  gradient view.
 * endColor inherited from gradient view.
 */

/**
 * Show shadow at the top of the view.
 */
@property (assign) BOOL showShadow;

/**
 * Desired size of each item (label and icon together).
 */
@property (assign) CGSize itemSize;

/**
 * Minimum size of the margins. Width specifies horizontal margin.
 * Height specifies vertical margin. These margins are the minimum
 * size. When the view is actually laid out, the margins may be
 * increased to fill the space.
 */
@property (assign) CGSize margin;

/**
 * Specifies the height of the toolbar.
 */
@property (assign) int toolbarHeight;

/**
 * True to include a toolbar in the view. The toolbar will use
 * the toolbarHeight propety to set the height. It will autosize
 * to fill the width.
 */
@property (assign) BOOL useToolbar;

/**
 * Items for the toolbar. Changing this after the view is loaded
 * will have no effect.
 */
@property (retain) NSArray* toolbarItems;


/**
 * Delegate used to gather layout info and handle events
 */
@property (retain) id<HomeViewDelegate> homeDelegate;

/**
 * Model datasource that provides the icons to show
 */
@property (retain) id<HomeViewDatasource> homeDatasource;

/**
 * Toolbar for the view. Value is nil if the delegate
 * useToolbar property is false.
 */
@property (readonly) UIToolbar* toolbar;

/**
 * Total number of pages.
 */
@property (readonly) int numberOfPages;

/**
 * Current page being shown.
 */
@property (assign) int currentPage;

- (id) initWithFrame: (CGRect) aFrame delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource;

/**
 * Called to perform the inital setup of the home view before any other 
 * sub views are added. You can override this to customize the home view
 * appearance for your purposes.
 */
- (void) setupView;

/**
 * Factory method to create the individual page views. You can override 
 * this to provide your own UIView for the page view.
 */
- (IconPageView*) createPageView: (int) aPage frame: (CGRect) aFrame;

/**
 * Factory method to create the scroll view. You can override this to 
 * provide your own UIView for the scroll view.
 */
- (UIScrollView*) createScrollViewWithFrame: (CGRect) aFrame;

/**
 * Factory method to create the page control view. You can override this 
 * to provide your own UIView for the page control view.
 */
- (UIPageControl*) createPageControlWithFrame: (CGRect) aFrame;

/**
 * Factory method to create the page control view. You can override this 
 * to provide your own UIView for the page control view.
 */
- (UIToolbar*) createToolbarWithFrame: (CGRect) aFrame;

@end