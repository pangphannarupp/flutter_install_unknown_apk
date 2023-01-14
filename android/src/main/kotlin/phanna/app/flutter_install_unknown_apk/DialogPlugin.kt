@file:Suppress("DEPRECATION")

package phanna.app.flutter_install_unknown_apk

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import java.util.*

class DialogPlugin: Plugin() {
    private var param: Map<String, Any>? = null
    private var result: MutableMap<String, Any>? = null

    private var progressDialog: ProgressDialog? = null

    private var textConfirm: String? = null
    private var textCancel: String? = null
    private var textOk: String? = null

    override fun execute(param: Map<String, Any>?) {
        this.param = param!!
        textConfirm = param["text_confirm"].toString()
        textCancel = param["text_cancel"].toString()
        textOk = param["text_ok"].toString()

        val type = param["type"].toString()
        when (type.lowercase()) {
            "confirm" -> alertConfirmMessage(
                param["text_title"].toString(),
                param["text_message"].toString(),
                onConfirm = this::callbackSuccess,
                onCancel = {
                    callbackError(-1, "Cancel")
                }
            )
            "alert" -> alertMessage(param["text_title"].toString(), param["text_message"].toString()) {
                callbackSuccess()
            }
            "alert_loading" -> alertLoading(param["message"].toString())
            "hide_loading" -> hideLoading()
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

    /**
     * response fail to flutter client
     * {result: false, errorCode: Int, errorCode: String}
     * @param errorCode
     * @param errorMessage describe meaning of error
     */
    private fun callbackError(errorCode: Int, errorMessage: String) {
        result = mutableMapOf()
        result!!["result"] = false
        result!!["errorCode"] = errorCode
        result!!["errorMessage"] = errorMessage
        callback!!.success(result)
    }

    /**
     * alertConfirmMessage
     * @param title
     * @param description
     * @param onCancel optional function
     * @param onConfirm optional function
     * @usage used to alert confirm popup
     * */
    private fun alertConfirmMessage(title: String, description: String, onCancel: (() -> Unit)? = null, onConfirm: (() -> Unit)? = null) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!, RUtil.getInstance().getType(context!!, "style", "DialogTheme"))
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(description)
        builder.setPositiveButton(textConfirm) { _, _ ->
            if (onConfirm != null) {
                onConfirm()
            }
        }
        builder.setNegativeButton(textCancel) { _, _ ->
            if (onCancel != null) {
                onCancel()
            }
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
    }

    /**
     * alertMessage
     * @param title
     * @param description
     * @param onConfirm optional function
     * @usage used to alert confirm popup
     * */
    private fun alertMessage(title: String, description: String, onConfirm: (() -> Unit)? = null) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!, RUtil.getInstance().getType(context!!, "style", "DialogTheme"))
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(description)
        builder.setPositiveButton(textOk) { _, _ ->
            if (onConfirm != null) {
                onConfirm()
            }
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
    }

    private fun alertLoading(message: String) {
        //show loading dialog
        progressDialog = ProgressDialog(activity!!, RUtil.getInstance().getType(context!!, "style", "DialogTheme"))
        progressDialog!!.setMessage(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()

        println("message => $message")

        callbackSuccess()
    }

    private fun hideLoading() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            callbackSuccess()
        } else {
            callbackError(-1, "Popup not exist.")
        }
    }
}