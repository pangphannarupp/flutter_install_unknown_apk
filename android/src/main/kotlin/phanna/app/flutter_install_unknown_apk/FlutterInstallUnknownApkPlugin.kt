package phanna.app.flutter_install_unknown_apk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** FlutterInstallUnknownApkPlugin */
class FlutterInstallUnknownApkPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener,
  LifecycleEventObserver {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private val tag = "FlutterInstallUnknownApkPlugin"
  private lateinit var channel : MethodChannel
  private var context: Context? = null
  private var activity: Activity? = null
  private var param: Map<String, Any>? = null
  private var plugin: Plugin? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_install_unknown_apk")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    val args = call.arguments as Map<String, Any>
    if (call.hasArgument("param")) {
      param = args["param"] as Map<String, Any>
    }

    if(call.method == "initialize") {
      plugin = Main()
      plugin!!.context = context
      plugin!!.activity = activity
      plugin!!.execute(param)
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    //Log.d(tag, "onAttachedToActivity")
    activity = binding.activity
//    activityPluginBinding = binding
//
//    activityPluginBinding!!.addActivityResultListener(this)
//    activityPluginBinding!!.addRequestPermissionsResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    print("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    print("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    print("Not yet implemented")
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    plugin!!.onActivityResult(requestCode, resultCode, data)

    return true
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ): Boolean {
    plugin!!.onRequestPermissionsResult(requestCode, permissions, grantResults)

    return true
  }

  @SuppressLint("LongLogTag")
  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    when (event) {
      Lifecycle.Event.ON_PAUSE -> {
        Log.d(tag, "onPause")
        plugin!!.onPause()
      }
      Lifecycle.Event.ON_RESUME -> {
        Log.d(tag, "onResume")
        plugin!!.onResume()
      }
      Lifecycle.Event.ON_DESTROY -> {
        Log.d(tag, "onDestroy")
        plugin!!.onDestroy()
      }
      Lifecycle.Event.ON_CREATE -> {
        Log.d(tag, "onCreate")
        plugin!!.onCreate()
      }
      Lifecycle.Event.ON_START -> {
        Log.d(tag, "onStart")
        plugin!!.onStart()
      }
      Lifecycle.Event.ON_STOP -> {
        Log.d(tag, "onStop")
        plugin!!.onStop()
      }
      Lifecycle.Event.ON_ANY -> {
        Log.d(tag, "onAny")
        plugin!!.onAny()
      }
    }
  }
}
