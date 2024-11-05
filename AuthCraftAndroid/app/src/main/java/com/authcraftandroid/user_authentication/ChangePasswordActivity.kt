package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.MainActivity
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityChangePasswordBinding
import com.authcraftandroid.databinding.ActivityUserProfileBinding
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.PasswordChangeRequest
import com.authcraftandroid.models.ResetPasswordRequest
import com.authcraftandroid.utils.ApiService
import com.authcraftandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        sessionManager = SessionManager(this)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.changePasswordButton.setOnClickListener {
            if (isValidate()) {
                val oldPassword = binding.tilOldPassword.editText!!.text.toString()
                val password = binding.tilNewPassword.editText!!.text.toString()
                val confPassword = binding.tilConfPassword.editText!!.text.toString()
                // Call the function here
                performChangePassword(oldPassword, password, confPassword)
            }
        }
    }

    private fun performChangePassword(oldPassword: String, password: String, confPassword: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a request
        val changePasswordRequest = PasswordChangeRequest(oldPassword, password, confPassword)

        showLoadingDialog()
        // Make the API call
        apiService.changePassword(sessionManager.userID.toString(), changePasswordRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            showDialogBoxF(
                                SweetAlertDialog.SUCCESS_TYPE,
                                "SUCCESS",
                                "Change password Successfully  "
                            )
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
    private fun isValidate(): Boolean = validatePassword() && validateConfirmPassword() && validateOldPassword()
    private fun validateOldPassword(): Boolean {
        if (binding.edtOldPassword.text.toString().trim().isEmpty()) {
            binding.tilOldPassword.error = "Required Field!"
            binding.edtOldPassword.requestFocus()
            return false
        } else {
            binding.tilOldPassword.isErrorEnabled = false
        }
        return true
    }
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
    private fun showDialogBoxF(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
                val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
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
