package com.cofee4you.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cofee4you.R
import com.cofee4you.models.TokenRequest
import com.cofee4you.objects.ApiClient
import com.cofee4you.objects.CurrentUser
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoffeeFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()
        apiService = ApiClient.apiService
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Новий токен: $token")

        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {

        val userId = CurrentUser.getUser()?.userId?:"invalidId"
        val tokenRequest = TokenRequest(userId = userId, token = token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendTokenToServer(tokenRequest)
                if (response.isSuccessful) {
                    println("Токен успішно надіслано на сервер")
                } else {
                    println("Не вдалося надіслати токен: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Помилка під час надсилання токена: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Default Title"
        val body = remoteMessage.notification?.body ?: "Default Body"

        println("Отримано повідомлення: $title - $body")

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
