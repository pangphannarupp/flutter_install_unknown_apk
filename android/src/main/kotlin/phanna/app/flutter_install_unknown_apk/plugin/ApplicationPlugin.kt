@file:Suppress("DEPRECATION")

package phanna.app.flutter_install_unknown_apk.plugin

import android.app.ProgressDialog
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import androidx.annotation.RequiresApi
import phanna.app.flutter_install_unknown_apk.util.AppUtil
import phanna.app.flutter_install_unknown_apk.util.RUtil
import phanna.app.flutter_install_unknown_apk.config.Plugin
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class ApplicationPlugin: Plugin() {
    private var result: MutableMap<String, Any>? = null

    private val intentAppSettingCode: Int = 1
    var progressDialog: ProgressDialog? = null

    /**
     * Use to define type of error for client
     * **/
    private val ERROR_CODE_NO_FUNCTION = -1
    private val ERROR_CODE_CANNOT_OPEN_APP_SETTING = -2

    override fun execute(param: Map<String, Any>?) {
        when (param!!["type"].toString().lowercase()) {
            "exit" -> exitApplication(param["status"] as Int)
            "finish" -> finishApplication()
            "restart" -> restartApplication()
            "app_setting" -> openAppSetting()
            "app_info" -> checkAppInfo()
            "battery_level" -> checkBatteryLevel()
            //"increase_volume" -> increaseVolume()
            //"decrease_volume" -> decreaseVolume()
            //else -> super.noImplementation(callback)
        }
    }

    /**
     * response success to flutter client
     * {result: true}
     */
    private fun callbackSuccess() {
        result = mutableMapOf()
        result!!["result"] = true
        callback!!.success(result)
    }
    private fun callbackSuccess(batter: Float) {
        result = mutableMapOf()
        result!!["result"] = true
        result!!["batter"] = batter
        callback!!.success(result)
    }

    /**
     * response fail to flutter client
     * {result: false, errorCode: Int, errorCode: String}
     * @param errorCode
     */
    private fun callbackError(errorCode: Int) {
        val errorMessage = when (errorCode) {
            ERROR_CODE_NO_FUNCTION -> "Not implementation."
            ERROR_CODE_CANNOT_OPEN_APP_SETTING -> "Cannot open app setting."
            else -> "Unknown error"
        }

        result = mutableMapOf()
        result!!["result"] = false
        result!!["errorCode"] = errorCode
        result!!["errorMessage"] = errorMessage
        callback!!.success(result)
    }

    /**
     * exit application by status
     * @param status
     */
    private fun exitApplication(status: Int) {
        exitProcess(status)
    }

    /**
     * exit current activity
     */
    private fun finishApplication() {
        activity!!.finish()
    }

    private fun restartApplication() {
//        val intent = Intent(context!!, Class.forName("${context!!.applicationContext.packageName}.MainActivity"))
//        val mPendingIntentId: Int = 1
//        val mPendingIntent = PendingIntent.getActivity(
//            context!!,
//            mPendingIntentId,
//            intent,
//            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        val mgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
//        exitProcess(0)

        activity!!.recreate()
    }

    /**
     * open activity setting of the app
     */
    private fun openAppSetting() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + activity!!.packageName))
            activity!!.startActivityForResult(intent, intentAppSettingCode)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            callbackError(ERROR_CODE_CANNOT_OPEN_APP_SETTING)
        }
    }

    private fun checkAppInfo() {
        result = mutableMapOf()
        result!!["result"] = true
        result!!["os"] = "Android"
        result!!["model"] = Build.MODEL
        result!!["deviceType"] = if (AppUtil.getInstance().isTablet(context!!)) "tablet" else "phone"
        result!!["appName"] = context!!.applicationInfo.loadLabel(context!!.packageManager).toString()
        result!!["appVersion"] = context!!.packageManager.getPackageInfo(context!!.packageName, 0).versionName
        result!!["packageName"] = context!!.packageName
        result!!["themeMode"] = AppUtil.getInstance().checkThemeMode(context!!)
        result!!["orientation"] = AppUtil.getInstance().checkOrientation(activity!!)
        result!!["language"] = Locale.getDefault().displayLanguage
        result!!["locale"] = Locale.getDefault().country
        callback!!.success(result)
    }

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryLevel = level * 100 / scale.toFloat()

            callbackSuccess(batteryLevel)
        }
    }

    private fun checkBatteryLevel() {
        activity!!.registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        Handler().postDelayed({
            activity!!.unregisterReceiver(batteryInfoReceiver)
        }, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun increaseVolume() {
        val audioManager =
        activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
        showCurrentVolume(audioManager)

//        val handle: Handler = @SuppressLint("HandlerLeak")
//        object : Handler() {
//            override fun handleMessage(msg: Message) {
//                super.handleMessage(msg)
//                progressDialog!!.incrementProgressBy(2) // Incremented By Value 2
//            }
//        }



//        Thread {
//            try {
//                while (progressDialog!!.progress <= progressDialog!!.max) {
//                    Thread.sleep(200)
//                    handle.sendMessage(handle.obtainMessage())
//                    if (progressDialog!!.progress == progressDialog!!.max) {
//                        progressDialog!!.dismiss()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun decreaseVolume() {
        val audioManager =
            activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
        showCurrentVolume(audioManager)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showCurrentVolume(audioManager: AudioManager) {
        progressDialog = ProgressDialog(activity!!, RUtil.getInstance()
            .getType(context!!, "style", "DialogTheme"))
        progressDialog!!.max = 100 // Progress Dialog Max Value
        progressDialog!!.setMessage("Volume") // Setting Message
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // Progress Dialog Style Horizontal
        progressDialog!!.show() // Display Progress Dialog
        progressDialog!!.setCancelable(false)
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        println("current volume => $current")
        println("max volume => $max")
        val percent: Int = ((current/max) * 100).roundToInt()
        println("percent volume => $percent")

        progressDialog!!.incrementProgressBy(percent)
        Handler().postDelayed({
            progressDialog!!.dismiss()
        }, 600)
    }

}