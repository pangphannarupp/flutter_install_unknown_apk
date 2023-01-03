package phanna.app.flutter_install_unknown_apk

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.flutter.BuildConfig
import java.io.File

@Suppress("DEPRECATION")
class Main: Plugin() {
    private val permissionUnknownApkRequestCode: Int = 1
    private val permissionRequestCode: Int = 2
    private val url = "https://bmoon.club/database-php/com.ppplaylist.korean_movies/com.ppplaylist.korean_movies.1.5.2.apk"
    private val fileName = "app"

    override fun execute(param: Map<String, Any>?) {
        //installtion permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context!!.packageManager!!.canRequestPackageInstalls()) {
                activity!!.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", context!!.packageName))), permissionUnknownApkRequestCode
                )
            } else {
                checkPermission()
            }
        }

    }

    private fun checkPermission() {
        val listOfPermission = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        val permissionUtil = PermissionUtil(activity!!, listOfPermission, permissionRequestCode)
        if(permissionUtil.checkPermissions()) {
            download(url, fileName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == permissionUnknownApkRequestCode){
            checkPermission()
        }
    }

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

                download(url, fileName)
            }
        }
    }

    private fun download(url: String, fileName: String) {
        try {
            var downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileUrl = Uri.parse(url)
            val request = DownloadManager.Request(fileUrl)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().toString(), File.separator + fileName + ".apk")
            downloadManager.enqueue(request)

        } catch (e: Exception) {
            e.printStackTrace()
            //callbackError(0)
        }
    }

    fun installAPK() {
        val PATH: String = Environment.getExternalStorageDirectory().toString() + "/" + "apkname.apk"
        val file = File(PATH)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(activity!!.applicationContext, File(PATH)),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                activity!!.applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Log.e("TAG", "Error in opening the file!")
            }
        } else {
            Toast.makeText(activity!!.applicationContext, "installing", Toast.LENGTH_LONG).show()
        }
    }

    private fun uriFromFile(context: Context?, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context!!, context.packageName + ".provider", file!!)
        } else {
            Uri.fromFile(file)
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