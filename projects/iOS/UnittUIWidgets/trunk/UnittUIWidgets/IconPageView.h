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

@property (retain) id<HomeViewDelegate> homeDelegate;
@property (retain) id<HomeViewDatasource> homeDatasource;
@property (readonly) int rows;
@property (readonly) int columns;
@property (assign) int pageNumber;

+ (CGSize) getGridExtents: (CGRect) aRect margin: (CGSize) aMargin itemSize: (CGSize) aItemSize;

- (id) initWithFrame: (CGRect) aFrame page: (int) aPage delegate: (id<HomeViewDelegate>) aDelegate datasource: (id<HomeViewDatasource>) aDataSource;
- (UIView*) createImageForTag: (NSInteger) aTag;
- (void) applyImageForTag:(NSInteger)aTag icon:(UIImage*) aIconImage view:(UIView *)aView;
- (UIView*) createTextForTag: (NSInteger) aTag;
- (void) applyTextForTag: (NSInteger) aTag text: (NSString*) aText view: (UIView*) aView;

@end