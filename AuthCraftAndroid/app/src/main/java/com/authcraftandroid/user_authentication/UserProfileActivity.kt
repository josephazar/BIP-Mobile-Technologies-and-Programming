package com.authcraftandroid.user_authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.authcraftandroid.MainActivity
import com.authcraftandroid.R
import com.authcraftandroid.databinding.ActivityUserProfileBinding
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.RegistrationRequest
import com.authcraftandroid.models.RegistrationResponse
import com.authcraftandroid.models.UpdateRequest
import com.authcraftandroid.utils.ApiService
import com.authcraftandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        sessionManager = SessionManager(this)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        getUserInformation()

        binding.updateId.setOnClickListener {
            val fullName = binding.tilFullName.editText!!.text.toString()
            val phoneNumber = binding.tilMobileNumber.editText!!.text.toString()
            val emailAddress = binding.tilEmailAddress.editText!!.text.toString()

            if (isValidate()){
                val updateRequest = UpdateRequest(
                    fullName,
                    phoneNumber,
                    emailAddress
                )
                updateInfo(updateRequest)
            }
        }

    }

    private fun updateInfo(updateRequest: UpdateRequest){
        val apiService = ApiService.CreateApi()
        showLoadingDialog()
        // Make the API call
        apiService.userUpdate(sessionManager.userID.toString(), updateRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                sessionManager.name = response.result.name
                                sessionManager.mobileNumber = response.result.mobile_number
                                sessionManager.emailAddress = response.result.email_address
                                dismissLoadingDialog()
                                showFDialogBox(
                                    SweetAlertDialog.SUCCESS_TYPE,
                                    "SUCCESS",
                                    "Update Successfully  "
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
                    call: Call<LoginResponse>,
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
    private fun getUserInformation(){
        val apiService = ApiService.CreateApi()
        showLoadingDialog()
        // Make the API call
        apiService.userDetails(sessionManager.userID.toString())
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                dismissLoadingDialog()
                                binding.edtFullName.setText(response.result.name)
                                binding.edtMobileNumber.setText(response.result.mobile_number)
                                binding.edtEmailAddress.setText(response.result.email_address)
                            } else {
                                dismissLoadingDialog()
                                showDialogBox(
                                    SweetAlertDialog.WARNING_TYPE,
                                    "Waring",
                                    "User data not found "
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
                    call: Call<LoginResponse>,
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
    private fun isValidate(): Boolean =
        validateFullName() && validateMobileNumber() && validateEmailAddress()
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
                startActivity(Intent(this,MainActivity::class.java))
            }
        sweetAlertDialog.show()
    }
}
