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


@protocol HomeViewDatasource

- (NSArray*) getHomeItems;

@end


@protocol HomeViewDelegate

@property (retain) UIColor* startColor;
@property (retain) UIColor* endColor;
@property (assign) CGSize itemSize;
@property (assign) CGSize margin;
@property (assign) int toolbarHeight;
@property (assign) BOOL useToolbar;
@property (readonly) NSArray* toolbarItems;

- (void) didSelectItem: (UIButton*) aSender;

@end


@class IconPageView;


@interface HomeView : GradientView <UIScrollViewDelegate>
{
@private
    id<HomeViewDelegate> homeDelegate;
    id<HomeViewDatasource> homeDatasource;
    UIScrollView *scrollView;
	UIPageControl *pageControl;
    UIToolbar* toolbar;
    int numberOfPages;
    int currentPage;
    NSMutableArray* pageViews;
    
    // To be used when scrolls originate from the UIPageControl
    BOOL pageControlUsed;
    
	CAGradientLayer *originShadow;
}

@property (retain) id<HomeViewDelegate> homeDelegate;
@property (retain) id<HomeViewDatasource> homeDatasource;
@property (readonly) UIToolbar* toolbar;
@property (readonly) int numberOfPages;
@property (assign) int currentPage;

- (id) initWithFrame: (CGRect) aFrame delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource;
- (void) setupView;
- (IconPageView*) createPageView: (int) aPage frame: (CGRect) aFrame;
- (UIScrollView*) createScrollViewWithFrame: (CGRect) aFrame;
- (UIPageControl*) createPageControlWithFrame: (CGRect) aFrame;
- (UIToolbar*) createToolbarWithFrame: (CGRect) aFrame;

@end