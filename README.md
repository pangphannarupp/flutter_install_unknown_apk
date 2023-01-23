## Installation

Add dependency in pubspec.yaml
```xml
  flutter_install_unknown_apk:
    git:
      url: https://github.com/pangphannarupp/flutter_install_unknown_apk.git
```

## Configuration
### Android

1. Add provider in AndroidManifest (in ```<application></application>```)
```xml
<provider
    tools:replace="android:authorities"
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/path" />
</provider>
```
path.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <root-path name="root" path="." />
    <files-path name="files" path="../" />
    <external-path name="external" path="." /> <!-- Environment.getExternalStorageDirectory(). -->
</paths>
```

## Usage
1. Check Update
```dart
String url = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';
void checkAppUpdate() async {
    var application = FlutterInstallUnknownApkApi(api: url);
    var canUpdate = await application.canUpdate();
    if(canUpdate) {
        application.update(
            title: 'កំពុងធ្វើបច្ចុប្បន្នភាព... '
        );
    }
}
```
2. More Application
```dart
String url = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';
Navigator.push(
    context,
    MaterialPageRoute(builder: (context) => MoreApplication(
        screenTitle: 'កម្មវិធីផ្សេងទៀត',
        description: 'ទាញយក និងដំឡើង',
        downloadTitle: 'កំពុងទាញយក... ',
        api: url
    )),
);
```