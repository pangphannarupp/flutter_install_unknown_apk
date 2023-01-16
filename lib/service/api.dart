import 'dart:convert';
import 'dart:math';

import 'package:http/http.dart' as http;
import 'dart:convert' as convert;

class Api {

  final String _URL_BASE = 'raw.githubusercontent.com';
  final String _URL_PATH = '/pangphannarupp/appstore/main/api.json';
  final String _URL_HTTP = 'https';

  Future<List<dynamic>> getAll() async {
    // var url = Uri.https(_URL_BASE, _URL_PATH, {'q': '{$_URL_HTTP}'});
    var url = Uri.parse('https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}');
    var response = await http.get(url);
    if (response.statusCode == 200) {
      var jsonResponse =
      convert.jsonDecode(response.body) as List<dynamic>;
      return jsonResponse;
    }

    return [];
  }

  Future<dynamic> getByAppId(String appId) async {
    var jsonResponse = await Api().getAll();
    for(var i = 0; i < jsonResponse.length; i++) {
      if(jsonResponse[i]['app_id'].toString() == appId) {
        return jsonResponse[i];
      }
    }

    return null;
  }

  Future<bool> canUpdate(dynamic appInfo) async {
    var jsonResponse = await Api().getByAppId(appInfo['packageName']);
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