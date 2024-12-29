package com.cofee4you.services

import com.cofee4you.models.BuyNewTariffRequest
import com.cofee4you.models.BuyNewTariffResponse
import com.cofee4you.models.CreateInviteLinkRequest
import com.cofee4you.models.FacebookLoginRequest
import com.cofee4you.models.LoginRequest
import com.cofee4you.models.LoginResponse
import com.cofee4you.models.PaymentRequest
import com.cofee4you.models.PaymentResponse
import com.cofee4you.models.ReferralResponse
import com.cofee4you.models.RegisterRequest
import com.cofee4you.models.RegisterResponse
import com.cofee4you.models.TokenRequest
import com.cofee4you.models.VerifyRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/verify")
    suspend fun verify(@Body request: VerifyRequest): Boolean

    @POST("/referral/generate_referral_service")
    suspend fun generateUniqueReferralLink(@Body request: CreateInviteLinkRequest): Response<ReferralResponse>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("tariffs/buy_new_tariff")
    suspend fun buyNewTariff(@Body tariffRequest: BuyNewTariffRequest) : Response<BuyNewTariffResponse>

    @POST("auth/facebook-login")
    suspend fun facebookLogin(@Body request: FacebookLoginRequest): Response<LoginResponse>

    @POST("push-msg/save-token")
    suspend fun sendTokenToServer(@Body request: TokenRequest): Response<ResponseBody>

    @POST("api/payment/create")
    fun createPaymentIntent(@Body paymentRequest: PaymentRequest): Call<PaymentResponse>

}