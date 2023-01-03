package phanna.app.flutter_install_unknown_apk

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

abstract class Plugin: Error() {
    open var context: Context? = null
    open var activity: Activity? = null
    open var callback: MethodChannel.Result? = null
    open var binaryMessenger: BinaryMessenger? = null
    open var channel: MethodChannel? = null
    abstract fun execute(param: Map<String, Any>?)
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    open fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {}
    open fun onCreate(){}
    open fun onStart(){}
    open fun onPause(){}
    open fun onResume(){}
    open fun onStop(){}
    open fun onDestroy(){}
    open fun onAny(){}
}