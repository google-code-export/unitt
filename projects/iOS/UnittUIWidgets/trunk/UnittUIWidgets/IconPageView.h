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
#import "HomeView.h"

@class HomeView;
@protocol HomeViewDelegate;
@protocol HomeViewDatasource;

@interface IconPageView : UIView 
{
@private
    id<HomeViewDelegate> homeDelegate;
    id<HomeViewDatasource> homeDatasource;
    NSMutableArray* buttons;
    NSMutableArray* labels;
    int rows;
    int columns;
    int pageNumber;
}

/**
 * Delegate used to gather layout info and handle events
 */
@property (retain) id<HomeViewDelegate> homeDelegate;

/**
 * Model datasource that provides the icons to show
 */
@property (retain) id<HomeViewDatasource> homeDatasource;

/**
 * Calculated number of rows that can be shown
 */ 
@property (readonly) int rows;

/**
 * Calculated number of columns that can be shown
 */
@property (readonly) int columns;

/**
 * The page that this view represents. Will be used to grab the correct items from the 
 * datasource based upon the max number of items that can be shown in the view.
 */
@property (assign) int pageNumber;

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
 * Returns the number of rows (width) and number of columns (height) that can be shown in
 * the specified frame for the desired minimum margin and desired item size.
 */
+ (CGSize) getGridExtents: (CGRect) aRect margin: (CGSize) aMargin itemSize: (CGSize) aItemSize;


- (id) initWithFrame: (CGRect) aFrame page: (int) aPage delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource;

/**
 * Factory method to create icon for the item at the specified index in the datasource.
 * You can override this to provide your own UIView for the icon.
 */
- (UIView*) createImageForTag: (NSInteger) aTag;

/**
 * Apply image to the icon for the item at the specified index in the datasource.
 * You can override this to apply the image to your own UIView for the icon.
 */
- (void) applyImageForTag:(NSInteger)aTag icon:(UIImage*) aIconImage view:(UIView *)aView;

/**
 * Factory method to create label for the item at the specified index in the datasource.
 * You can override this to provide your own UIView for the label.
 */
- (UIView*) createTextForTag: (NSInteger) aTag;

/**
 * Apply text to the label for the item at the specified index in the datasource.
 * You can override this to apply the text to your own UIView for the label.
 */
- (void) applyTextForTag: (NSInteger) aTag text: (NSString*) aText view: (UIView*) aView;

@end