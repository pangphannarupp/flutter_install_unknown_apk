import 'dart:math';

import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:http/http.dart' as http;
import 'dart:convert' as convert;

class FlutterInstallUnknownApkApi {

  final String api;
  FlutterInstallUnknownApkApi({required this.api});

  final plugin = FlutterInstallUnknownApk();
  String url = '';

  void update({
    required String title,
  })  {
    if(url != '') {
      plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
        'downloadUrl': url,
        'downloadTitle': title
      });
    }
  }

  Future<bool> canUpdate() async {
    var result = await plugin.execute('APPLICATION_PLUGIN', {
      'type': 'app_info',
    });
    //print('app info => $result');
    var appInfo = await getByAppId(result['packageName']);
    url = appInfo['app_url'].toString();
    //print('appInfo => $appInfo');
    return await canUpdateFromApi(result);
  }

  Future<List<dynamic>> getAll() async {
    // var url = Uri.https(_URL_BASE, _URL_PATH, {'q': '{$_URL_HTTP}'});
    // var url = Uri.parse('https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}');
    var url = Uri.parse(api);
    var response = await http.get(url);
    if (response.statusCode == 200) {
      var jsonResponse =
      convert.jsonDecode(response.body) as List<dynamic>;
      return jsonResponse;
    }

    return [];
  }

  Future<dynamic> getByAppId(String appId) async {
    var jsonResponse = await getAll();
    for(var i = 0; i < jsonResponse.length; i++) {
      if(jsonResponse[i]['app_id'].toString() == appId) {
        return jsonResponse[i];
      }
    }

    return null;
  }

  Future<bool> canUpdateFromApi(dynamic appInfo) async {
    var jsonResponse = await getByAppId(appInfo['packageName']);
    if(jsonResponse == null) {
      return false;
    }

    var localAppVersions = (appInfo['appVersion'].toString().split('.').length == 2 ?
        appInfo['appVersion'] + '.0' : appInfo['appVersion']).toString().split('.');
    var serverAppVersions = jsonResponse['app_version'].toString().split('.');
    if(int.parse(serverAppVersions[0]) > int.parse(localAppVersions[0])) {
      return true;
    } else if(int.parse(serverAppVersions[0]) == int.parse(localAppVersions[0])) {
      if(int.parse(serverAppVersions[1]) > int.parse(localAppVersions[1])) {
        return true;
      } else if(int.parse(serverAppVersions[1]) == int.parse(localAppVersions[1])) {
        if(int.parse(serverAppVersions[2]) > int.parse(localAppVersions[2])) {
          return true;
        }
      }
    }

    return false;
  }
}