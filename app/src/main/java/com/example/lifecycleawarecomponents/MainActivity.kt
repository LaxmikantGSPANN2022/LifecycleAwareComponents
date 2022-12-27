package com.example.lifecycleawarecomponents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        networkMonitor = NetworkMonitor(this)
        lifecycle.addObserver(networkMonitor)

        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
//            Toast.makeText(this@MainActivity, "On Button Click!!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SecondActivity::class.java))
        })
    }
}

