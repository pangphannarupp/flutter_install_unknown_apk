package phanna.app.flutter_install_unknown_apk

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class RUtil {

    companion object {
        private var instance: RUtil? = null

        fun getInstance(): RUtil {
            if(instance == null) {
                instance = RUtil()
            }

            return instance!!
        }
    }

    fun getLayout(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "layout", context.applicationContext.packageName)
    }

    fun getDrawable(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.applicationContext.packageName)
    }

    fun getColor(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "color", context.applicationContext.packageName)
    }

    fun getFont(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "font", context.applicationContext.packageName)
    }

    fun getId(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "id", context.applicationContext.packageName)
    }

    fun getXml(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "xml", context.applicationContext.packageName)
    }

    fun getType(context: Context, type: String, name: String): Int {
        return context.resources.getIdentifier(name, type, context.applicationContext.packageName)
    }
}