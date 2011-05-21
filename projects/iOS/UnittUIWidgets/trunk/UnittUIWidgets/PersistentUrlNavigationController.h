//
//  PersistentUrlNavigationController.h
//  UnittUIWidgets
//
//  Created by Josh Morris on 5/19/11.
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
#import "UrlNavigationController.h"


@interface PersistentUrlNavigationController : UrlNavigationController 
{
@private
    NSString* persistentUrlKey;
}

/**
 * Key used to save/load the active urls in NSUserDefaults. If none is provided a
 * default constant will be used.
 */
@property (copy) NSString* persistentUrlKey;

/**
 * Saves the absolute urls returned by each of the controllers in the urls
 * property to the NSUserDefaults.
 */
- (void) saveActiveUrls;

/**
 * Loads the urls saved in NSUserDefaults and pushes the entire array of 
 * controllers onto the stack. The last one will be animated if so specified.
 */
- (void) loadActiveUrls: (BOOL) aAnimated;


+ (id) controllerWithUrlManager: (UrlManager*) aUrlManager urlKey: (NSString*) aUrlKey;
- (id) initWithUrlManager: (UrlManager*) aUrlManager urlKey: (NSString*) aUrlKey;

@end
