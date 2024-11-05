package com.authcraftandroid.utils

import com.google.gson.JsonObject
import com.authcraftandroid.models.RegistrationRequest
import com.authcraftandroid.models.RegistrationResponse
import com.authcraftandroid.models.ForgotPasswordRequest
import com.authcraftandroid.models.LoginRequest
import com.authcraftandroid.models.LoginResponse
import com.authcraftandroid.models.LoginWithMobileRequest
import com.authcraftandroid.models.PasswordChangeRequest
import com.authcraftandroid.models.ResetPasswordRequest
import com.authcraftandroid.models.UpdateRequest
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @PUT("v1/change_password/{id}")
    fun changePassword(@Path("id") id: String, @Body request: PasswordChangeRequest): Call<LoginResponse>

    @PUT("v1/user_update/{id}")
    fun userUpdate(@Path("id") id: String, @Body request: UpdateRequest): Call<LoginResponse>

    @GET("v1/user_details/{id}")
    fun userDetails(@Path("id") id: String): Call<LoginResponse>

    @POST("v1/reset_password")
    fun userResetPassword(@Body request: ResetPasswordRequest): Call<LoginResponse>

    @POST("v1/check_verification_code")
    fun checkVerificationCode(@Body request: ForgotPasswordRequest): Call<LoginResponse>

    @POST("v1/send_password_reset_code")
    fun sendPasswordResetCode(@Body request: ForgotPasswordRequest): Call<LoginResponse>

    @POST("v1/check_otp")
    fun checkOtp(@Body request: LoginWithMobileRequest): Call<LoginResponse>

    @POST("v1/login_with_mobile")
    fun loginWithMobile(@Body request: LoginWithMobileRequest): Call<LoginResponse>

    @POST("v1/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("v1/register")
    fun userRegistration(@Body request: RegistrationRequest): Call<RegistrationResponse>

    companion object {
        // NodeJS This function creates an instance of ApiService
        fun CreateApi(): ApiService {
            // Set up Retrofit and return an instance of ApiService
            val retrofit = Retrofit.Builder()
                //Change this ip address. you can set your local ip address
                .baseUrl("http://10.0.2.2:3000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

}


