package com.authcraftandroid.user_authentication.forgot_password

import android.app.Dialog
import android.content.Intent
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
import com.authcraftandroid.databinding.ActivityVerifyCodeBinding
import com.authcraftandroid.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyCodeBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        val email_address = intent.getStringExtra("email_address")!!

        binding.verifyButton.setOnClickListener {
            val verifyCode = binding.verificationCodeEditText.text.toString()

            if (validateInput(verifyCode)) {
                // Call the function here
                performVerifyCode(email_address, verifyCode)
            }
        }
    }

    private fun performVerifyCode(mobileNumber: String, verifyCode: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a request
        val forgotPasswordRequest = ForgotPasswordRequest(mobileNumber, verifyCode)

        showLoadingDialog()
        // Make the API call
        apiService.checkVerificationCode(forgotPasswordRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            val intent = Intent(this@VerifyCodeActivity, ResetPasswordActivity::class.java)
                            intent.putExtra("email_address", loginResponse.result.email_address)
                            intent.putExtra("password_reset_code", verifyCode)
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
    private fun validateInput(verificationCode: String): Boolean {
        if (verificationCode.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Verification code is required")
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
