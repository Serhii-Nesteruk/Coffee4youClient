package com.cofee4you.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.cofee4you.R
import com.cofee4you.models.FacebookDTO
import com.cofee4you.models.FacebookLoginRequest
import com.cofee4you.models.LoginRequest
import com.cofee4you.models.User
import com.cofee4you.objects.ApiClient
import com.cofee4you.objects.CurrentUser
import com.cofee4you.patterns.CofeeAppPatterns
import com.cofee4you.services.ApiService
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    private lateinit var registerTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var patterns: CofeeAppPatterns
    private lateinit var apiService: ApiService
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var callbackManager: CallbackManager

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_login
    }

    override fun initializeDependencies() {
        super.initializeDependencies()

        patterns = CofeeAppPatterns()
        apiService = ApiClient.apiService

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        registerTextView = findViewById(R.id.registerTextView)
        registerTextView.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                registerViewOnClickListener()
            }
        }

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                loginButtonOnClickListener()
            }
        }

        setupFacebookLogin()
    }

    private fun setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create()

        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)
        facebookLoginButton.setPermissions("email", "public_profile")

        facebookLoginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val accessToken = loginResult.accessToken.token
                fetchFacebookUserProfile(accessToken)
            }

            override fun onCancel() {
                showToast("Facebook Login Cancelled")
            }

            override fun onError(exception: FacebookException) {
                showToast("Error: ${exception.message}")
            }
        })
    }

    private fun fetchFacebookUserProfile(accessToken: String) {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { jsonObject, _ ->
            handleFacebookResponse(jsonObject, accessToken)
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookResponse(jsonObject: JSONObject?, accessToken: String) {
        val email = jsonObject?.optString("email")
        val name = jsonObject?.optString("name")
        val facebookId = jsonObject?.optString("id")

        sendFacebookDataToServer(FacebookDTO(email, name, facebookId), accessToken)
    }

    private fun sendFacebookDataToServer(facebookDTO: FacebookDTO, accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.facebookLogin(FacebookLoginRequest(facebookDTO, accessToken))
            if (response.isSuccessful) {
                val body = response.body()
                processSuccessfulLogin(
                    body?.userId ?: "invalidId",
                    body?.phoneNumber ?: "invalidPhoneNumber",
                    body?.referralCode ?: "invalidReferralCode"
                )
            } else {
                showToast("Facebook Login Failed")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private suspend fun loginButtonOnClickListener() {
        val password = passwordEditText.text.toString()

        val response = apiService.login(
            LoginRequest(phoneNumberEditText.text.toString(), password)
        )

        if (response.isSuccessful) {
            processSuccessfulLogin(
                response.body()?.userId ?: "invalidId",
                response.body()?.phoneNumber?: "invalidPhoneNumber",
                response.body()?.referralCode?:"invalidReferralCode"
            )
        }
    }

    private fun processSuccessfulLogin(userId: String, phoneNumber: String, referralCode: String) {
        CurrentUser.setUser(User(userId, phoneNumber, referralCode))
        moveToHomeActivity(this@LoginActivity)
    }

    private fun registerViewOnClickListener() {
        patterns.moveToActivity(
            this,
            Intent(this@LoginActivity, RegisterActivity::class.java),
            mapOf()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}