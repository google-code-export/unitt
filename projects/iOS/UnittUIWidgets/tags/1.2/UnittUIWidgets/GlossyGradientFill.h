//
//  GlossyGradientFill.h
//  UnittUIWidgets
//
//  Original code provided by Matt Gallagher. For reference, see his excellent
//  blog, "Cocoa With Love" at http://cocoawithlove.com/. The specific article is
//  http://cocoawithlove.com/2008/09/drawing-gloss-gradients-in-coregraphics.html
//  Original code, Copyright (c) 2009-2011 Matt Gallagher. All rights reserved. 
//
//  Alterations and improvements made by Josh Morris on 10/23/10.
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
 * Workhorse for actually creating the various gradients and effects used in the
 * GlossyButton.
 */
@interface GlossyGradientFill : NSObject 
{    
}

extern void DrawGlossGradient(CGContextRef context, CGColorRef color, CGRect inRect);
extern void DrawGlowShadow(CGContextRef context, CGColorRef color, CGRect inRect);

@end
