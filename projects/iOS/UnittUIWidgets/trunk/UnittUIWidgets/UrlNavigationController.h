//
//  UrlNavigationController.h
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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "UrlManager.h"


@interface UrlNavigationController : UINavigationController 
{
@private
    UrlManager* urlManager;
}

/**
 * Returns the current URL value of the top most controller. Returns nil if the
 * top controller is nil or does not conform to the UIViewControllerHasUrl protocol. 
 */
@property (nonatomic,readonly) NSURL* topUrl;


/**
 * Returns an array of all controllers that conform to the UIViewControllerHasUrl 
 * protocol. If none are found on the stack, the array will be empty. Typically used
 * to persist the state of this navigation controller for restoration later.
 */
@property (nonatomic,readonly) NSArray* urls;

/**
 * The UrlManager used by this controller to handle the specified urls.
 */
@property (retain) UrlManager* urlManager;

/**
 * Used to push a UIViewController that conforms to the UIViewControllerHasUrl protocol
 * onto the stack of managed controllers. Returns true if the url can be handled and
 * a UIViewController was successfully created and pushed onto the stack.
 */
- (BOOL) pushUrl:(NSURL*) aUrl animated:(BOOL) aAnimated;

/**
 * Used to push an array of UIViewControllers that conforms to the UIViewControllerHasUrl
 * protocol onto the stack of managed controllers. Returns true if there are urls that 
 * can be handled and a UIViewController was successfully created and pushed onto the stack.
 */
- (BOOL) pushUrls:(NSArray*) aUrls animated:(BOOL) aAnimated;

+ (id) controllerWithUrlManager: (UrlManager*) aUrlManager;
- (id) initWithUrlManager: (UrlManager*) aUrlManager;
- (id) init;

@end
