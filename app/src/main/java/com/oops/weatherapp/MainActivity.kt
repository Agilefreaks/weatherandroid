package com.oops.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oops.weatherapp.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }
}