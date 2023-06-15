package com.example.ecohero.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecohero.databinding.ActivityDetailResponseBinding

class DetailResponseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailResponseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.result.text = intent.getStringExtra("extra_result")
        Glide.with(this).load(intent.getStringExtra("extra_picture")).into(binding.image)
        binding.textDescription.text = intent.getStringExtra("extra_description")
        binding.textHandling.text = intent.getStringExtra("extra_handling")
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_PICTURE = "extra_picture"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_HANDLING = "extra_handling"
    }
}