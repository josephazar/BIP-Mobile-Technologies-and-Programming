package com.authcraftandroid.utils
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

    var userID: Int?
        get() = sharedPreferences.getInt("id", 0)
        set(userID) = sharedPreferences.edit().putInt("id", userID!!).apply()
    var name: String?
        get() = sharedPreferences.getString("name", "")
        set(name) = sharedPreferences.edit().putString("name", name!!).apply()
    var mobileNumber: String?
        get() = sharedPreferences.getString("mobile_number", "")
        set(mobileNumber) = sharedPreferences.edit().putString("mobile_number", mobileNumber!!).apply()
    var emailAddress: String?
        get() = sharedPreferences.getString("email_address", "")
        set(emailAddress) = sharedPreferences.edit().putString("email_address", emailAddress!!).apply()
}
