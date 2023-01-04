import 'package:flutter/material.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';

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
    super.initState();
    requestInstallFromUnknownSource();
  }

  void requestInstallFromUnknownSource() {
    plugin.execute('REQUEST_INSTALL_FROM_UNKNOWN_SOURCE', {});
  }

  void downloadAndInstallApk({required String url}) {
    plugin.execute('DOWNLOAD', {
      'url': url
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('DOWNLOAD & INSTALL APK'),
          elevation: 0,
        ),
        body: ListView(
          children: [
            ListTile(
              onTap: () {
                downloadAndInstallApk(url: 'https://bmoon.club/database-php/com.phanna.app.komnapkhmer/com.phanna.app.komnapkhmer.5.1.2.apk');
              },
              leading: const Icon(Icons.android_outlined),
              title: const Text('កំណាព្យខ្មែរ'),
              trailing: const Icon(Icons.save_alt),
            ),
            ListTile(
              onTap: () {
                downloadAndInstallApk(url: 'https://bmoon.club/database-php/com.ppplaylist.korean_movies/com.ppplaylist.korean_movies.1.5.2.apk');
              },
              leading: const Icon(Icons.android_outlined),
              title: const Text('Korean Movie'),
              trailing: const Icon(Icons.save_alt),
            )
          ],
        ),
      ),
    );
  }
}
