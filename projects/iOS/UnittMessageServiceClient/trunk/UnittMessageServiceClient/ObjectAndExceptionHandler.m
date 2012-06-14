//
//  Created by jmorris on 6/13/12.
//


#import "ObjectAndExceptionHandler.h"


@implementation ObjectAndExceptionHandler

- (BOOL) isException:(NSDictionary*) aDictionary {
    //all exceptions must have the right keys
    BOOL hasCauseKey = NO;
    BOOL hasMessageKey = NO;
    BOOL hasLocalizedMessageKey = NO;
    BOOL hasStacktraceKey = NO;

    for (id key in aDictionary.allKeys) {
        if ([key isKindOfClass:[NSString class]]) {
            NSString* keyString = (NSString*) key;
            if (!hasCauseKey) {
                hasCauseKey = [keyString isEqualToString:@"cause"];
            }
            if (!hasMessageKey) {
                hasMessageKey = [keyString isEqualToString:@"message"];
            }
            if (!hasLocalizedMessageKey) {
                hasLocalizedMessageKey = [keyString isEqualToString:@"localizedMessage"];
            }
            if (!hasStacktraceKey) {
                hasStacktraceKey = [keyString isEqualToString:@"stackTrace"];
            }
        }
    }

    return hasCauseKey && hasMessageKey && hasLocalizedMessageKey && hasStacktraceKey;
}

- (Class) readConcreteClassFromDictionary:(NSDictionary*) aData {
    Class result = [super readConcreteClassFromDictionary:aData];

    //if we don't have a result class
    if (!result) {
        if ([self isException:aData]) {
            return [NSError class];
        }
    }

    return result;
}

- (id) createFromClass:(Class) aClass {
    if (aClass == [NSError class]) {
        return [NSError errorWithDomain:MessageServiceClientErrorDomain code:0 userInfo:[NSMutableDictionary dictionary]];
    }

    return [super createFromClass:aClass];
}

- (NSString*) stringFromStackTrace:(NSArray*) aStacktrace {
    NSMutableString* result = [NSMutableString string];

    for (id item in aStacktrace) {
        if ([item isKindOfClass:[NSDictionary class]]) {
            NSDictionary* line = (NSDictionary*) item;
            [result appendFormat:@"%@#%@: %@\n", [line objectForKey:@"className"], [line objectForKey:@"methodName"], [line objectForKey:@"methodName"]];
        }
    }

    return result;
}

- (void)fillObjectFromDictionary:(NSDictionary *)aData object:(id)aObject {
    if ([aObject isKindOfClass:[NSError class]]) {
        NSError* error = (NSError*) aObject;
        if (error.userInfo && [error.userInfo isKindOfClass:[NSMutableDictionary class]]) {
            NSMutableDictionary* info = (NSMutableDictionary*) error.userInfo;
            [info setObject:[aData objectForKey:@"localizedMessage"] forKey:NSLocalizedDescriptionKey];
            [info setObject:[self stringFromStackTrace:[aData objectForKey:@"stackTrace"]] forKey:NSLocalizedFailureReasonErrorKey];
            id errorValue = [aData objectForKey:@"cause"];
            if (errorValue && [errorValue isKindOfClass:[NSDictionary class]]) {
                id error = [self createFromClass:[NSError class]];
                [self fillObjectFromDictionary:errorValue object:error];
                [info setObject:error forKey:NSUnderlyingErrorKey];
            }
        }
    }

    [super fillObjectFromDictionary:aData object:aObject];
}


@end