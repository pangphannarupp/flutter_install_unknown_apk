#import "FlutterInstallUnknownApkPlugin.h"
#if __has_include(<flutter_install_unknown_apk/flutter_install_unknown_apk-Swift.h>)
#import <flutter_install_unknown_apk/flutter_install_unknown_apk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_install_unknown_apk-Swift.h"
#endif

@implementation FlutterInstallUnknownApkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterInstallUnknownApkPlugin registerWithRegistrar:registrar];
}
@end
