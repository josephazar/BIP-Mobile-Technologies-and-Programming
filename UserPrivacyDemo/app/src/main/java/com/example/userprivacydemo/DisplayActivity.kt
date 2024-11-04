package com.example.userprivacydemo
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class DisplayActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display)
        preferences = AppPreferences(this)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvMobile = findViewById<TextView>(R.id.tvMobile)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvAge = findViewById<TextView>(R.id.tvAge)
        val btnClear = findViewById<Button>(R.id.btnClear)

        // Display saved data
        tvFullName.text = "Full Name: ${preferences.getData("fullName")}"
        tvMobile.text = "Mobile Number: ${preferences.getData("mobile")}"
        tvEmail.text = "Email: ${preferences.getData("email")}"
        tvAge.text = "Age: ${preferences.getData("age")}"
        btnClear.setOnClickListener {
            preferences.clearData()
            // Go back to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}