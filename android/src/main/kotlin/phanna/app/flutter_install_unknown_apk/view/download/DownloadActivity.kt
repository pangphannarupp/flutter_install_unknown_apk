package phanna.app.flutter_install_unknown_apk.view.download

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import phanna.app.flutter_install_unknown_apk.util.RUtil
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class DownloadActivity: Activity() {
    private val TAG = "DownloadActivity"
    private val permissionUnknownApkRequestCode: Int = 1
    //param from client
    private var downloadUrl: String? = null
    private var downloadIcon: String? = null
    private var downloadThumbnail: String? = null
    private var downloadName: String? = null
    private var downloadTitle: String? = null
    private var isUpdate: Boolean? = null

    private var fileName = ""
    private var executor: ExecutorService = Executors.newFixedThreadPool(1)
    private var progressBarDialog: ProgressDialog? = null

    private var progressBarDownload: ProgressBar? = null
    private var progressBarTitle: TextView? = null
    private var appIcon: ImageView? = null
    private var appThumbnail: ImageView? = null
    private var appName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(RUtil.getInstance().getLayout(this, "activity_download"))

        progressBarDownload = findViewById(RUtil.getInstance().getId(this, "activity_download_progressBar"))
        progressBarTitle = findViewById(RUtil.getInstance().getId(this, "activity_download_progressTitle"))
        appIcon = findViewById(RUtil.getInstance().getId(this, "activity_download_icon"))
        appThumbnail = findViewById(RUtil.getInstance().getId(this, "activity_download_thumbnail"))
        appName = findViewById(RUtil.getInstance().getId(this, "activity_download_appName"))


        //get data from MP3PlayerPlugin
        val intent = intent
        downloadUrl = intent.getStringExtra("downloadUrl")
        downloadIcon = intent.getStringExtra("downloadIcon")
        downloadThumbnail = intent.getStringExtra("downloadThumbnail")
        downloadName = intent.getStringExtra("downloadName")
        downloadTitle = intent.getStringExtra("downloadTitle")
        isUpdate = intent.getBooleanExtra("isUpdate", false)

        Picasso.get().load(downloadIcon).into(appIcon!!)
        Picasso.get().load(downloadThumbnail).into(appThumbnail!!)
        appName!!.text = downloadName
//        Glide.with(this).load(downloadIcon).into(appIcon!!)
//        Glide.with(this).load(downloadThumbnail).into(appThumbnail!!)

        download(downloadUrl!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == permissionUnknownApkRequestCode){
            requestInstallAPK()
        }
    }

    @SuppressLint("Range", "SetTextI18n", "LongLogTag")
    private fun download(downloadUrl: String) {
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
            progressBarDownload!!.progress = 0


            Log.d(TAG, "url => $downloadUrl")
            Log.d(TAG, "fileName => $fileName")
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileUrl = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(fileUrl)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("application/vnd.android.package-archive")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + fileName)
//            downloadManager.enqueue(request)
//            context!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            //Run a task in a background thread to check download progress
            val downloadId = downloadManager.enqueue(request)
            executor.execute {
                var progress = 0
                var isDownloadFinished = false
                while (!isDownloadFinished) {
                    try {
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
                                    //progressBarDialog!!.dismiss()
                                    finish()
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
                    } catch (e: NullPointerException) {
                        Log.d(TAG, "Exception => ${e.printStackTrace()}")
                        Log.d(TAG, "Exception progress => $progress")
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception => ${e.printStackTrace()}")
            //callbackError(100)
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

    @SuppressLint("SetTextI18n")
    private val mainHandler = Handler(
        Looper.getMainLooper()
    ) { msg ->
        if (msg.what == 1) {
            val downloadProgress = msg.arg1
//            progressBarDialog!!.progress = downloadProgress
            progressBarDownload!!.progress = downloadProgress
            progressBarTitle!!.text = "$downloadTitle $downloadProgress%"
        }
        true
    }

    private fun requestInstallAPK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager!!.canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", packageName))), permissionUnknownApkRequestCode
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
                uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            startActivity(intent)

            if (isUpdate!!) {
                exitProcess(0)
            }
        } else {
            //callbackError(3)
        }
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

    override fun onBackPressed() {

    }
}