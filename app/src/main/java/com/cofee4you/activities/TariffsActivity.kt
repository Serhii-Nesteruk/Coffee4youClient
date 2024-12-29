package com.cofee4you.activities

import android.os.Bundle
import android.widget.RelativeLayout
import com.cofee4you.R
import com.cofee4you.models.BuyNewTariffRequest
import com.cofee4you.models.TariffType
import com.cofee4you.objects.CurrentUser
import com.cofee4you.patterns.CofeeAppPatterns
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.logging.Logger
import kotlin.math.log

class TariffsActivity : BaseActivity() {
    private lateinit var baseTariffWidget: RelativeLayout;
    private lateinit var proTariffWidget: RelativeLayout;
    private lateinit var premiumTariffWidget: RelativeLayout;
    private lateinit var exclusiveTariffWidget: RelativeLayout;

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_tariffs
    }

    private fun initializeWidgets() {
        baseTariffWidget = findViewById(R.id.tariff_base)
        proTariffWidget = findViewById(R.id.tariff_pro)
        premiumTariffWidget = findViewById(R.id.tariff_premium)
        exclusiveTariffWidget = findViewById(R.id.tariff_exclusive)
    }

    private fun setupWidgets() {
        baseTariffWidget.setOnClickListener { baseTariffClickListener() }
        proTariffWidget.setOnClickListener { proTariffClickListener() }
        premiumTariffWidget.setOnClickListener { premiumTariffClickListener() }
        exclusiveTariffWidget.setOnClickListener { exclusiveTariffClickListener() }
    }

    private fun exclusiveTariffClickListener() {
        createRequestAndMoveToActivity(TariffType.EXCLUSIVE)
    }

    private fun premiumTariffClickListener() {
        createRequestAndMoveToActivity(TariffType.PREMIUM)
    }

    private fun proTariffClickListener() {
        createRequestAndMoveToActivity(TariffType.PRO)
    }

    private fun baseTariffClickListener() {
        createRequestAndMoveToActivity(TariffType.BASE)
    }

    private fun createRequestAndMoveToActivity(tariffType: TariffType) {
        val Log = Logger.getLogger(this@TariffsActivity::class.java.name)
        Log.warning(CurrentUser.getUser()?.phoneNumber)
        val request = BuyNewTariffRequest(CurrentUser.getUser()?.phoneNumber?:"", tariffType)
        moveToBuyNewTariffActivity(this, request)
    }

    override fun initializeDependencies() {
        super.initializeDependencies()
        initializeWidgets()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWidgets()
    }


}