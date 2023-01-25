import 'package:flutter/material.dart';
import 'package:flutter_install_unknown_apk/flutter_install_unknown_apk.dart';
import 'package:flutter_install_unknown_apk/service/api.dart';

class MoreApp1 extends StatefulWidget {
  const MoreApp1(
      {Key? key,
      required this.screenTitle,
      required this.downloadText,
      required this.downloadTitle,
      required this.api,
      required this.typeApi})
      : super(key: key);

  final String api;
  final String typeApi;
  final String screenTitle;
  final String downloadText;
  final String downloadTitle;

  @override
  State<MoreApp1> createState() => _MoreApp1State();
}

class _MoreApp1State extends State<MoreApp1> {
  final plugin = FlutterInstallUnknownApk();

  List<dynamic> data = [];

  void getData() async {
    var jsonResponse = await FlutterInstallUnknownApkApi(
            api: widget.api, typeApi: widget.typeApi)
        .getTypesAndAppList();
    setState(() {
      data = jsonResponse;
    });
  }

  void downloadAndInstall({
    required String downloadUrl,
    required String downloadIcon,
    required String downloadThumbnail,
    required String downloadName,
  }) {
    plugin.execute('DOWNLOAD_AND_INSTALL_PLUGIN', {
      'downloadUrl': downloadUrl,
      'downloadIcon': downloadIcon,
      'downloadThumbnail': downloadThumbnail,
      'downloadName': downloadName,
      'downloadTitle': widget.downloadTitle,
      'isUpdate': false,
    });
  }

  @override
  void initState() {
    getData();

    super.initState();
  }

  Widget CardView(dynamic data) {
    return Container(
      margin: const EdgeInsets.all(10),
      child: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: Text(data['type']['type_name'],
                style: const TextStyle(
                  fontWeight: FontWeight.bold
                ),),
              ),
              // GestureDetector(
              //   onTap: () {
              //     print('You are clicking on more app');
              //   },
              //   child: const Icon(
              //     Icons.arrow_right_alt,
              //   ),
              // ),
            ],
          ),
          const SizedBox(
            height: 10,
          ),
          SizedBox(
            height: 170,
            child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: data['app_list'].length,
                itemBuilder: (context, index) {
                  return Container(
                    margin: EdgeInsets.only(
                        right: index < data['app_list'].length - 1 ? 10 : 0),
                    width: 100,
                    child: GestureDetector(
                      onTap: () {
                        downloadAndInstall(
                          downloadUrl: data['app_list'][index]['app_url'],
                          downloadIcon: data['app_list'][index]['app_icon'],
                          downloadThumbnail: data['app_list'][index]['app_thumbnail'],
                          downloadName: data['app_list'][index]['app_name'],
                        );
                      },
                      child: Column(
                        children: [
                          Image.network(
                            data['app_list'][index]['app_icon'],
                            width: 80,
                            height: 80,
                          ),
                          const SizedBox(
                            height: 10,
                          ),
                          Text(
                            data['app_list'][index]['app_name'],
                            maxLines: 1,
                            textAlign: TextAlign.center,
                            overflow: TextOverflow.ellipsis,
                          ),
                          const SizedBox(
                            height: 5,
                          ),
                          Container(
                            padding: const EdgeInsets.all(5),
                            decoration: BoxDecoration(
                              color: Theme.of(context).primaryColor,
                              borderRadius: BorderRadius.circular(30),
                            ),
                            child: Center(
                              child: Text(widget.downloadText,
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                style: const TextStyle(
                                    color: Colors.white,
                                  fontSize: 11
                                ),),
                            ),
                          )
                        ],
                      ),
                    ),
                  );
                }),
          )
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.screenTitle),
        elevation: 0,
        actions: [
          IconButton(
              onPressed: () {
                setState(() {
                  data = [];
                });
                getData();
              },
              icon: const Icon(Icons.refresh))
        ],
      ),
      body: data.isNotEmpty
          ? ListView.builder(
              itemCount: data.length,
              itemBuilder: (context, index) {
                return CardView(data[index]);
              },
            )
          : const Center(
              child: CircularProgressIndicator(),
            ),
    );
  }
}
