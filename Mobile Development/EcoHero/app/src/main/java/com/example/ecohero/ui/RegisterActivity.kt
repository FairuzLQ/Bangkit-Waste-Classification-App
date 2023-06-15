package com.example.ecohero.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ecohero.data.Account
import com.example.ecohero.databinding.ActivityRegisterBinding
import com.example.ecohero.viewmodels.LoginRegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private val loginRegisterViewModel by viewModels<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.back.setOnClickListener {
            finish()
        }

        binding.signup.setOnClickListener {
            val name = binding.nameTextInput.text.toString()
            val phone = binding.phoneTextInput.text.toString()
            val email = binding.emailTextInput.text.toString()
            val pass = binding.passwordTextInput.text.toString()

            when {
                name.isEmpty() -> binding.nameTextInput.error = "Name is Required"
                phone.isEmpty() -> binding.phoneTextInput.error = "Phone Number is Required"
                email.isEmpty() -> binding.emailTextInput.error = "Email is Required"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.emailTextInput.error = "Invalid Email Format"
                pass.isEmpty() -> binding.passwordTextInput.error = "Password is Required"
                pass.length < 8 -> binding.passwordTextInput.error = "Password must contain at least 8 characters"
                else -> {
                    loginRegisterViewModel.registerAccount(Account(name, phone, email, "", null), pass, auth, db)
                }
            }
        }

        loginRegisterViewModel.result.observe(this) {
            when(it) {
                "Success" -> {
                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                    finish()
                }
                "Error" -> {
                    Toast.makeText(this, "Failed to create Account", Toast.LENGTH_SHORT).show()
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