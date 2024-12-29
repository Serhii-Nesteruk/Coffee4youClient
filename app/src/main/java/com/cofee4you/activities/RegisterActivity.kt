package com.cofee4you.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.cofee4you.R
import com.cofee4you.models.RegisterRequest
import com.cofee4you.models.RegisterResponse
import com.cofee4you.models.User
import com.cofee4you.objects.ApiClient
import com.cofee4you.objects.CurrentUser
import com.cofee4you.patterns.CofeeAppPatterns
import com.cofee4you.services.RegisterService
import com.cofee4you.utils.SecurityUtils
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class RegisterActivity : BaseActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var registerService: RegisterService
    private lateinit var patterns: CofeeAppPatterns
    private lateinit var loginTextView: TextView
    private lateinit var referralCodeTextView: TextView

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_register
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initializeServices()
        setupRegisterButton()
        setupViews()
    }

    private fun setupViews() {
        loginTextView.setOnClickListener {
            moveToRegisterActivity()
        }
    }

    private fun moveToRegisterActivity() {
        patterns.moveToActivity(
            this,
            Intent(this@RegisterActivity, LoginActivity::class.java),
            mapOf()
        )
    }

    private fun initializeServices() {
        registerService = RegisterService()
        patterns = CofeeAppPatterns()
    }

    private fun initializeViews() {
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        nameEditText = findViewById(R.id.nameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginTextView = findViewById(R.id.loginTextView)
        referralCodeTextView = findViewById(R.id.incomingReferralCode)
    }

    private fun setupRegisterButton() {
        registerButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val incomingReferralCode = referralCodeTextView.text.toString().trim()

            registerButtonOnClickAction(phoneNumber, name, lastName, email, password, incomingReferralCode)
        }
    }

    private fun registerButtonOnClickAction( phoneNumber: String, name: String,
                                             lastName: String, email: String, password: String,
                                             incomingReferralCode: String) {
        val validateResult =
            registerService.validateInput(phoneNumber, name, lastName, email, password)

        if (!validateResult.equals(null))
            showToast(validateResult.toString());
        else if (phoneNumber.isNotEmpty() && password.isNotEmpty())
            registerUser(phoneNumber, name, lastName, email, password, incomingReferralCode)
    }

    private fun handleRegisterResponse(response: Response<RegisterResponse>, phoneNumber: String) {
        if (response.isSuccessful) {
            CurrentUser.setUser(User(response.body()?.userId?:"invalidId", phoneNumber, ""))
            showToast("Код підтвердження відправлено")
            navigateToVerifyActivity(phoneNumber)
        } else
            showToast("Помилка реєстрації: ${response.message()}")
    }

    private fun navigateToVerifyActivity(phoneNumber: String) {
        patterns.moveToActivity(
            this,
            Intent(this@RegisterActivity, VerifyActivity::class.java),
            mapOf("phoneNumber" to phoneNumber))
    }

    private suspend fun sendRegisterRequest(phoneNumber: String, name: String, lastName: String,
                                            email: String, password: String, incomingReferralCode: String) : Response<RegisterResponse> {
        val response = ApiClient.apiService.register(
            RegisterRequest(phoneNumber, name, lastName, email, password, incomingReferralCode)
        )
        return response
    }

    private fun registerUser(
        phoneNumber: String, name: String, lastName: String,
        email: String, password: String, incomingReferralCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
               val response = sendRegisterRequest(phoneNumber, name, lastName, email, password, incomingReferralCode)
               withContext(Dispatchers.Main) {
                   handleRegisterResponse(response, phoneNumber)
               }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Помилка: ${e.message}")
                }
            }
        }
    }
}
