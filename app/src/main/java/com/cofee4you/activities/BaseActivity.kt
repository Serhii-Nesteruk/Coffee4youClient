package com.cofee4you.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cofee4you.R
import com.cofee4you.models.BuyNewTariffRequest
import com.cofee4you.models.TariffType
import com.cofee4you.patterns.CofeeAppPatterns
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var patterns: CofeeAppPatterns
    protected lateinit var bottomNavigationView: BottomNavigationView
    protected lateinit var contentFrame: FrameLayout

    abstract fun getLayoutResourceId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        initializeDependencies()
    }

    open fun initializeDependencies() {
        patterns = CofeeAppPatterns()
        if (checkIfActivityHasBottomNav()) {
            bottomNavigationView = findViewById(R.id.bottom_navigation)
            if (::bottomNavigationView.isInitialized) {
                setupBottomNavigationView()
            } else {
                throw IllegalStateException("BottomNavigationView with ID R.id.bottom_navigation not found in layout.")
            }
        }
    }

    private fun checkIfActivityHasBottomNav(): Boolean {
        return this is HomeActivity || this is ProfileActivity || this is TariffsActivity || this is InviteActivity;
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item)
        }
    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> handleHomeNavigation()
            R.id.profile -> handleProfileNavigation()
            R.id.Invite -> handleInviteNavigation()
            R.id.Tariffs -> handleTariffsNavigation()
            else -> false
        }
    }

    private fun handleHomeNavigation(): Boolean {
        if (this !is HomeActivity) {
            moveToHomeActivity(this)
        }
        return true
    }

    private fun handleProfileNavigation(): Boolean {
        if (this !is ProfileActivity) {
            moveToProfileActivity(this)
        }
        return true
    }

    private fun handleInviteNavigation(): Boolean {
        if (this !is InviteActivity) {
            moveToInviteActivity(this)
        }
        return true
    }

    private fun handleTariffsNavigation(): Boolean {
        if (this !is TariffsActivity) {
            moveToTariffsActivity(this)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        updateSelectedMenuItem()
    }

    private fun updateSelectedMenuItem() {
        when (this) {
            is HomeActivity -> bottomNavigationView.selectedItemId = R.id.home
            is ProfileActivity -> bottomNavigationView.selectedItemId = R.id.profile
            is InviteActivity -> bottomNavigationView.selectedItemId = R.id.Invite
            is TariffsActivity -> bottomNavigationView.selectedItemId = R.id.Tariffs
        }
    }

    protected fun moveToBuyNewTariffActivity(currentActivity: BaseActivity, tariffRequest: BuyNewTariffRequest) {
        patterns.moveToActivity(
            currentActivity,
            Intent(currentActivity, PaymentActivity::class.java),
            mapOf("request" to tariffRequest)
        )
    }

    protected fun moveToHomeActivity(currentActivity: BaseActivity) {
        patterns.moveToActivity(
            currentActivity,
            Intent(currentActivity, HomeActivity::class.java),
            mapOf("phoneNumber" to "+48504076855") // TODO: chane phone number
        )
    }

    protected fun moveToInviteActivity(currentActivity: BaseActivity) {
        patterns.moveToActivity(
            this,
            Intent(currentActivity, InviteActivity::class.java),
            mapOf("phoneNumber" to "+48504076855"))
    }

    protected fun moveToTariffsActivity(currentActivity: BaseActivity) {
        patterns.moveToActivity(
            this,
            Intent(currentActivity, TariffsActivity::class.java),
            mapOf("phoneNumber" to "+48504076855"))
    }

    protected fun moveToProfileActivity(currentActivity: BaseActivity) {
        patterns.moveToActivity(
            this,
            Intent(currentActivity, ProfileActivity::class.java),
            mapOf("phoneNumber" to "+48504076855"))
    }

}