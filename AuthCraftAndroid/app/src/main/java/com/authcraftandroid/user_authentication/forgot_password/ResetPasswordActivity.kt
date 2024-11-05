package com.authcraftandroid.user_authentication.forgot_password

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.ResetPasswordRequest
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityResetPasswordBinding
import com.authcraftandroid.databinding.ActivityVerifyCodeBinding
import com.authcraftandroid.user_authentication.LoginActivity
import com.authcraftandroid.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        val email_address = intent.getStringExtra("email_address")!!
        val password_reset_code = intent.getStringExtra("password_reset_code")!!
        binding.resetPasswordButton.setOnClickListener {
            if (isValidate()) {
                val password = binding.tilNewPassword.editText!!.text.toString()
                val confPassword = binding.tilConfPassword.editText!!.text.toString()
                // Call the function here
                performSendResetPassword(email_address, password_reset_code, password, confPassword)
            }
        }
    }

    private fun performSendResetPassword(email_address: String, password_reset_code: String, password: String, confPassword: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a request
        val resetPasswordRequest = ResetPasswordRequest(email_address, password_reset_code, password, confPassword)

        showLoadingDialog()
        // Make the API call
        apiService.userResetPassword(resetPasswordRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
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
    private fun isValidate(): Boolean = validatePassword() && validateConfirmPassword()
    private fun validatePassword(): Boolean {
        if (binding.edtNewPassword.text.toString().trim().isEmpty()) {
            binding.tilNewPassword.error = "Required Field!"
            binding.edtNewPassword.requestFocus()
            return false
        } else if (binding.edtNewPassword.text.toString().length < 3) {
            binding.tilNewPassword.error = "password can't be less than 3"
            binding.edtNewPassword.requestFocus()
            return false
        } else {
            binding.tilNewPassword.isErrorEnabled = false
        }
        return true
    }
    private fun validateConfirmPassword(): Boolean {
        if (binding.edtConfPassword.text.toString().trim().isEmpty()) {
            binding.tilConfPassword.error = "Required Field!"
            binding.edtConfPassword.requestFocus()
            return false
        } else if (binding.edtConfPassword.text.toString().length < 3) {
            binding.tilConfPassword.error = "password can't be less than 3"
            binding.edtConfPassword.requestFocus()
            return false
        } else {
            binding.tilConfPassword.isErrorEnabled = false
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
