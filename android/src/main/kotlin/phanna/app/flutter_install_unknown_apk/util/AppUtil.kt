@file:Suppress("DEPRECATION")

package phanna.app.flutter_install_unknown_apk.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import kotlin.math.sqrt

class AppUtil {

    companion object {
        private var instance: AppUtil? = null

        fun getInstance(): AppUtil {
            if(instance == null) {
                instance = AppUtil()
            }

            return instance!!
        }
    }

    /**
     * check theme mode => {dark, light, undefined}
     *
     * @param context
     * @return {dark, light, undefined}
     */
    fun checkThemeMode(context: Context): String {
        var mode = "light"
        val nightModeFlags: Int = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> mode = "dark"
            Configuration.UI_MODE_NIGHT_NO -> mode = "light"
            Configuration.UI_MODE_NIGHT_UNDEFINED -> mode = "undefined"
        }

        return mode
    }

    /**
     * check screen orientation => {landscape, portrait}
     *
     * @param activity
     * @return {landscape, portrait}
     */
    fun checkOrientation(activity: Activity): String {
        val orientation: Int = activity.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            "landscape"
        } else {
            "portrait"
        }
    }

    fun isTablet(context: Context): Boolean {
        val var1 = context.resources.configuration.screenLayout and 15 >= 3
        val var2 = Build.MODEL
        return if (var1) {
            val var3 = DisplayMetrics()
            val var4 = context as Activity
            var4.windowManager.defaultDisplay.getMetrics(var3)
            val var5 = var3.heightPixels.toFloat() / var3.ydpi
            val var6 = var3.widthPixels.toFloat() / var3.xdpi
            val var7 = sqrt((var6 * var6 + var5 * var5).toDouble())
            if (var7 >= 6.5) {
                var2 != "SM-T255S"
            } else {
                false
            }
        } else {
            false
        }
    }
}