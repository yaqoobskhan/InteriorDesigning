package pk.event.booking.app.retrofit

import android.content.Context
import android.content.SharedPreferences
import pk.event.booking.app.R
import pk.event.booking.app.data.UserResponseBody
import pk.event.booking.app.utils.Utils

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        var TOKEN = ""

    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, "")
    }

    fun clearPreferences(){
        prefs.edit().clear().apply()
    }

    fun saveUserDataInSp(user: UserResponseBody) {
        val editor = prefs.edit()
        editor.putString(Utils.FIRST_NAME, user.FirstName)
        editor.putString(Utils.LAST_NAME, user.LastName)
        editor.putString(Utils.CITY, user.City)
        editor.putString(Utils.EMAIL, user.Email)
        editor.putString(Utils.USER_NAME, user.Username)
        editor.apply()
    }

    fun getUserDataFromSp(): UserResponseBody? {
        return UserResponseBody(
            FirstName = prefs.getString(Utils.FIRST_NAME, "").toString(),
            LastName = prefs.getString(Utils.FIRST_NAME, null).toString(),
            City = prefs.getString(Utils.CITY, null).toString(),
            Email = prefs.getString(Utils.EMAIL, null).toString(),
            Username = prefs.getString(Utils.USER_NAME, null).toString(),
            Password = "",
            Date = ""
        )
    }


}