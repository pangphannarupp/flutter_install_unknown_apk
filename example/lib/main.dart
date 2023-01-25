import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_install_unknown_apk/screen/more_app_01.dart';
import 'package:flutter_install_unknown_apk/screen/more_app_00.dart';
import 'package:flutter_install_unknown_apk/service/api.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  String apiUrl = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';
  String typeApiUrl = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/type_api.json?r=${Random().nextInt(1000)}';

  @override
  void initState() {
    //checkAppUpdate();

    super.initState();
  }

  void checkAppUpdate() async {
    var application = FlutterInstallUnknownApkApi(api: apiUrl);
    var canUpdate = await application.canUpdate();
    if(canUpdate) {
      application.update(
          title: 'កំពុងធ្វើបច្ចុប្បន្នភាព... '
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MoreApp1(
        api: apiUrl,
        typeApi: typeApiUrl,
        screenTitle: 'កម្មវិធីផ្សេងៗ',
        downloadText: 'ទាញយក និងដំឡើង',
        downloadTitle: 'កំពុងទាញយក... ',
      ),
      // home: MoreApp0(
      //   api: apiUrl,
      //   screenTitle: 'កម្មវិធីផ្សេងៗ',
      //   downloadText: 'ទាញយក និងដំឡើង',
      //   downloadTitle: 'កំពុងទាញយក... ',
      // ),
    );
  }
}
