package com.cofee4you.activities

import com.cofee4you.R

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cofee4you.models.PaymentRequest
// import com.example.paymentapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.cofee4you.objects.ApiClient

class PaymentActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var payButton: Button
    private lateinit var paymentWebView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        amountEditText = findViewById(R.id.amountEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        payButton = findViewById(R.id.payButton)
        paymentWebView = findViewById(R.id.paymentWebView)

        payButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val description = descriptionEditText.text.toString()

            if (amount != null && description.isNotEmpty()) {
                createPaymentIntent(amount, description)
            } else {
                Toast.makeText(this, "Заповніть всі поля", Toast.LENGTH_SHORT).show()
            }
        }

        setupWebView()
    }

    private fun setupWebView() {
        paymentWebView.settings.javaScriptEnabled = true
        paymentWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.contains("success")) {
                    Toast.makeText(this@PaymentActivity, "Платіж успішний!",
                        Toast.LENGTH_SHORT).show()
                    paymentWebView.visibility = View.GONE
                } else if (url.contains("failure")) {
                    Toast.makeText(this@PaymentActivity, "Платіж не вдалося!",
                        Toast.LENGTH_SHORT).show()
                    paymentWebView.visibility = View.GONE
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Можна додати додаткові дії після завантаження сторінки
            }
        }
    }

    private fun createPaymentIntent(amount: Double, description: String) {
        val paymentRequest = PaymentRequest(
            amount = amount,
            currency = "UAH",
            description = description
        )

        ApiClient.apiService.createPaymentIntent(paymentRequest)
            .enqueue(object : Callback<com.cofee4you.models.PaymentResponse> {
                override fun onResponse(
                    call: Call<com.cofee4you.models.PaymentResponse>,
                    response: Response<com.cofee4you.models.PaymentResponse>
                ) {
                    if (response.isSuccessful) {
                        val paymentData = response.body()?.payment_data
                        val checkoutUrl = response.body()?.checkout_url
                        if (paymentData != null && checkoutUrl != null) {
                            loadPaymentPage(checkoutUrl, paymentData)
                        } else {
                            Toast.makeText(this@PaymentActivity, "Помилка отримання даних платежу", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@PaymentActivity, "Помилка створення платежу", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.cofee4you.models.PaymentResponse>, t: Throwable) {
                    Toast.makeText(this@PaymentActivity, "Помилка мережі: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadPaymentPage(url: String, paymentData: String) {
        paymentWebView.visibility = View.VISIBLE
        val postData = paymentData.toByteArray(Charsets.UTF_8)
        paymentWebView.postUrl(url, postData)
    }
}
/*
import android.os.Bundle
import android.widget.Button
import com.cofee4you.R
import com.cofee4you.models.BuyNewTariffRequest
import com.cofee4you.models.TariffType
import com.cofee4you.objects.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
/*
class BuyNewTariffActivity : BaseActivity() {
    private val apiService = ApiClient.apiService
    private lateinit var tariffRequest: BuyNewTariffRequest
    private lateinit var buyButton: Button

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_buy_tariff
    }

    override fun initializeDependencies() {
        super.initializeDependencies()
        tariffRequest = intent.getParcelableExtra("request") ?: BuyNewTariffRequest("", TariffType.BASE)
        buyButton = findViewById(R.id.buy_tariff_btn)
        buyButton.setOnClickListener { isBuyButtonClickedListener() }
    }

    private fun isBuyButtonClickedListener() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.buyNewTariff(tariffRequest)
                if (response.isSuccessful && response.body()?.isSuccessful == true) {
                    showToast("Tariff purchased successfully")
                } else {
                    showToast(
                        ("Error purchasing tariff. Error message: " + response.body()?.message)
                            ?: "No error message"
                    )
                }
            } catch (e: Exception) {
                showToast("Network error: ${e.localizedMessage}")
            }
        }
    }

}*/