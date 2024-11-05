package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.databinding.ActivityLoginWithMobileBinding
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.LoginWithMobileRequest
import com.authcraftandroid.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginWithMobileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWithMobileBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWithMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.btnLoginWithEmail.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.loginButton.setOnClickListener {
            val mobileNumber = binding.mobileNumberEditText.text.toString()

            if (validateInput(mobileNumber)) {
                // Call the login function here
                performLogin(mobileNumber)
            }
        }
    }
    private fun validateInput(mobileNumber: String): Boolean {
        if (mobileNumber.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Mobile number is required")
            return false
        }
        return true
    }
    private fun performLogin(mobileNumber: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi()

        // Create a login request
        val loginWithMobileRequest = LoginWithMobileRequest(mobileNumber, "")

        showLoadingDialog()
        // Make the API call
        apiService.loginWithMobile(loginWithMobileRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    // Handle successful login response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            val intent = Intent(this@LoginWithMobileActivity, MobileVerificationActivity::class.java)
                            intent.putExtra("mobile_number", mobileNumber)
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
