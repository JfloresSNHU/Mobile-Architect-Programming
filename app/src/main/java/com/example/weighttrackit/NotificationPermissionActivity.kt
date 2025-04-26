package com.example.weighttrackit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NotificationPermissionActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_permission)

        val tvStatus = findViewById<TextView>(R.id.tvPermissionStatus)
        val btnRequest = findViewById<Button>(R.id.btnRequestPermission)

        updateStatus(tvStatus)

        btnRequest.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    SMS_PERMISSION_CODE
                )
            } else {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            val tvStatus = findViewById<TextView>(R.id.tvPermissionStatus)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
                updateStatus(tvStatus)
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
                updateStatus(tvStatus)
            }
        }
    }

    private fun updateStatus(tvStatus: TextView) {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        tvStatus.text = if (granted == PackageManager.PERMISSION_GRANTED) {
            "SMS permission is granted. Notifications will be sent."
        } else {
            "SMS permission is not granted. Notifications are disabled."
        }
    }
}
