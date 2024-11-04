package com.example.userprivacydemo
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Disable screenshots
        /*
        By setting the FLAG_SECURE flag, the app prevents screenshots and screen recordings of sensitive screens, which is crucial in safeguarding user information against accidental leaks.
         */
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        preferences = AppPreferences(this)

        // Check if data is already saved
        if (preferences.getData("fullName") != null) {
            startActivity(Intent(this, DisplayActivity::class.java))
            finish()
        }

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAge = findViewById<EditText>(R.id.etAge)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Request permissions with explanations
        explainPermissionsIfNeeded()

        btnSave.setOnClickListener {
            val fullName = etFullName.text.toString()
            val mobile = etMobile.text.toString()
            val email = etEmail.text.toString()
            val age = etAge.text.toString()

            preferences.saveData("fullName", fullName)
            preferences.saveData("mobile", mobile)
            preferences.saveData("email", email)
            preferences.saveData("age", age)

            // Navigate to DisplayActivity
            startActivity(Intent(this, DisplayActivity::class.java))
            finish()
        }
    }
    private fun explainPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                // Show custom dialog explaining why you need this permission
                showPermissionExplanationDialog("This app needs access to your images to function.")
            } else {
                checkPermission()
            }
        }
    }

    private fun showPermissionExplanationDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Allow") { _, _ ->
                checkPermission()
            }
            .setNegativeButton("Deny", null)
            .show()
    }
    /*
    Minimal logging reduces the chance of exposing sensitive information, but even better is to avoid logging permission statuses in production releases.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (BuildConfig.DEBUG) {
                permissions.entries.forEach { entry ->
                    Log.d("PrivacyDemo", "${entry.key} granted: ${entry.value}")
                }
            }
        }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionsNeeded = mutableListOf<String>()

            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO)
            }

            if (permissionsNeeded.isNotEmpty()) {
                requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
            }
        } else {
            // For Android versions lower than 13, use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }
}