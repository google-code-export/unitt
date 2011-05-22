//
//  PrefixUrlHandler.h
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
#import "UrlManager.h"

/**
 * UrlHandler that can handle any url that has an equivalent prefix. The scheme 
 * must match (can be case-insensitive), but the resourceSpecifier must match in
 * a case-sensitive manner.
 */ 
@interface PrefixUrlHandler : NSObject <UrlHandler>
{
@private
    Class controllerClass;
    NSURL* urlPrefix;
    BOOL useNib;
    NSString* nibName;
    NSBundle* nibBundle;
}

/**
 * If true, init new instances of the controller with the nibName & nibBundle
 * properties. If false, simply init.
 */
@property (assign) BOOL useNib;

/**
 * Name of the NIB to load. Can be nil.
 */
@property (copy) NSString* nibName;

/**
 * Bundle to use when loading the NIB. Can be nil.
 */
@property (retain) NSBundle* nibBundle;

/**
 * Class of the UIViewControllerHasUrl to return when handling a url.
 **/
@property (retain) Class controllerClass;

/**
 * Url used as a prefix to test incoming urls. 
 **/
@property (retain) NSURL* urlPrefix;

+ (id) handlerWithControllerClass: (Class) aControllerClass urlPrefix: (NSURL*) aUrlPrefix;
+ (id) handlerWithControllerClass: (Class) aControllerClass nibName: (NSString*) aNibName nibBundle: (NSBundle*) aNibBundle urlPrefix: (NSURL*) aUrlPrefix; //sets useNib to true
- (id) initWithControllerClass: (Class) aControllerClass urlPrefix: (NSURL*) aUrlPrefix;
- (id) initWithControllerClass: (Class) aControllerClass nibName: (NSString*) aNibName nibBundle: (NSBundle*) aNibBundle urlPrefix: (NSURL*) aUrlPrefix; //sets useNib to true

@end
