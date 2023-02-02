import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:http/http.dart' as http;
import 'dart:convert' as convert;

class FlutterInstallUnknownApkApi {

  final String api;
  final String? typeApi;
  FlutterInstallUnknownApkApi({required this.api, this.typeApi});

  final plugin = FlutterInstallUnknownApk();
  String downloadUrl = '';
  String downloadIcon = '';
  String downloadThumbnail = '';
  String downloadName = '';

  void update({
    required String title,
  })  {
    if(downloadUrl != '') {
      plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
        'downloadUrl': downloadUrl,
        'downloadIcon': downloadIcon,
        'downloadThumbnail': downloadThumbnail,
        'downloadName': downloadName,
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
    print('appInfo => $appInfo');
    if(appInfo != null) {
      downloadUrl = appInfo['app_url'].toString();
      downloadIcon = appInfo['app_icon'].toString();
      downloadThumbnail = appInfo['app_thumbnail'].toString();
      downloadName = appInfo['app_name'].toString();
      //print('appInfo => $appInfo');
      return await canUpdateFromApi(result);
    }

    return false;
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

  Future<List<Map<String, dynamic>>> getTypesAndAppList() async {
    var result = <Map<String, dynamic>>[];
    var url = Uri.parse(typeApi!);
    var response = await http.get(url);
    if (response.statusCode == 200) {
      var jsonResponse =
      convert.jsonDecode(response.body) as List<dynamic>;
      for(int i = 0; i < jsonResponse.length; i++) {
        if(jsonResponse[i]['type_id'] != 200) {
          var data = <String, dynamic>{};
          var appList = await getAppByTypeId(jsonResponse[i]['type_id']);
          if(appList.isNotEmpty) {
            data["type"] = jsonResponse[i];
            data["app_list"] = appList;
            result.add(data);
          }
        }
      }
    }

    return result;
  }

  Future<List<dynamic>> getAppByTypeId(int typeId) async {
    var result = [];
    var jsonResponse = await getAll();
    for(int i = 0; i < jsonResponse.length; i++) {
      if(jsonResponse[i]['type_id'] == typeId) {
        Map<String, dynamic> responseFromNative = await plugin.execute('APPLICATION_PLUGIN', {
          'type': 'app_installed_or_not',
          'app_id': jsonResponse[i]['app_id'],
        });
        if(!responseFromNative['result']) {
          result.add(jsonResponse[i]);
        }
        // result.add(jsonResponse[i]);
      }

      if(i == jsonResponse.length - 1) {
        result.shuffle();
      }
    }

    return result;
  }

  Future<List<dynamic>> getOnlyAppNotInstall() async {
    var result = [];
    var jsonResponse = await getAll();
    for(int i = 0; i < jsonResponse.length; i++) {
      Map<String, dynamic> responseFromNative = await plugin.execute('APPLICATION_PLUGIN', {
        'type': 'app_installed_or_not',
        'app_id': jsonResponse[i]['app_id'],
      });
      if(!responseFromNative['result']) {
        result.add(jsonResponse[i]);
      }
    }

    return result;
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