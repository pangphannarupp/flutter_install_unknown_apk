#include "include/flutter_install_unknown_apk/flutter_install_unknown_apk_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "flutter_install_unknown_apk_plugin.h"

void FlutterInstallUnknownApkPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  flutter_install_unknown_apk::FlutterInstallUnknownApkPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
