package com.example.ecohero.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.ecohero.databinding.ActivityLoginBinding
import com.example.ecohero.viewmodels.LoginPreferences
import com.example.ecohero.viewmodels.LoginRegisterViewModel
import com.example.ecohero.viewmodels.LoginViewModel
import com.example.ecohero.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private val loginRegisterViewModel by viewModels<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.login.setOnClickListener {
            val email = binding.emailTextInput.text.toString()
            val pass = binding.passwordTextInput.text.toString()

            when {
                email.isEmpty() -> binding.emailTextInput.error = "Email is Required"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.emailTextInput.error = "Invalid Email Format"
                pass.isEmpty() -> binding.passwordTextInput.error = "Password is Required"
                pass.length < 8 -> binding.passwordTextInput.error = "Password must contain at least 8 characters"
                else -> {
                    loginRegisterViewModel.loginAccount(email, pass, auth)
                }
            }
        }

        loginRegisterViewModel.result.observe(this) {
            when(it) {
                "Success" -> {
                    val pref = LoginPreferences.getInstance(dataStore)
                    val loginViewModel = ViewModelProvider(this@LoginActivity, ViewModelFactory(pref))[LoginViewModel::class.java]
                    loginViewModel.setLoggedIn(true)
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                "Error" -> {
                    Toast.makeText(this, "Email or Password is Incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginRegisterViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}