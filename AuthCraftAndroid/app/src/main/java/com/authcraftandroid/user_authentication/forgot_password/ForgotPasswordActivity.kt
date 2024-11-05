package com.authcraftandroid.user_authentication.forgot_password

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.models.ForgotPasswordRequest
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.MainActivity
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityForgotPasswordBinding
import com.authcraftandroid.databinding.ActivityLoginBinding
import com.authcraftandroid.user_authentication.LoginActivity
import com.authcraftandroid.utils.ApiService
import com.authcraftandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.sendCodeButton.setOnClickListener {
            val emailAddress = binding.emailAddressEditText.text.toString()

            if (validateInput(emailAddress)) {
                // Call the function here
                performSendForgotPasswordRequest(emailAddress)
            }
        }

    }

    private fun validateInput(emailAddress: String): Boolean {
        if (emailAddress.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Email address is required")
            return false
        }
        return true
    }
    private fun performSendForgotPasswordRequest(emailAddress: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a send forgot password request
        val forgotPasswordRequest = ForgotPasswordRequest(emailAddress,"")

        showLoadingDialog()
        // Make the API call
        apiService.sendPasswordResetCode(forgotPasswordRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            val intent = Intent(this@ForgotPasswordActivity, VerifyCodeActivity::class.java)
                            intent.putExtra("email_address", emailAddress)
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
