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

#import "HomeView.h"
#import "IconModel.h"
#import "IconPageView.h"

@interface HomeView()

@property (nonatomic, retain) UIScrollView* scrollView;
@property (nonatomic, retain) UIPageControl* pageControl;
@property (retain) NSMutableArray* pageViews;

- (void) loadScrollViewWithPage: (int) aPage;
- (void) scrollViewDidScroll: (UIScrollView*) aSender;

@end

@implementation HomeView

@synthesize homeDelegate, homeDatasource;
@synthesize numberOfPages;
@synthesize pageViews;
@synthesize toolbar, scrollView, pageControl;

int pageControlHeight = 18;

#pragma mark View
- (void) onItemOpen: (UIButton*) aSender
{
    if (self.homeDelegate)
    {
        [self.homeDelegate didSelectItem:aSender];
    }
}

- (void) trimSubViewArray: (NSMutableArray*) aArray toLength: (int) aMaxCount
{
    while (aArray.count > aMaxCount)
    {
        int index = aArray.count - 1;
        UIView* item = [aArray objectAtIndex:index];
        [item removeFromSuperview];
        [aArray removeObjectAtIndex:index];
    }
}

- (CGRect) getPageViewFrame: (int) aPage
{
    CGRect frame = self.scrollView.frame;
    frame.origin.x = frame.size.width * aPage;
    frame.origin.y = 0;
    return frame;
}

- (void) updatePageCount
{
    CGSize grid = [IconPageView getGridExtents:[self getPageViewFrame:0] margin:self.homeDelegate.margin itemSize:self.homeDelegate.itemSize];
    int maxPerPage = grid.width * grid.height;
    int totalItems = [self.homeDatasource getHomeItems].count;
    if (maxPerPage > 0)
    {
        int totalPages = totalItems / maxPerPage;
        if (totalPages == 0 && totalItems > 0)
        {
            //handle less than one page
            totalPages = 1;
        }
        else if (totalPages != 0 && totalItems % maxPerPage > 0)
        {
            //handle remaining
            totalPages++;
        }
        numberOfPages = totalPages;
    }
}

- (CAGradientLayer *)createShadowLayer
{
	CAGradientLayer *newShadow = [[CAGradientLayer alloc] init];
	CGRect newShadowFrame =
    CGRectMake(0, 0, self.frame.size.width, SHADOW_HEIGHT);
	newShadow.frame = newShadowFrame;
	CGColorRef darkColor =
    [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.5].CGColor;
	CGColorRef lightColor =
    [self.backgroundColor colorWithAlphaComponent:0.0].CGColor;
	newShadow.colors =
    [NSArray arrayWithObjects:(id)darkColor, (id)lightColor, nil];
	return [newShadow autorelease];
}

- (void)layoutSubviews
{
	[super layoutSubviews];
	
	//create origin shadow, if needed
    if (self.homeDelegate && self.homeDelegate.showShadow)
    {
        if (!originShadow)
        {
            originShadow = [self createShadowLayer];
            [self.layer insertSublayer:originShadow atIndex:0];
        }
        else if (![[self.layer.sublayers objectAtIndex:0] isEqual:originShadow])
        {
            [self.layer insertSublayer:originShadow atIndex:0];
        }
        
        // Stretch and place the origin shadow
        [CATransaction begin];
        [CATransaction setValue:(id)kCFBooleanTrue forKey:kCATransactionDisableActions];
        CGRect originShadowFrame = originShadow.frame;
        originShadowFrame.size.width = self.frame.size.width;
        originShadowFrame.origin.y = 0;
        originShadow.frame = originShadowFrame;
        [CATransaction commit];
    }
    
    //recalculate page number
    [self updatePageCount];
    
    //adjust page control
    self.pageControl.numberOfPages = self.numberOfPages;
    
    //adjust scrollview
    self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width * self.numberOfPages, self.scrollView.frame.size.height);
    
    //refill & update pages
    [self trimSubViewArray:self.pageViews toLength:self.numberOfPages];
    if (self.pageViews.count < self.numberOfPages)
    {
        for (int i = self.pageViews.count; i < self.numberOfPages; i++) 
        {
            [self.pageViews addObject:[NSNull null]];        
        }
    }
    for (id page in self.pageViews) 
    {
        //if non null, update layout        
        if ((NSNull*)page != [NSNull null])
        {
            IconPageView* pageView = (IconPageView*)page;
            pageView.frame = [self getPageViewFrame:pageView.pageNumber];
            [page setNeedsLayout];
        }
    }
    
    //go to current page
    if (self.currentPage >= self.numberOfPages || self.currentPage < 0)
    {
        self.currentPage = 0;
    }
}

