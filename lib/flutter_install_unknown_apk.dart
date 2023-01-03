
import 'flutter_install_unknown_apk_platform_interface.dart';

class FlutterInstallUnknownApk {
  Future<String?> getPlatformVersion() {
    return FlutterInstallUnknownApkPlatform.instance.getPlatformVersion();
  }
}
