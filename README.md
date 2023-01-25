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
- screen 0
```dart
String apiUrl = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';
MoreApp0(
  screenTitle: 'កម្មវិធីផ្សេងទៀត',
  downloadText: 'ទាញយក និងដំឡើង',
  downloadTitle: 'កំពុងទាញយក... ',
  api: apiUrl
);
```
- screen 1
```dart
String apiUrl = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/api.json?r=${Random().nextInt(1000)}';
String typeApiUrl = 'https://raw.githubusercontent.com/pangphannarupp/appstore/main/type_api.json?r=${Random().nextInt(1000)}';
MoreApp1(
  screenTitle: 'កម្មវិធីផ្សេងទៀត',
  downloadText: 'ទាញយក និងដំឡើង',
  downloadTitle: 'កំពុងទាញយក... ',
  api: apiUrl,
  typeApi: typeApiUrl,
);
```

## Data
- App
```json
{
    "app_name": "Application Name",
    "app_id": "com.app.name",
    "app_version": "1.0.0",
    "app_url": "com.app.name.1.0.0.apk",
    "app_icon": "com.app.name.icon.png",
    "app_thumbnail": "com.app.name.thumbnail.png",
    "type_id": 1
}
```
- App Type
```json
{
  "type_name": "Education",
  "type_id": 1
}
```