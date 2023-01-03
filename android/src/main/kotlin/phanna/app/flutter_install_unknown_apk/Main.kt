package phanna.app.flutter_install_unknown_apk

import android.Manifest
import android.app.Activity
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
    override fun execute(param: Map<String, Any>?) {
        //installtion permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context!!.packageManager!!.canRequestPackageInstalls()) {
                activity!!.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", context!!.packageName))), 1
                )
            }
        }

        checkPermission()
    }

    private val permissionRequestCode: Int = 1

    private fun checkPermission() {
        val listOfPermission = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        val permissionUtil = PermissionUtil(activity!!, listOfPermission, permissionRequestCode)
        if(permissionUtil.checkPermissions()) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == permissionRequestCode && data != null){
            //val imageUri = getImageUri(data)
            //callbackSuccess(Util.getInstance().getPath(context!!, imageUri!!)!!)
        } else if(requestCode == permissionRequestCode && resultCode == Activity.RESULT_CANCELED) {
            //callbackError(ERROR_CODE_USER_CANCEL)
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
                        //callbackError(ERROR_CODE_PERMISSION_DENIED)
                        return
                    }
                }

                //openCamera()
            }
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
}