package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.LoginWithMobileRequest
import com.authcraftandroid.MainActivity
import com.authcraftandroid.databinding.ActivityMobileVerificationBinding
import com.authcraftandroid.utils.ApiService
import com.authcraftandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileVerificationBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var mobile_number: String
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        val intent = intent
        mobile_number = intent.getStringExtra("mobile_number")!!

        binding.verifyButton.setOnClickListener {
            val verifyCode = binding.verifyCodeEditText.text.toString()

            if (validateInput(verifyCode)) {
                // Call the login function here
                performLogin(mobile_number, verifyCode)
            }
        }
    }

    private fun validateInput(verifyCode: String): Boolean {
        if (verifyCode.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "OTP number is required")
            return false
        }
        return true
    }
    private fun performLogin(mobileNumber: String, verifyCode: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a login request
        val loginWithMobileRequest = LoginWithMobileRequest(mobileNumber, verifyCode)

        showLoadingDialog()
        // Make the API call
        apiService.checkOtp(loginWithMobileRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful login response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            // Save login information (e.g., in SharedPreferences)
                            saveLoginInfoToSharedPreferences(loginResponse)
                            val intent = Intent(this@MobileVerificationActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            dismissLoadingDialog()
                            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring", loginResponse.message)
                        }
                    }else{
                        dismissLoadingDialog()
                        showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response NULL value. Try later")
                    }
                }else{
                    dismissLoadingDialog()
                    showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response failed. Try later")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dismissLoadingDialog()
                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Network error")
            }
        })
    }
    private fun saveLoginInfoToSharedPreferences(result: LoginResponse) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Save properties from the Result data class to SharedPreferences
        editor.putString("id", result.result.id.toString())
        editor.putString("name", result.result.name)
        editor.putString("mobile_number", result.result.mobile_number)
        editor.putString("email_address", result.result.email_address)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
        sessionManager.userID = result.result.id
        sessionManager.name = result.result.name
        sessionManager.mobileNumber = result.result.mobile_number
        sessionManager.emailAddress = result.result.email_address
    }
    private fun showDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
            }
        sweetAlertDialog.show()
    }
    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }
    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }
}
