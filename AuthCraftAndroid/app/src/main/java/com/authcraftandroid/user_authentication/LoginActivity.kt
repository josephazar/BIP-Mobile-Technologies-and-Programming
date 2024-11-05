package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.models.LoginRequest
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.MainActivity
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityLoginBinding
import com.authcraftandroid.user_authentication.forgot_password.ForgotPasswordActivity
import com.authcraftandroid.utils.ApiService
import com.authcraftandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.loginButton.setOnClickListener {
            val username = binding.emailAddressEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(username, password)) {
                // Call the login function here
                performLogin(username, password)
            }
        }
        binding.btnLoginWithMobile.setOnClickListener {
            startActivity(Intent(this,LoginWithMobileActivity::class.java))
            finish()
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
            finish()
        }
    }

    private fun performLogin(username: String, password: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a login request
        val loginRequest = LoginRequest(username, password)

        showLoadingDialog()
        // Make the API call
        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful login response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            // Save login information (e.g., in SharedPreferences)
                            saveLoginInfoToSharedPreferences(loginResponse)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
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
    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Email address is required")
            return false
        }
        if (password.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Password is required")
            return false
        }
        return true
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
