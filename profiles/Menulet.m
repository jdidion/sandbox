//
//  Menulet.m
//  profiles
//
//  Created by John Didion on 3/17/10.
//  Copyright 2010 UNC. All rights reserved.
//

#import "Menulet.h"

@implementation Menulet

-(void)dealloc
{
    [statusItem release];
	[super dealloc];
}

- (void)awakeFromNib
{
	statusItem = [[[NSStatusBar systemStatusBar] 
				   statusItemWithLength:NSVariableStatusItemLength]
				  retain];
	[statusItem setImage:[NSImage imageNamed:@"profile"]];
	[statusItem setHighlightMode:YES];
	[statusItem setTitle:[NSString stringWithString:@""]]; 
	[statusItem setEnabled:YES];
	[statusItem setToolTip:@"Profiles"];
	
	[statusItem setAction:@selector(updateIPAddress:)];
	[statusItem setTarget:self];
}

-(IBAction)updateIPAddress:(id)sender
{
	[statusItem setTitle: [NSString stringWithString:@"1.1.1.1"]]; 
}

+ (void)showMenuIcon
{
	NSMenu       *theMenu;
	theMenu = [[NSMenu alloc] initWithTitle:@""];
	[theMenu addItemWithTitle:@"Quit" action:@selector(terminate:) keyEquivalent:@"Q"];
	[statusItem setMenu:theMenu];
	[theMenu release];
}

@end
