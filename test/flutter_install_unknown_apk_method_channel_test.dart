import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk_method_channel.dart';

void main() {
  MethodChannelFlutterInstallUnknownApk platform = MethodChannelFlutterInstallUnknownApk();
  const MethodChannel channel = MethodChannel('flutter_install_unknown_apk');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
