package com.example.userprivacydemo
import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return preferences.getString(key, null)
    }

    fun clearData() {
        preferences.edit().clear().apply()
    }
}
/*
The AppPreferences class respects privacy in the following ways:

1-MODE_PRIVATE Access Mode:
When creating the SharedPreferences instance with Context.MODE_PRIVATE, the data stored in SharedPreferences is restricted to your app only. Other apps on the device cannot access this data, making it inaccessible outside of your app's sandbox. This is a fundamental way Android apps protect data from being accessed by other apps.

2-Avoiding Sensitive Data Collection:
The example in this class does not store any particularly sensitive information (e.g., passwords, tokens, or financial details). This is a privacy-respecting practice: only collecting and storing necessary data minimizes the risk associated with potential data leaks or misuse.

3-No External Data Sharing:
This class does not provide any external access to the stored data. It does not share SharedPreferences data with other apps, services, or networks, which helps ensure that data remains isolated within the app.
 */