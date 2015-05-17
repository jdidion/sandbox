//
//  profilesAppDelegate.h
//  profiles
//
//  Created by John Didion on 3/16/10.
//  Copyright 2010 UNC. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface profilesAppDelegate : NSObject <NSApplicationDelegate> {
    NSWindow *window;
}

@property (assign) IBOutlet NSWindow *window;

@end
