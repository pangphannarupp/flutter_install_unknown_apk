@file:Suppress("DEPRECATION")

package phanna.app.flutter_install_unknown_apk.plugin

import android.content.*
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import phanna.app.flutter_install_unknown_apk.config.Plugin
import phanna.app.flutter_install_unknown_apk.util.AppUtil
import java.util.*
import kotlin.system.exitProcess

class ApplicationPlugin: Plugin() {
    private var result: MutableMap<String, Any>? = null

    private val intentAppSettingCode: Int = 1

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
}