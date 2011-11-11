//
//  IconModel.m
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

#import "IconModel.h"


@implementation IconModel

@synthesize key;
@synthesize viewController;
@synthesize url;
@synthesize labelText;
@synthesize iconImage;
@synthesize segue;


#pragma mark Lifecycle
+ (id) iconModelWithKey:(NSString*) aKey controller:(UIViewController*) aController icon:(UIImage*) aIcon label:(NSString*) aLabel segue:(NSString*)asegue
{
    IconModel* value = [[IconModel alloc] init];
    
    value.key = aKey;
    value.viewController = aController;
    value.iconImage = aIcon;
    value.labelText = aLabel;
    value.segue = asegue;
    
    return [value autorelease];
}

+ (id) iconModelWithKey:(NSString*) aKey url:(NSURL*) aUrl icon:(UIImage*) aIcon label:(NSString*) aLabel
{
    IconModel* value = [[IconModel alloc] init];
    
    value.key = aKey;
    value.url = aUrl;
    value.iconImage = aIcon;
    value.labelText = aLabel;
    
    return [value autorelease];
}

- (void)dealloc 
{
    [key release];
    [viewController release];
    [url release];
    [labelText release];
    [iconImage release];
    [segue release];
    [super dealloc];
}
@end
