import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:flutter_install_unknown_apk/service/api.dart';

class Application {
  final plugin = FlutterInstallUnknownApk();
  String url = '';

  void update() {
    if(url != '') {
      plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
        'url': url
      });
    }
  }

  Future<bool> canUpdate() async {
    var result = await plugin.execute('APPLICATION_PLUGIN', {
      'type': 'app_info',
    });
    //print('app info => $result');
    var appInfo = await FlutterInstallUnknownApkApi().getByAppId(result['packageName']);
    url = appInfo['app_url'].toString();
    //print('appInfo => $appInfo');
    return await FlutterInstallUnknownApkApi().canUpdate(result);
  }
}