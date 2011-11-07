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

#import "IconPageView.h"
#import "IconModel.h"

@interface IconPageView() 

@property (retain) NSMutableArray* buttons;
@property (retain) NSMutableArray* labels;

@end

@implementation IconPageView

@synthesize homeDelegate, homeDatasource;
@synthesize buttons;
@synthesize labels;
@synthesize rows, columns, pageNumber;
@synthesize itemSize, margin;

CGSize actualMargin;


#pragma mark View
- (void) onItemOpen: (UIButton*) aSender
{
    if (self.homeDelegate)
    {
        [self.homeDelegate didSelectItem:aSender];
    }
}

+ (CGSize) getGridExtents: (CGRect) aRect margin: (CGSize) aMargin itemSize: (CGSize) aItemSize
{
    int innerWidth = aRect.size.width;
    int innerHeight = aRect.size.height;
    
    //determine max rows & columns
    int columns = (innerWidth - aMargin.width) / (aItemSize.width + aMargin.width);
    int rows = (innerHeight - aMargin.height) / (aItemSize.height + aMargin.height);
    
    return CGSizeMake(columns, rows);
}

- (CGPoint) getPositionIndex: (NSInteger) aIndex
{
    int x = 0;
    int y = 0;
    
    //get x & y position
    if (aIndex < self.columns)
    {
        //is in the first set
        x = aIndex;
    }
    else if ((aIndex + 1) % self.columns == 0)
    {
        //is the last item in a set
        x = self.columns - 1;
        y = ((aIndex + 1) / self.columns) - 1;
    }
    else
    {
        //determine its position
        x = ((aIndex + 1) % self.columns) - 1;
        y = (aIndex + 1) / self.columns;
    }
    
    return CGPointMake(x, y);
}

- (int) indexFor: (NSInteger) aIndex
{    
    int index = aIndex;
    int maxPerPage = self.columns * self.rows;
    if (index >= maxPerPage)
    {
        index = aIndex % maxPerPage;
    }
    return index;
}

- (CGRect) makeRectForItem: (UIView*) aItem
{
    CGPoint origin = CGPointMake(self.bounds.origin.x, self.bounds.origin.y);
    CGSize size = CGSizeMake(self.itemSize.width, self.itemSize.height);
    
    //determine row/column index - zero based
    int index = [self indexFor:aItem.tag];
    CGPoint position = [self getPositionIndex: index];
    int rowIndex = position.y;
    int columnIndex = position.x;
    
    //determine origin based on row/column index
    origin.x = actualMargin.width + (columnIndex * (size.width + actualMargin.width));
    origin.y = actualMargin.height + (rowIndex * (size.height + actualMargin.height));
    
    //modify attributes based on type
    if ([aItem isMemberOfClass:[UILabel class]])
    {
        size.height -= size.width;
        origin.y += size.width;
    }
    else
    {
        size.height = size.width;
    }
    return CGRectMake(origin.x, origin.y, size.width, size.height);
}

- (UIView*) createImageForTag:(NSInteger)aTag
{
    UIButton* item = [UIButton buttonWithType:UIButtonTypeCustom];
    [item addTarget:self action:@selector(onItemOpen:) forControlEvents:UIControlEventTouchUpInside];
    return item;
}

- (void) applyImageForTag:(NSInteger)aTag icon:(UIImage*) aIconImage view:(UIView *)aView
{
    if ([aView isMemberOfClass:[UIButton class]]) 
    {
        UIButton* item = (UIButton*) aView;
        [item setImage: aIconImage forState:UIControlStateNormal];
    }
}

- (void) applyImageValuesForTag:(NSInteger) aTag icon: (UIImage*) aIconImage
{
    UIView* item = nil;
    int index = [self indexFor:aTag];
    
    //try to get button from list, create if missing
    if (buttons.count > index)
    {
        item = [buttons objectAtIndex:index];
    }
    else
    {
        item = [self createImageForTag:aTag];
        item.frame = self.frame;
        item.opaque = NO;
        item.contentMode = UIViewContentModeScaleAspectFill;
        [buttons addObject:item];
        [self addSubview:item];
    }
    
    //apply values
    [self applyImageForTag:aTag icon:aIconImage view:item];
    item.tag = aTag;
    item.frame = [self makeRectForItem:item];
    item.bounds = CGRectMake(0, 0, item.frame.size.width, item.frame.size.height);
    item.center = CGPointMake(item.frame.origin.x + item.frame.size.width / 2, item.frame.origin.y + item.frame.size.height / 2);
    [item setNeedsDisplay];
}

