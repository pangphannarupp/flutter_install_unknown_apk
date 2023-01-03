#ifndef FLUTTER_PLUGIN_FLUTTER_INSTALL_UNKNOWN_APK_PLUGIN_H_
#define FLUTTER_PLUGIN_FLUTTER_INSTALL_UNKNOWN_APK_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace flutter_install_unknown_apk {

class FlutterInstallUnknownApkPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  FlutterInstallUnknownApkPlugin();

  virtual ~FlutterInstallUnknownApkPlugin();

  // Disallow copy and assign.
  FlutterInstallUnknownApkPlugin(const FlutterInstallUnknownApkPlugin&) = delete;
  FlutterInstallUnknownApkPlugin& operator=(const FlutterInstallUnknownApkPlugin&) = delete;

 private:
  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace flutter_install_unknown_apk

#endif  // FLUTTER_PLUGIN_FLUTTER_INSTALL_UNKNOWN_APK_PLUGIN_H_
