package com.cofee4you.patterns

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.cofee4you.activities.BaseActivity
import com.cofee4you.models.BuyNewTariffRequest

class CofeeAppPatterns {
    public fun moveToActivity(currentActivity: BaseActivity, intent: Intent, extras: Map<String, Any?>) {
        for ((key, value) in extras) {
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is BuyNewTariffRequest -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Unsupported extra type for key: $key")
            }
        }
        currentActivity.startActivity(intent)
    }
}