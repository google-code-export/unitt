//
//  Created by jmorris on 6/13/12.
//


#import <Foundation/Foundation.h>
#import "ObjectHandler.h"


@interface ObjectAndExceptionHandler : ObjectHandler {
}

- (BOOL) isException:(NSDictionary*) aDictionary;

- (NSString *)stringFromStackTrace:(NSArray *)aStacktrace;


NSString *const MessageServiceClientErrorDomain = @"MessageServiceClientErrorDomain";

@end