- (UIView*) createTextForTag: (NSInteger) aTag
{
    UILabel* item = [[[UILabel alloc] initWithFrame:self.frame] autorelease];
    item.shadowColor = [UIColor grayColor];
    item.shadowOffset = CGSizeMake(1,1);
    item.textColor = [UIColor whiteColor];
    item.textAlignment = UITextAlignmentCenter;
    item.opaque = NO;
    item.backgroundColor = nil;
    item.adjustsFontSizeToFitWidth = YES;
    item.lineBreakMode = UILineBreakModeWordWrap;
    item.numberOfLines = 2;
    return item;
}

- (void) applyTextForTag: (NSInteger) aTag text: (NSString*) aText view: (UIView*) aView
{
    if ([aView isMemberOfClass:[UILabel class]])
    {
        UILabel* item = (UILabel*) aView;
        item.text = aText;
    }
}

- (void) applyLabelValuesForTag: (NSInteger) aTag text: (NSString*) aText
{
    UIView* item = nil;
    int index = [self indexFor:aTag];
    
    //try to get label from list, create if missing
    if (labels.count > index)
    {
        item = [labels objectAtIndex:index];
    }
    else
    {
        item = [self createTextForTag:aTag];
        item.frame = self.frame;
        [labels addObject:item];
        [self addSubview:item];
    }
    
    //apply values
    [self applyTextForTag:aTag text:aText view:item];
    item.tag = aTag;
    item.frame = [self makeRectForItem:item];
    item.bounds = CGRectMake(0, 0, item.frame.size.width, item.frame.size.height);
    item.center = CGPointMake(item.frame.origin.x + item.frame.size.width / 2, item.frame.origin.y + item.frame.size.height / 2);
    [item setNeedsDisplay];
}

- (void) updateGridExtends
{
    CGRect viewFrame = self.frame;
    
    //determine max rows & columns
    CGSize grid = [IconPageView getGridExtents:viewFrame margin:self.margin itemSize:self.itemSize];
    
    //determine max rows & columns
    columns = grid.width;
    rows = grid.height;
    
    //determine actual margins to use
    int actualHorizMargin = (viewFrame.size.width - (self.columns * self.itemSize.width)) / (self.columns + 1);
    int actualVertMargin = (viewFrame.size.height - (self.rows * self.itemSize.height)) / (self.rows + 1);
    actualMargin = CGSizeMake(actualHorizMargin, actualVertMargin);
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

- (void) layoutSubviews
{
	[super layoutSubviews];
    
    //determine grid extents
    [self updateGridExtends];
    
    //align buttons & label data with model
    NSArray* items = [self.homeDatasource getHomeItems];
    int start = self.pageNumber * self.columns * self.rows;
    int end = start + (self.columns * self.rows);
    if (items.count < end)
    {
        end = items.count;
    }
    for (int i = start; i < end; i++) 
    {
        IconModel* item = [items objectAtIndex:i];
        [self applyImageValuesForTag: i icon:item.iconImage];
        [self applyLabelValuesForTag: i text:item.labelText];
    }
    
    //remove extras from buttons & labels
    [self trimSubViewArray:buttons toLength:end - start];
    [self trimSubViewArray:labels toLength:end - start];
    
    [self setNeedsDisplay];
}


#pragma mark Lifecycle
- (id) initWithFrame: (CGRect) aFrame page: (int) aPage delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource
{
    self = [super initWithFrame:aFrame];
    if (self)
    {
        self.homeDelegate = aDelegate;
        self.homeDatasource = aDataSource;
        self.labels = [NSMutableArray array];
        self.buttons = [NSMutableArray array];
        self.pageNumber = aPage;
        self.opaque = NO;
    }
    return self;
}

- (void)dealloc
{
    self.homeDelegate = nil;
    self.homeDatasource = nil;
    [buttons release];
    [labels release];
    [super dealloc];
}

@end
