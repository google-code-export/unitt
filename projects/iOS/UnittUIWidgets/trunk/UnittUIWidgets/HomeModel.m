//
//  HomeModel.m
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

#import "HomeModel.h"


@implementation HomeModel


#pragma mark Model
- (NSArray*) iconModels
{
    if (iconModels)
    {
        return [NSArray arrayWithArray:iconModels];
    }
    
    return nil;
}

- (void) addItem: (NSString*) aKey controller: (UIViewController*) aController icon: (UIImage*) aIcon label: (NSString*) aLabelText segue:(NSString*)asegue
{
    //if params are valid, proceed
    if (aKey && aController && aIcon && aLabelText)
    {
        //verify we don't already have this key
        for (IconModel* item in iconModels) 
        {
            if (item)
            {
                if ([aKey isEqualToString:item.key])
                {
                    return;
                }
            }
        }
        
        //add to models
        [iconModels addObject:[IconModel iconModelWithKey:aKey controller:aController icon:aIcon label:aLabelText segue:asegue]];
    }
}

- (void) addItem: (NSString*) aKey url: (NSURL*) aUrl icon: (UIImage*) aIcon label: (NSString*) aLabelText
{
    //if params are valid, proceed
    if (aKey && aUrl && aIcon && aLabelText)
    {
        //verify we don't already have this key
        for (IconModel* item in iconModels) 
        {
            if (item)
            {
                if ([aKey isEqualToString:item.key])
                {
                    return;
                }
            }
        }
        
        //add to models
        [iconModels addObject:[IconModel iconModelWithKey:aKey url:aUrl icon:aIcon label:aLabelText]];
    }
}

- (void) removeItem: (NSString*) aKey
{
    //if params are valid, proceed
    if (aKey)
    {
        IconModel* itemToRemove = nil;
        
        //see if we have an item with the specified key
        for (IconModel* item in iconModels) 
        {
            if (item)
            {
                if ([aKey isEqualToString:item.key])
                {
                    itemToRemove = item;
                    break;
                }
            }
        }
        
        //remove from models
        if (itemToRemove)
        {
            [iconModels removeObject:itemToRemove];
        }
    }
}


#pragma mark Lifecycle
- (id)init 
{
    self = [super init];
    if (self) 
    {
        iconModels = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc 
{
    [iconModels release];
    [super dealloc];
}

@end
