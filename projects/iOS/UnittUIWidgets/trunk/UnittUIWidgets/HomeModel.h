//
//  HomeModel.h
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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "IconModel.h"


@interface HomeModel : NSObject 
{
@private NSMutableArray* iconModels;
}

/**
 * Ordered list of item models for the home screen.
 **/
@property (readonly) NSArray* iconModels;

/**
 * Adds an item to the model. The key must be unique.
 **/
- (void) addItem: (NSString*) aKey controller: (UIViewController*) aController icon: (UIImage*) aIcon label: (NSString*) aLabelText segue:(NSString*)asegue;

/**
 * Adds an item to the model. The key must be unique.
 **/
- (void) addItem:(NSString*) aKey url:(NSURL*) aUrl icon:(UIImage*) aIcon label:(NSString*) aLabelText;

/**
 * Removes an item from the model.
 */
- (void) removeItem: (NSString*) aKey;

@end
