package com.cofee4you.services

import android.net.Uri

class ReferralService {
    public fun getReferralCodeFromUrl(url: String) : String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("referralCode")
    }
}