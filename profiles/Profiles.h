//
//	Profiles.h
//
//	Shared defines for the entire project
// 

///////////////////////////////////////////////////////////////
//	
//	Bundle information
//
///////////////////////////////////////////////////////////////

// Bundle directory name of the preferences bundle
#define kPrefBundleName					@"MenuMeterDefaults.bundle"

// Bundle ID for the Profiles menu extra
#define kMenuBundleID					@"net.didion.Profiles"

///////////////////////////////////////////////////////////////
//	
//	Preference information
//
///////////////////////////////////////////////////////////////

// Pref versioning
#define	kPrefVersionKey					@"MenuMeterPrefVersion"
#define	kCurrentPrefVersion				1

///////////////////////////////////////////////////////////////
//	
//	Notifications
//
///////////////////////////////////////////////////////////////

// Preferences were changed
#define kPrefChangeNotification			@"prefChange"

// Extras unload
#define kMenuUnloadNotification			@"menuUnload"

///////////////////////////////////////////////////////////////
//	
//	String formats
//
///////////////////////////////////////////////////////////////

#define kMenuIndentFormat				@"    %@"
#define kMenuDoubleIndentFormat			@"        %@"
#define kMenuTripleIndentFormat			@"            %@"
