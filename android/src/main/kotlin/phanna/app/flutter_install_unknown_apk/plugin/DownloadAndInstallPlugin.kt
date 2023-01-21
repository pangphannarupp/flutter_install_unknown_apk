package phanna.app.flutter_install_unknown_apk.plugin

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import phanna.app.flutter_install_unknown_apk.util.PermissionUtil
import phanna.app.flutter_install_unknown_apk.config.Plugin
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Suppress("DEPRECATION")
class DownloadAndInstallPlugin: Plugin() {
    private val TAG = "DownloadAndInstallPlugin"
    private val permissionUnknownApkRequestCode: Int = 1
    private val permissionRequestCode: Int = 2
    //param from client
    private var param: Map<String, Any>? = null

    private var fileName = ""
    private var executor: ExecutorService = Executors.newFixedThreadPool(1)
    private var progressBarDialog: ProgressDialog? = null

    override fun execute(param: Map<String, Any>?) {
        this.param = param!!

        checkPermission()
    }

    private fun checkPermission() {
        val listOfPermission = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        val permissionUtil = PermissionUtil(activity!!, listOfPermission, permissionRequestCode)
        if(permissionUtil.checkPermissions()) {
            download(param!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == permissionUnknownApkRequestCode){
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
                        callbackError(2)
                        return
                    }
                }

                download(param!!)
            }
        }
    }

    @SuppressLint("Range", "SetTextI18n", "LongLogTag")
    private fun download(param: Map<String, Any>) {
        val downloadUrl = param["downloadUrl"].toString()
        val downloadTitle = param["downloadTitle"].toString()
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1, downloadUrl.length)
        executor = Executors.newFixedThreadPool(1)
        try {
            //remove old content before downloading new apk
            val filePath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }

            //show progressbar with download progress
            progressBarDialog = ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT)
            progressBarDialog!!.setMessage(downloadTitle)
            progressBarDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressBarDialog!!.progress = 0
            progressBarDialog!!.setCancelable(false)
            progressBarDialog!!.show()


            Log.d(TAG, "url => $downloadUrl")
            Log.d(TAG, "fileName => $fileName")
            val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileUrl = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(fileUrl)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("application/vnd.android.package-archive")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + fileName)
            //downloadManager.enqueue(request)
            //context!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            //Run a task in a background thread to check download progress
            val downloadId = downloadManager.enqueue(request)
            executor.execute {
                var progress = 0
                var isDownloadFinished = false
                while (!isDownloadFinished) {
                    val cursor: Cursor =
                        downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                    if (cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_RUNNING -> {
                                val totalBytes: Long =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                if (totalBytes > 0) {
                                    val downloadedBytes: Long =
                                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                    progress = (downloadedBytes * 100 / totalBytes).toInt()
                                }
                                Log.d(TAG, "Downloading... $progress%")
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Log.d(TAG, "Download Complete")
                                progress = 100
                                isDownloadFinished = true
                                executor.shutdown()
                                mainHandler.removeCallbacksAndMessages(null)
                                progressBarDialog!!.dismiss()
                                requestInstallAPK()
                            }
                            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {}
                            DownloadManager.STATUS_FAILED -> isDownloadFinished = true
                        }
                        val message: Message = Message.obtain()
                        message.what = 1
                        message.arg1 = progress
                        mainHandler.sendMessage(message)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception => ${e.printStackTrace()}")
            callbackError(100)
        }
    }

//    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(ctxt: Context, intent: Intent) {
//            executor.shutdown()
//            mainHandler.removeCallbacksAndMessages(null)
//            progressBarDialog!!.dismiss()
//
//            requestInstallAPK()
//        }
//    }

    private val mainHandler = Handler(
        Looper.getMainLooper()
    ) { msg ->
        if (msg.what == 1) {
            val downloadProgress = msg.arg1
            progressBarDialog!!.progress = downloadProgress
        }
        true
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
            callbackError(3)
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
            3 -> "File is not exist."
            else -> "Unknown error"
        }

        val result: MutableMap<String, Any> = mutableMapOf()
        result["result"] = false
        result["errorCode"] = errorCode
        result["errorMessage"] = errorMessage
        callback!!.success(result)
    }

    private fun hideProgressbarDialog() {
        if(progressBarDialog != null) {
            progressBarDialog!!.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        hideProgressbarDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressbarDialog()
    }
}