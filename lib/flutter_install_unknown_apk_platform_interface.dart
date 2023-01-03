import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_install_unknown_apk_method_channel.dart';

abstract class FlutterInstallUnknownApkPlatform extends PlatformInterface {
  /// Constructs a FlutterInstallUnknownApkPlatform.
  FlutterInstallUnknownApkPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterInstallUnknownApkPlatform _instance = MethodChannelFlutterInstallUnknownApk();

  /// The default instance of [FlutterInstallUnknownApkPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterInstallUnknownApk].
  static FlutterInstallUnknownApkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterInstallUnknownApkPlatform] when
  /// they register themselves.
  static set instance(FlutterInstallUnknownApkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
