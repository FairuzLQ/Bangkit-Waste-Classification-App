package com.example.ecohero.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ecohero.R
import com.example.ecohero.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navView: BottomNavigationView = binding.botNavView
        val homeFragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, homeFragment)
        fragmentTransaction.commit()

        navView.setOnItemSelectedListener() {
            when(it.itemId) {
                R.id.navigation_home -> {
                    val fragmentTransaction1 = supportFragmentManager.beginTransaction()
                    fragmentTransaction1.replace(R.id.nav_host_fragment, homeFragment)
                    fragmentTransaction1.commit()
                    true
                }

                R.id.navigation_profile -> {
                    val profileFragment = ProfileFragment()
                    val fragmentTransaction2 = supportFragmentManager.beginTransaction()
                    fragmentTransaction2.replace(R.id.nav_host_fragment, profileFragment)
                    fragmentTransaction2.commit()
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.scan.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }
}