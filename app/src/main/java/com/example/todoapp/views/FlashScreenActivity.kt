package com.example.todoapp.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityFlashBinding

class FlashScreenActivity:AppCompatActivity() {
    private lateinit var activityFlashScreenActivity: ActivityFlashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFlashScreenActivity=DataBindingUtil.setContentView(this,R.layout.activity_flash)
    }

    override fun onStart() {
        super.onStart()
        handler()
    }

    private fun handler() {
        Handler().postDelayed({
            val intent = Intent(this@FlashScreenActivity, TodoAppActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}