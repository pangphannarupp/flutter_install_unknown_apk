package phanna.app.flutter_install_unknown_apk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class Main: Plugin() {
    private val permissionUnknownApkRequestCode: Int = 1
    private val permissionRequestCode: Int = 2
    private var url = ""
    private var fileName = ""
    private var checkInstallReady = false

    override fun execute(param: Map<String, Any>?) {
        url = param!!["url"].toString()
        fileName = url.substring(url.lastIndexOf('/') + 1, url.length)
        checkInstallReady = false
        checkPermission()
    }

    fun requestInstallFromUnknownSource() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context!!.packageManager!!.canRequestPackageInstalls()) {
                activity!!.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", context!!.packageName))), permissionUnknownApkRequestCode
                )
            }
        }
    }

    private fun checkPermission() {
        val listOfPermission = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        val permissionUtil = PermissionUtil(activity!!, listOfPermission, permissionRequestCode)
        if(permissionUtil.checkPermissions()) {
            download(url)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == permissionUnknownApkRequestCode && !checkInstallReady){
            requestInstallAPK()
        }
    }

    @SuppressLint("LongLogTag")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode -> {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        //callbackError(0)
                        return
                    }
                }

                download(url)
            }
        }
    }

    private fun download(url: String) {
        try {
            Log.d("MAIN_DOWNLOAD", "url => $url")
            Log.d("MAIN_DOWNLOAD", "fileName => $fileName")
            val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileUrl = Uri.parse(url)
            val request = DownloadManager.Request(fileUrl)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("application/vnd.android.package-archive")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + fileName)
            downloadManager.enqueue(request)
            context!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MAIN_DOWNLOAD", "Exception => ${e.printStackTrace()}")
            //callbackError(0)
        }
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            requestInstallAPK()
        }
    }

    private fun requestInstallAPK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context!!.packageManager!!.canRequestPackageInstalls()) {
                activity!!.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", context!!.packageName))), permissionUnknownApkRequestCode
                )
            } else {
                installAPK()
            }
        } else {
            installAPK()
        }
    }

    private fun installAPK() {
        checkInstallReady = true
        val filePath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName
        val file = File(filePath)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            var uri = Uri.fromFile(file)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(activity!!, activity!!.packageName + ".fileprovider", file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            activity!!.startActivity(intent)
        } else {
            //callbackError(0)
        }
    }

    /**
     * response success to flutter client
     * {result: true, file_path: String}
     * @param filePath
     */
    private fun callbackSuccess(filePath: String) {
        val result: MutableMap<String, Any> = mutableMapOf()
        result["result"] = true
        result["file_path"] = filePath
        callback!!.success(result)
    }

    /**
     * response fail to flutter client
     * {result: false, errorCode: Int, errorCode: String}
     * @param errorCode
     */
    private fun callbackError(errorCode: Int) {
        val errorMessage = when (errorCode) {
            1 -> "Your device not support this feature."
            2 -> "Permission denied."
            else -> "Unknown error"
        }

        val result: MutableMap<String, Any> = mutableMapOf()
        result["result"] = false
        result["errorCode"] = errorCode
        result["errorMessage"] = errorMessage
        callback!!.success(result)
    }
}