- (void) moveToCurrentPage
{	
	int page = self.currentPage;
    
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
    [self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];
    
	// update the scroll view to the appropriate page
    CGRect frame = self.scrollView.frame;
    frame.origin.x = frame.size.width * page;
    frame.origin.y = 0;
    [self.scrollView scrollRectToVisible:frame animated:YES];
}

- (int) currentPage
{
    return currentPage;
}

- (void) setCurrentPage: (int) aPage
{
    if (aPage < 0 || aPage >= self.numberOfPages)
    {
        return;
    }
    
    currentPage = aPage;
    if (self.pageControl)
    {
        self.pageControl.currentPage = aPage;
    }
    [self moveToCurrentPage];
}

- (IconPageView*) createPageView: (int) aPage frame: (CGRect) aFrame
{
    return [[IconPageView alloc] initWithFrame:aFrame page:aPage delegate:self.homeDelegate datasource:self.homeDatasource];
}

- (IconPageView*) createPageView: (int) aPage
{
    CGRect frame = [self getPageViewFrame:aPage];
    IconPageView* view = [self createPageView:aPage frame:frame];
    view.pageNumber = aPage;
    [self.pageViews replaceObjectAtIndex: aPage withObject: view];
    [self.scrollView addSubview: view];
    [view setNeedsLayout];
    return view;
}

