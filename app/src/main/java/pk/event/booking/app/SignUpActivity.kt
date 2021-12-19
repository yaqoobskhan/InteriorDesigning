package pk.event.booking.app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import pk.event.booking.app.data.SignUpResponse
import pk.event.booking.app.data.UserBody
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun signUpButtonOnClick(view: View) {
        if (validateSignUpField()) {
            progressDialog = Utils.showProgressDialog(this, "Please wait...")!!
            userSignUp(
                et_firstname.text.toString(),
                et_lastname.text.toString(),
                et_email.text.toString(),
                spinner_city.selectedItem.toString(),
                et_username.text.toString(),
                et_password.text.toString()
            )
        }
    }

    private fun userSignUp(
        firstName: String,
        lastName: String,
        email: String,
        city: String,
        username: String,
        password: String
    ) {

        try {

            val sdf = SimpleDateFormat("MM-DD-YYYY")
            val currentDate = sdf.format(Date())

            val user = UserBody(username, firstName, lastName, city, email, currentDate, password)

            val apiInterface = ApiInterface.create().registerUser(user)

            apiInterface.enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(
                    call: Call<SignUpResponse>?,
                    response: Response<SignUpResponse>?
                ) {
                    progressDialog.dismiss()
                    if (response?.isSuccessful == true) {
                        try {

                            val message: String = response.body().msg
                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent: Intent = Intent(
                                applicationContext,
                                LoginActivity::class.java
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Something went wrong, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>?, t: Throwable?) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, call.toString(), Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateSignUpField(): Boolean {
        if (et_firstname.text.toString().length < 3) {
            Utils.showToast(applicationContext, "First name must be minimum 3 characters long")
            return false
        } else if (et_lastname.text.toString().length < 3) {
            Utils.showToast(applicationContext, "Last name must be minimum 3 characters long")
            return false
        } else if (et_username.text.toString().trim().length < 3) {
            Utils.showToast(applicationContext, "Username must be minimum 3 characters long")
            return false
        } else if (et_email.text.toString().trim() == "" || !isValidEmail(
                et_email.text.toString().trim()
            )
        ) {
            Utils.showToast(applicationContext, "Please enter valid email")
            return false
        } else if (spinner_city.selectedItemPosition == 0) {
            Utils.showToast(applicationContext, "Please select city")
            return false
        } else if (et_password.text.toString().length < 6) {
            Utils.showToast(applicationContext, "Password must be 6 characters long")
            return false
        } else if (et_password.text.toString() != et_onfirmpassword.text.toString()) {
            Utils.showToast(applicationContext, "Password and confirm password doesn't match.")
            return false
        }
        return true;
    }


    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

}