package com.example.lifecycleawarecomponents

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    lateinit var textviewResult: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        val currentNet = connectivityManager.activeNetwork
        textviewResult = findViewById<TextView>(R.id.result_text)

        textviewResult.setText(currentNet.toString())

        findViewById<Button>(R.id.button_get_result).setOnClickListener(View.OnClickListener {
            val cap = connectivityManager.getNetworkCapabilities(currentNet)
            textviewResult.setText(cap.toString())
        })
    }
}