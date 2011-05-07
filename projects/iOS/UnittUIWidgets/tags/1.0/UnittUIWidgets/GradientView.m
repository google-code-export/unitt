//
//  GradientView.m
//  UnittUIWidgets
//
//  Created by Josh Morris on 4/11/11.
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

#import "GradientView.h"
#import <QuartzCore/QuartzCore.h>

@implementation GradientView

@synthesize startColor;
@synthesize endColor;

// Construct the gradient for either construction method
- (void)setupGradientLayer
{
    if (self.layer && self.startColor && self.endColor)
    {
        CAGradientLayer *gradientLayer = (CAGradientLayer *) self.layer;
        gradientLayer.colors =
        [NSArray arrayWithObjects:
         (id)startColor.CGColor,
         (id)endColor.CGColor,
         nil];
        self.backgroundColor = [UIColor clearColor];
    }
}

- (void) setStartColor: (UIColor *) aColor
{
    [startColor release];
    startColor = [aColor retain];
    [self setupGradientLayer];
}

- (void) setEndColor: (UIColor *) aColor
{
    [endColor release];
    endColor = [aColor retain];
    [self setupGradientLayer];
}

+ (Class)layerClass
{
	return [CAGradientLayer class];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
	if (self)
	{
		[self setupGradientLayer];
    }
    return self;
}

- (id) initWithCoder: (NSCoder *) aDecoder
{
    self = [super initWithCoder: aDecoder];
	if (self)
	{
		[self setupGradientLayer];
    }
    return self;
}

@end
