//
//  GlossyButton.h
//  UnittUIWidgets
//
//  Created by Josh Morris on 10/22/10.
//  Copyright 2010 UnitT Software, Inc. All rights reserved.
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
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>


/**
 * Class used to provide a simple glossy or a rich, caustic gradient, gel effect
 * to a UIButton. At this time, only rounded rectangle buttons are supported. This
 * can easily be changed and the rounded rectangle appearance moved into a configurable
 * property.
 */
@interface GlossyButton : UIButton 
{
    UIColor *disabledGradientColor;
    UIColor *gradientColor;
    BOOL useGelAppearance;
    UIColor *borderHighlightColor;
    BOOL highlightBorder;
}

/**
 * Color to use as a base for the gradient. Highlights are derived from this
 * color so darker is better. This is used, rather than the gradientColor, when 
 * the button is disabled.
 */
@property (nonatomic, retain) UIColor *disabledGradientColor;

/**
 * Color to use as a base for the gradient. Highlights are derived from this
 * color so darker is better.
 */
@property (nonatomic, retain) UIColor *gradientColor;

/**
 * True to use a caustic highlight to provide a gel appearance. False to use
 * a simple gloss effect. For more information on caustice highlights, see
 * Matt Gallagher's excellent article in his "Cocoa With Love" blog, 
 * http://cocoawithlove.com/2008/09/drawing-gloss-gradients-in-coregraphics.html
 */
@property (nonatomic) BOOL useGelAppearance;

/**
 * Color to use in the border highlight.
 */
@property (nonatomic, retain) UIColor *borderHighlightColor;

/**
 * True to show a border highlight. Typically used to indicate a preferabl action.
 */
@property (nonatomic) BOOL highlightBorder;

@end
