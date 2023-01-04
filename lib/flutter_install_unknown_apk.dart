
import 'flutter_install_unknown_apk_platform_interface.dart';

class FlutterInstallUnknownApk {
  Future<Map<String, dynamic>> execute(String pluginKey, Object? param) {
    return FlutterInstallUnknownApkPlatform.instance.execute(pluginKey, param ?? {});
  }
}
