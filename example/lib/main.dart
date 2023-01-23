import 'dart:math';

import 'package:flutter/material.dart';
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

  String url = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';

  @override
  void initState() {
    checkAppUpdate();

    super.initState();
  }

  void checkAppUpdate() async {
    var application = FlutterInstallUnknownApkApi(api: url);
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
      home: MoreApplication(
        api: url,
        screenTitle: 'កម្មវិធីផ្សេងៗ',
        description: 'ទាញយក និងដំឡើង',
        downloadTitle: 'កំពុងទាញយក... ',
      ),
    );
  }
}
