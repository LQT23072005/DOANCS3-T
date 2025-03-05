package com.example.doancs3.Activity

import android.content.Intent
import android.os.Bundle
import com.example.doancs3.databinding.ActivityIntroMainBinding

class IntroMainActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
