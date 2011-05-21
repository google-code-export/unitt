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

/**
 * Designates a UIViewController as supporting a URL for representing its state.
 */
@protocol UIViewControllerHasUrl

/**
 * Can be used to retrieve or change the current URL and so change/retrieve the state
 * of the view controller.
 */
@property (retain) NSURL* currentUrl;

@end

/**
 * Designates a class capable of constructing an instance of a UIViewControllerHasUrl
 * from a URL.
 */
@protocol UrlHandler

/**
 * Returns true if the URL can be handled.
 */
- (BOOL) canHandleUrl: (NSURL*) aUrl;

/**
 * Returns a new instance of a UIViewControllerHasUrl configured with the specified
 * URL. Will return nil if it is unable to handle the URL.
 */
- (UIViewController<UIViewControllerHasUrl>*) handleUrl: (NSURL*) aUrl;

@end

/**
 * Convenience class used to create view controllers from a specified URL. It is a
 * simple wrapper around a list of url handlers that can be modified at anytime.
 */
@interface UrlManager : NSObject 
{
@private
    NSMutableArray* urlHandlers;
}

/**
 * Ordered list of UrlHandlers that will be used to handle a specified URL.
 */
@property (retain) NSMutableArray* urlHandlers;

/**
 * Returns true if the URL can be handled by one of the UrlHandlers
 */
- (BOOL) canHandleUrl: (NSURL*) aUrl;

/**
 * Returns a new instance of a UIViewControllerHasUrl configured with the specified
 * URL. Will return nil if none of the UrlHandlers is unable to handle the URL.
 */
- (UIViewController<UIViewControllerHasUrl>*) handleUrl: (NSURL*) aUrl;


+ (id) managerWithUrlHandlers: (NSArray*) aUrlHandlers;
- (id) initWithUrlHandlers: (NSArray*) aUrlHandlers;

@end
