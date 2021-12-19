package pk.event.booking.app.utils

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast

class Utils() {

    companion object {
        val SHAREDPREF = "App_SP"
        val USER_TOKEN = "token"
        val USER_NAME  = "username"
        val FIRST_NAME = "first_name"
        val LAST_NAME = "last_name"
        val EMAIL = "email"
        val CITY = "city"
        var TOKEN = ""

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
        fun showProgressDialog(context: Context?, message: String?): ProgressDialog? {
            val mDialog = ProgressDialog(context)
            mDialog.setMessage(message)
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mDialog.setCancelable(false)
            mDialog.show()
            return mDialog
        }

    }

}