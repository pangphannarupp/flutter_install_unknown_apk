package phanna.app.flutter_install_unknown_apk.config

import phanna.app.flutter_install_unknown_apk.plugin.ApplicationPlugin
import phanna.app.flutter_install_unknown_apk.plugin.DownloadAndInstallPlugin

val pluginList: Array<PluginModel> = arrayOf(
    PluginModel(
        pluginKey = "APPLICATION_PLUGIN",
        pluginClass = ApplicationPlugin()
    ),
    PluginModel(
        pluginKey = "DOWNLOAD_AND_INSTALL_PLUGIN",
        pluginClass = DownloadAndInstallPlugin()
    ),
)