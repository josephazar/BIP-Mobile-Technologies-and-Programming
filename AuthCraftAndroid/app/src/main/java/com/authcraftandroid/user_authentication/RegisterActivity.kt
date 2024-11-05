package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.models.RegistrationRequest
import com.authcraftandroid.models.RegistrationResponse
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityRegisterBinding
import com.authcraftandroid.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.loginId.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.resisterId.setOnClickListener {
            val fullName = binding.tilFullName.editText!!.text.toString()
            val phoneNumber = binding.tilMobileNumber.editText!!.text.toString()
            val emailAddress = binding.tilEmailAddress.editText!!.text.toString()
            val password = binding.tilNewPassword.editText!!.text.toString()
            val confPassword = binding.tilConfPassword.editText!!.text.toString()

            if (isValidate()){
                if (password != confPassword) {
                    showDialogBoxForValidation(
                        SweetAlertDialog.WARNING_TYPE,
                        "Validation",
                        "Password not match"
                    )
                    return@setOnClickListener
                }

                val registrationRequest = RegistrationRequest(
                    fullName,
                    phoneNumber,
                    emailAddress,
                    password,
                    confPassword
                )
                register(registrationRequest)
            }
        }
    }

    private fun register(registerM: RegistrationRequest){
        val apiService = ApiService.CreateApi()
        showLoadingDialog()
        // Make the API call
        apiService.userRegistration(registerM)
            .enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                dismissLoadingDialog()
                                showFDialogBox(
                                    SweetAlertDialog.SUCCESS_TYPE,
                                    "SUCCESS",
                                    "Save Successfully  "
                                )
                            } else {
                                dismissLoadingDialog()
                                showDialogBox(
                                    SweetAlertDialog.WARNING_TYPE,
                                    "Waring",
                                    "Failed. Try Later."
                                )
                            }
                        } else {
                            dismissLoadingDialog()
                            showDialogBox(
                                SweetAlertDialog.ERROR_TYPE,
                                "Error",
                                "Response NULL value. Try later"
                            )
                        }
                    } else {
                        dismissLoadingDialog()
                        showDialogBox(
                            SweetAlertDialog.ERROR_TYPE,
                            "Error",
                            "Response failed. Try later"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<RegistrationResponse>,
                    t: Throwable
                ) {
                    dismissLoadingDialog()
                    showDialogBox(
                        SweetAlertDialog.ERROR_TYPE,
                        "Error",
                        "Network error"
                    )
                }
            })
    }
    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }
    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }
    private fun showDialogBoxForValidation(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
            }
        sweetAlertDialog.show()
    }
    private fun isValidate(): Boolean =
        validateFullName() && validateMobileNumber() && validateEmailAddress()
                && validatePassword() && validateConfirmPassword()
    private fun validateFullName(): Boolean {
        if (binding.edtFullName.text.toString().trim().isEmpty()) {
            binding.tilFullName.error = "Required Field!"
            binding.edtFullName.requestFocus()
            return false
        } else {
            binding.tilFullName.isErrorEnabled = false
        }
        return true
    }
    private fun validateMobileNumber(): Boolean {
        if (binding.edtMobileNumber.text.toString().trim().isEmpty()) {
            binding.tilMobileNumber.error = "Required Field!"
            binding.edtMobileNumber.requestFocus()
            return false
        } else {
            binding.tilMobileNumber.isErrorEnabled = false
        }
        return true
    }
    private fun validateEmailAddress(): Boolean {
        if (binding.edtEmailAddress.text.toString().trim().isEmpty()) {
            binding.tilEmailAddress.error = "Required Field!"
            binding.edtEmailAddress.requestFocus()
            return false
        } else {
            binding.tilEmailAddress.isErrorEnabled = false
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
    private fun showFDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        sweetAlertDialog.show()
    }

}
