//
//  IconModel.h
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


@interface IconModel : NSObject 

/**
 * Unique key to reference this model item.
 */
@property (copy) NSString* key;

/**
 * UIViewController to push into the view when this item is selected. This
 * property will not be used if the url property is a non-nil value and the
 * managing controller knows how to handle a URL.
 */
@property (retain) UIViewController* viewController;

/**
 * URL to push onto the UrlNavigationController when the item is selected. 
 * If this property is a non-nil value, the controller property will be
 * ignored if the managing controller knows how to handle a URL.
 */
@property (retain) NSURL* url;

/**
 * Label of the item in the home view.
 */
@property (copy) NSString* labelText;

/**
 * Icon of the item in the home view.
 */
@property (retain) UIImage* iconImage;


+ (id) iconModelWithKey:(NSString*) aKey controller:(UIViewController*) aController icon:(UIImage*) aIcon label:(NSString*) aLabel;
+ (id) iconModelWithKey:(NSString*) aKey url:(NSURL*) aUrl icon:(UIImage*) aIcon label:(NSString*) aLabel;

@end
