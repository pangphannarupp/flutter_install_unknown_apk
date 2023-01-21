import 'package:flutter/material.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:flutter_install_unknown_apk/service/api.dart';

class MoreApplication extends StatefulWidget {
  const MoreApplication({Key? key, required this.screenTitle, required this.description, required this.downloadTitle, required this.api}) : super(key: key);

  final String api;
  final String screenTitle;
  final String description;
  final String downloadTitle;

  @override
  State<MoreApplication> createState() => _MoreApplicationState();
}

class _MoreApplicationState extends State<MoreApplication> {
  final plugin = FlutterInstallUnknownApk();

  List<dynamic> data = [];

  void getData() async {
    var jsonResponse = await FlutterInstallUnknownApkApi(api: widget.api).getAll();
    setState(() {
      data = jsonResponse;
      data.shuffle();
    });
  }

  void downloadAndInstall({required String url}) {
    plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
      'downloadUrl': url,
      'downloadTitle': widget.downloadTitle
    });
  }

  @override
  void initState() {
    getData();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.screenTitle),
        elevation: 0,
        actions: [
          IconButton(onPressed: () {
            setState(() {
              data = [];
            });
            getData();
          }, icon: const Icon(Icons.refresh))
        ],
      ),
      body: data.isNotEmpty ? ListView.builder(
        itemCount: data.length,
        itemBuilder: (context, index) {
          return Column(
            children: [
              index == 0 ? const Divider() : const SizedBox(),
              ListTile(
                title: Row(
                  children: [
                    Image.network(data[index]['app_icon'], width: 35, height: 35,),
                    const SizedBox(width: 10,),
                    Expanded(child: Text(data[index]['app_name']))
                  ],
                ),
                subtitle: Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(top: 10, bottom: 10),
                      child: Image.network(data[index]['app_thumbnail']),
                    ),
                    GestureDetector(
                      onTap: () {
                        downloadAndInstall(url: data[index]['app_url']);
                      },
                      child: Container(
                        padding: const EdgeInsets.all(10),
                        decoration: BoxDecoration(
                          color: Theme.of(context).primaryColor,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Center(
                          child: Text(widget.description,
                            style: const TextStyle(
                                color: Colors.white
                            ),),
                        ),
                      ),
                    )
                  ],
                ),
              ),
              const Divider(),
            ],
          );
        },
      ) : const Center(
      child: CircularProgressIndicator(),
    ),
    );
  }
}