- (void) loadScrollViewWithPage: (int) aPage
{
    if (aPage < 0 || aPage >= self.numberOfPages)
    {
        return;
    }
    
    // add the controller's view to the scroll view
    id pageView = [self.pageViews objectAtIndex:aPage];
    
    //fetch from page views if we have it
    if ((NSNull*)pageView == [NSNull null])
    {
        [self createPageView: aPage];
        [scrollView setNeedsDisplay];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    // We don't want a "feedback loop" between the UIPageControl and the scroll delegate in
    // which a scroll event generated from the user hitting the page control triggers updates from
    // the delegate method. We use a boolean to disable the delegate logic when the page control is used.
    if (pageControlUsed)
    {
        // do nothing - the scroll was initiated from the page control, not the user dragging
        return;
    }
	
    // Switch the indicator when more than 50% of the previous/next page is visible
    CGFloat pageWidth = self.scrollView.frame.size.width;
    int page = floor((self.scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.pageControl.currentPage = page;
    
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
    [self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];
    
    // A possible optimization would be to unload the views+controllers which are no longer visible
}

// At the begin of scroll dragging, reset the boolean used when scrolls originate from the UIPageControl
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    pageControlUsed = NO;
}

// At the end of scroll animation, reset the boolean used when scrolls originate from the UIPageControl
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    pageControlUsed = NO;
}

- (void) changePage: (UIPageControl*) aSender
{
    int page = self.pageControl.currentPage;
    self.currentPage = page;
    [self moveToCurrentPage];
    
	// Set the boolean used when scrolls originate from the UIPageControl. See scrollViewDidScroll: above.
    pageControlUsed = YES;
}

- (UIScrollView*) createScrollViewWithFrame: (CGRect) aFrame
{
    UIScrollView* view = [[[UIScrollView alloc] initWithFrame: aFrame] autorelease];
    view.opaque = NO;
    return view;
}

- (UIPageControl*) createPageControlWithFrame: (CGRect) aFrame
{
    UIPageControl* view = [[[UIPageControl alloc] initWithFrame: aFrame] autorelease];
    view.opaque = NO;
    return view;
}

- (UIToolbar*) createToolbarWithFrame: (CGRect) aFrame
{
    UIToolbar* view = [[[UIToolbar alloc] initWithFrame: aFrame] autorelease];
    view.barStyle = UIBarStyleBlack;
    view.translucent = NO;
    view.tintColor = [UIColor colorWithRed:0 green:0 blue:128/255.0 alpha:1.0];
    return view;
}


#pragma mark Lifecycle
- (void) setupView
{
    self.opaque = YES;
    self.startColor = self.homeDelegate.startColor;
    self.endColor = self.homeDelegate.endColor;
}

- (void) setup: (CGRect) aFrame 
{
    //init
    currentPage = -1;
    [self setupView];
    [self updatePageCount];
    
    //add scrollview
    int height = aFrame.size.height - (aFrame.origin.y + pageControlHeight + (self.homeDelegate.useToolbar ? self.homeDelegate.toolbarHeight : 0));
    self.scrollView = [self createScrollViewWithFrame: CGRectMake(aFrame.origin.x, aFrame.origin.y, aFrame.size.width, height)];
    [self.scrollView setAutoresizingMask:UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight];
    [self addSubview: self.scrollView];
    
    //add page control
    self.pageControl = [self createPageControlWithFrame: CGRectMake(aFrame.origin.x, aFrame.origin.y + height, aFrame.size.width, pageControlHeight)];
    self.pageControl.numberOfPages = self.numberOfPages;
    self.pageControl.currentPage = 0;
    [self.pageControl setAutoresizingMask: UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin];
    [self addSubview: self.pageControl];
    [self.pageControl addTarget:self action:@selector(changePage:) forControlEvents:UIControlEventValueChanged];
    
    //add toolbar if requested
    if (self.homeDelegate.useToolbar)
    {
        toolbar = [self createToolbarWithFrame:CGRectMake(aFrame.origin.x, aFrame.size.height - self.homeDelegate.toolbarHeight, aFrame.size.width, self.homeDelegate.toolbarHeight)];
        [self.toolbar setAutoresizingMask: UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin];
        if (self.homeDelegate.toolbarItems && self.homeDelegate.toolbarItems.count > 0)
        {
            [self.toolbar setItems:self.homeDelegate.toolbarItems animated:YES];
        }
        [self addSubview: self.toolbar];
        [self.toolbar setNeedsLayout];
    }
    
    //init array of views
    for (unsigned i = 0; i < self.numberOfPages; i++)
    {
		[self.pageViews addObject:[NSNull null]];
    }
    
    // a page is the width of the scroll view
    self.scrollView.pagingEnabled = YES;
    self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width * self.numberOfPages, self.scrollView.frame.size.height);
    self.scrollView.showsHorizontalScrollIndicator = NO;
    self.scrollView.showsVerticalScrollIndicator = NO;
    self.scrollView.scrollsToTop = NO;
    self.scrollView.delegate = self;    
    self.pageControl.numberOfPages = self.numberOfPages;
    self.pageControl.currentPage = 0;
    
    // pages are created on demand
    // load the visible page
    // load the page on either side to avoid flashes when the user starts scrolling
    [self loadScrollViewWithPage:0];
    [self loadScrollViewWithPage:1];
}

- (id) initWithFrame: (CGRect) aFrame delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource
{
    self = [super initWithFrame: aFrame];
    if (self)
    {
        self.homeDelegate = aDelegate;
        self.homeDatasource = aDataSource;
        self.pageViews = [NSMutableArray array];
        [self setup: aFrame];
    }
    return self;
}

- (void)dealloc
{
    self.homeDelegate = nil;
    self.homeDatasource = nil;
    [toolbar release];
    [pageViews release];
    [scrollView release];
    [pageControl release];
    [super dealloc];
}

@end
