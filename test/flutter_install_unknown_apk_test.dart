import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk_platform_interface.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterInstallUnknownApkPlatform
    with MockPlatformInterfaceMixin
    implements FlutterInstallUnknownApkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterInstallUnknownApkPlatform initialPlatform = FlutterInstallUnknownApkPlatform.instance;

  test('$MethodChannelFlutterInstallUnknownApk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterInstallUnknownApk>());
  });

  test('getPlatformVersion', () async {
    FlutterInstallUnknownApk flutterInstallUnknownApkPlugin = FlutterInstallUnknownApk();
    MockFlutterInstallUnknownApkPlatform fakePlatform = MockFlutterInstallUnknownApkPlatform();
    FlutterInstallUnknownApkPlatform.instance = fakePlatform;

    expect(await flutterInstallUnknownApkPlugin.getPlatformVersion(), '42');
  });
}
