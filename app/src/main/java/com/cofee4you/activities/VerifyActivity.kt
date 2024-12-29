package com.cofee4you.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cofee4you.R
import com.cofee4you.models.VerifyRequest
import com.cofee4you.objects.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VerifyActivity : BaseActivity() {
    private lateinit var codeEditText: EditText
    private lateinit var verifyButton: Button
    private lateinit var phoneNumber: String

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_verify
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        codeEditText = findViewById(R.id.codeEditText)
        verifyButton = findViewById(R.id.verifyButton)

        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""

        verifyButton.setOnClickListener {
            val code = codeEditText.text.toString()
            Log.d("VerifyActivity", "Entered code: $code")
            phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            Log.d("VerifyActivity", "Phone number: $phoneNumber")
            verifyUser(phoneNumber, code)
        }
    }

    private fun verifyUser(phoneNumber: String, code: String) {
        if (code.isEmpty()) {
            Toast.makeText(this, "Будь ласка, введіть код верифікації", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.verify(VerifyRequest(phoneNumber, code))
                withContext(Dispatchers.Main) {
                    if (response) {
                        Toast.makeText(this@VerifyActivity, "Успішна реєстрація", Toast.LENGTH_SHORT).show()
                        moveToHomeActivity(this@VerifyActivity)
                    } else {
                        Toast.makeText(this@VerifyActivity, "Невірний код", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VerifyActivity, "Помилка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}