package com.cofee4you.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import com.cofee4you.R
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.cofee4you.models.CreateInviteLinkRequest
import com.cofee4you.objects.ApiClient
import com.cofee4you.objects.CurrentUser
import com.cofee4you.services.ApiService
import com.cofee4you.services.ReferralService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.concurrent.TaskRunner.Companion.logger
import java.util.logging.Level
import kotlin.math.log

class InviteActivity : BaseActivity() {

    private lateinit var inviteLink: TextView
    private lateinit var button: Button;
    private lateinit var apiService: ApiService
    private lateinit var referralService: ReferralService
    private lateinit var copyIcon: ImageView

    override fun initializeDependencies() {
        super.initializeDependencies()

        button = findViewById(R.id.create_new_button)
        inviteLink = findViewById(R.id.invite_link)
        copyIcon = findViewById(R.id.copy_icon)
        apiService = ApiClient.apiService
        referralService = ReferralService()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Link", text)
        clipboard.setPrimaryClip(clip)

        showToast("Link copied to clipboard!")
    }

    private fun setupActivityWidgets() {
        setupCreateNewLinkButton()
        setupInviteLink()
        setupCopyIcon()
    }

    private fun setupCopyIcon() {
        copyIcon.setOnClickListener {
            copyToClipboard(inviteLink.text.toString())
        }
    }

    private fun setupInviteLink() {
        inviteLink.text = CurrentUser.getUser()?.referralCode
    }

    private fun setupCreateNewLinkButton() {
        button.setOnClickListener{
            generateInviteLink();
        }
    }

    private suspend fun sendUniqueReferralLinkRequest(): String {
        val response = apiService.generateUniqueReferralLink(
            CreateInviteLinkRequest(CurrentUser.getUser()?.userId ?: "invalidId")
        )
        val userId = CurrentUser.getUser()?.userId?.toString() ?: "invalidId"
        Log.w("UserId", userId)

        return if (response.isSuccessful) {
            response.body()?.referralLink ?: throw Exception("Empty referral link")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    private fun handleLinkGenerationResponse(response: String) {
        showToast("Invite link successfully created")
        val code = referralService.getReferralCodeFromUrl(response)
        CurrentUser.getUser()?.referralCode = code.toString()
        inviteLink.text = CurrentUser.getUser()?.referralCode
    }

    private fun generateInviteLink() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = sendUniqueReferralLinkRequest()
                withContext(Dispatchers.Main) {
                    handleLinkGenerationResponse(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Error: Failed to generate referral link")
                }
            }
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_invite
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityWidgets()
    }
}