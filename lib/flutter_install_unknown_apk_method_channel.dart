import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_install_unknown_apk_platform_interface.dart';

/// An implementation of [FlutterInstallUnknownApkPlatform] that uses method channels.
class MethodChannelFlutterInstallUnknownApk extends FlutterInstallUnknownApkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_install_unknown_apk');

  @override
  Future<Map<String, dynamic>> execute(String pluginKey, Object param) async {
    final result = await methodChannel.invokeMethod(pluginKey, {
      'param': param
    });

    return Map.from(result);
  }
}
