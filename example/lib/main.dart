import 'package:flutter/material.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:flutter_install_unknown_apk/screen/more_application.dart';
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
  final plugin = FlutterInstallUnknownApk();

  @override
  void initState() {
    checkAppUpdate();

    super.initState();
  }

  void downloadAndInstallApk({required String url}) {
    plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
      'url': url
    });
  }

  void checkAppUpdate() async {
    var result = await plugin.execute('APPLICATION_PLUGIN', {
      'type': 'app_info',
    });
    //print('app info => $result');
    var appInfo = await Api().getByAppId(result['packageName']);
    //print('appInfo => $appInfo');
    var canUpdate = await Api().canUpdate(result);
    print('canUpdate => $canUpdate');
    if(canUpdate) {
      downloadAndInstallApk(url: appInfo['app_url'].toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      // home: Scaffold(
      //   appBar: AppBar(
      //     title: const Text('DOWNLOAD & INSTALL APK'),
      //     elevation: 0,
      //   ),
      //   body: ListView(
      //     children: [
      //       ListTile(
      //         onTap: () {
      //           downloadAndInstallApk(url: 'https://bmoon.club/database-php/com.phanna.app.komnapkhmer/com.phanna.app.komnapkhmer.5.1.2.apk');
      //         },
      //         leading: const Icon(Icons.android_outlined),
      //         title: const Text('កំណាព្យខ្មែរ'),
      //         trailing: const Icon(Icons.save_alt),
      //       ),
      //       ListTile(
      //         onTap: () {
      //           downloadAndInstallApk(url: 'https://bmoon.club/database-php/com.ppplaylist.korean_movies/com.ppplaylist.korean_movies.1.5.2.apk');
      //         },
      //         leading: const Icon(Icons.android_outlined),
      //         title: const Text('Korean Movie'),
      //         trailing: const Icon(Icons.save_alt),
      //       ),
      //       ListTile(
      //         onTap: () {
      //           downloadAndInstallApk(url: 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/apk/com.phanna.app.komnapkhmer.5.1.2.apk');
      //         },
      //         leading: const Icon(Icons.android_outlined),
      //         title: const Text('លំហាត់ថ្នាក់ទី១២'),
      //         trailing: const Icon(Icons.save_alt),
      //       )
      //     ],
      //   ),
      // ),
      home: MoreApplication(),
    );
  }
}
