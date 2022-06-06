package com.example.lifesum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.base.viewbinding.viewBinding
import com.example.lifesum.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}