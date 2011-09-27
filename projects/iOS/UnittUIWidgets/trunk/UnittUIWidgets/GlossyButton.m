//
//  GlossyButton.m
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

#import "GlossyButton.h"
#import "GlossyGradientFill.h"

@interface GlossyButton(Private)

- (void) handleEnableChange;

@end


@implementation GlossyButton

@synthesize gradientColor;
@synthesize useGelAppearance;
@synthesize highlightBorder;
@synthesize borderHighlightColor;
@synthesize disabledGradientColor;

- (void)setPathToRoundedRect:(CGRect)rect forInset:(NSUInteger)inset inContext:(CGContextRef)context
{
	// Experimentally determined
	static NSUInteger cornerRadius = 10;
    
	// Unpack size for compactness, find minimum dimension
	CGFloat w = rect.size.width;
	CGFloat h = rect.size.height;
	CGFloat m = w<h?w:h;
    
	// Bounds
	CGFloat b = rect.origin.y;
	CGFloat t = b + h;
	CGFloat l = rect.origin.x;
	CGFloat r = l + w;
	CGFloat d = (inset<cornerRadius)?(cornerRadius-inset):0;
    
	// Special case: Degenerate rectangles abort this method
	if (m <= 0) return;
    
	// Limit radius to 1/2 of the rectangle's shortest axis
	d = (d>0.5*m)?(0.5*m):d;
    
	// Define a CW path in the CG co-ordinate system (origin at LL)
	CGContextBeginPath(context);
	CGContextMoveToPoint(context, (l+r)/2, t);		// Begin at TDC
	CGContextAddArcToPoint(context, r, t, r, b, d);	// UR corner
	CGContextAddArcToPoint(context, r, b, l, b, d);	// LR corner
	CGContextAddArcToPoint(context, l, b, l, t, d);	// LL corner
	CGContextAddArcToPoint(context, l, t, r, t, d);	// UL corner
	CGContextClosePath(context);					// End at TDC
}

- (void) drawOutline:(CGRect) rect inContext:(CGContextRef) context 
{
    CGFloat color[4] = {0.0f, 0.0f, 0.0f, 1.0f};
    
    [self setPathToRoundedRect: rect forInset:0 inContext: context];   
    
    if (self.highlightBorder)
    {
        if(self.borderHighlightColor)
        {
            CGContextSetStrokeColorWithColor(context, [self.borderHighlightColor CGColor]);
        }
    
        CGContextSetLineWidth(context, 8);
    }
    else
    {
        CGContextSetStrokeColor(context, color);
    }
    
    CGContextStrokePath(context);
}

- (void) setEnabled:(BOOL) aEnabled
{
    [super setEnabled:aEnabled];
    [self handleEnableChange];
}

- (void) handleEnableChange
{
    [self setNeedsDisplay];
}

- (UIColor*) gradientColor
{
    if (self.enabled)
    {
        return gradientColor;
    }
    
    return self.disabledGradientColor;
}

- (void) setGradientColor:(UIColor*) aGradientColor
{
    if (gradientColor != aGradientColor)
    {
        if (gradientColor)
        {
            [gradientColor release];
        }
        if (aGradientColor)
        {
            gradientColor = [aGradientColor retain];
            if (!self.disabledGradientColor)
            {
                self.disabledGradientColor = [aGradientColor colorWithAlphaComponent:0.3];
            }
        }
    }
}

- (void)drawGlossRect:(CGRect)rect 
{
    CGContextRef currentContext = UIGraphicsGetCurrentContext();
    [self setPathToRoundedRect: rect forInset:0 inContext: currentContext];   
    
    //paint background color    
    CGContextSetFillColorWithColor(currentContext, self.gradientColor.CGColor);
    CGContextFillPath(currentContext);
    
    //create gradient info
    CGGradientRef glossGradient;
    CGColorSpaceRef rgbColorspace;
    size_t num_locations = 2;
    CGFloat locations[2] = { 0.0, 1.0 };
    CGFloat components[8] = { 1.0, 1.0, 1.0, 0.75,   // Start color
        1.0, 1.0, 1.0, 0.06 }; // End color    
    rgbColorspace = CGColorSpaceCreateDeviceRGB();
    glossGradient = CGGradientCreateWithColorComponents(rgbColorspace, components, locations, num_locations);
    
    //determine size/location
    CGRect currentBounds = self.bounds;
    CGPoint topCenter = CGPointMake(CGRectGetMidX(currentBounds), 0.0f);
    CGPoint midCenter = CGPointMake(CGRectGetMidX(currentBounds), CGRectGetMidY(currentBounds));
    
    //clip context
    [self setPathToRoundedRect: rect forInset:0 inContext: currentContext];      
    CGContextClip(currentContext);
    
    //paint gradient
    CGContextDrawLinearGradient(currentContext, glossGradient, topCenter, midCenter, 0);
    
    //draw outline
    [self drawOutline: self.bounds inContext: currentContext];
    
    CGGradientRelease(glossGradient);
    CGColorSpaceRelease(rgbColorspace); 
}

- (void) drawGlossyGelRect:(CGRect)rect 
{
    CGContextRef currentContext = UIGraphicsGetCurrentContext();
    
    //clip context
    [self setPathToRoundedRect: rect forInset:0 inContext: currentContext];
    CGContextClip(currentContext);
    //paint gradient
    DrawGlossGradient(currentContext, self.gradientColor.CGColor, self.bounds);
    
    //draw outline
    [self drawOutline: self.bounds inContext: currentContext];
}

- (void) drawGlossyGelCircle:(CGRect)rect 
{
    CGContextRef currentContext = UIGraphicsGetCurrentContext();
    CGContextClip(currentContext);
    
    //create circle
    
    //create layer
}


- (void)drawRect:(CGRect)rect 
{
    if (useGelAppearance)
    {
        [self drawGlossyGelRect: rect];
    }
    else 
    {
        [self drawGlossRect:rect];
    }

}

@end
