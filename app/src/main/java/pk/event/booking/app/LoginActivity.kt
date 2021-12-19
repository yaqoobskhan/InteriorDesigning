package pk.event.booking.app

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import pk.event.booking.app.data.UserLogin
import pk.event.booking.app.data.UserLoginResponse
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       // et_login_username.setText("test")
        //et_login_password.setText("qqqqqq")


        sessionManager = SessionManager(this)
    }

    fun signInButtonOnClick(view: View) {
        if (validateFields()) {
            progressDialog = Utils.showProgressDialog(this, "Please wait...")!!
            signIn(et_login_username.text.toString(), et_login_password.text.toString())
        }
    }

    fun signUpButtonOnClickLogin(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun signIn(username: String, password: String) {

        val user = UserLogin(username, password)

        val apiInterface = ApiInterface.create().signIn(user)

        apiInterface.enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(
                call: Call<UserLoginResponse>?,
                response: Response<UserLoginResponse>?
            ) {
                progressDialog.dismiss()

                if (response?.isSuccessful == true) {
                    try {

                        var token: String =
                            response.body().token

                        Toast.makeText(
                            this@LoginActivity,
                            "User Logged in successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        sessionManager.saveAuthToken(token)

                        startActivity(Intent(applicationContext, DrawerMainActivity::class.java))
                        finish()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid username/password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>?, t: Throwable?) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, call.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun validateFields(): Boolean {
        if (et_login_username.text.isEmpty()) {
            Utils.showToast(applicationContext, "Please enter username")
            return false
        } else if (et_login_password.text.isEmpty()) {
            Utils.showToast(applicationContext, "Please enter password")
            return false
        }
        return true
    }

 /*   private fun saveTokenInSp(token: String) {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            Utils.SHAREDPREF,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Utils.USER_TOKEN, token)
        editor.apply()
        editor.commit()
    }*/

}