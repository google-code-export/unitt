//
//  UnittWebSocketClient07Tests.h
//  UnittWebSocketClient
//
//  Created by Josh Morris on 6/19/11.
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

#import <SenTestingKit/SenTestingKit.h>
#import "WebSocket07.h"


@interface UnittWebSocketClient07Tests : SenTestCase  <WebSocketDelegate>
{
@private
    WebSocket* ws;
    NSString* response;
}

@property (nonatomic, readonly) WebSocket* ws;
@property (nonatomic, readonly) NSString* response;

- (void) waitForSeconds: (NSTimeInterval) aSeconds;

@end
