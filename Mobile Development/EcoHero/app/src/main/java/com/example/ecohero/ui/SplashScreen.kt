package com.example.ecohero.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ecohero.R
import com.example.ecohero.viewmodels.LoginPreferences
import com.example.ecohero.viewmodels.LoginViewModel
import com.example.ecohero.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "id")

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launch {
            delay(delay)
            withContext(Dispatchers.Main) {
                val pref = LoginPreferences.getInstance(dataStore)
                val loginViewModel = ViewModelProvider(this@SplashScreen, ViewModelFactory(pref))[LoginViewModel::class.java]

                loginViewModel.isLoggedIn().observe(this@SplashScreen) {
                    if (it) {
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashScreen, OnboardingActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val delay: Long = 1000
    }
}