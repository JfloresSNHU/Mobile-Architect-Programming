package com.example.weighttrackit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class WeightEntryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_entry)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        dbRef = FirebaseDatabase.getInstance().getReference("weights/$userId")

        val etWeight = findViewById<EditText>(R.id.etWeight)
        val btnSave = findViewById<Button>(R.id.btnSaveWeight)

        findViewById<Button>(R.id.btnCancelWeightEntry).setOnClickListener {
            finish() // Go back to dashboard
        }


        btnSave.setOnClickListener {
            val weightText = etWeight.text.toString().trim()

            if (weightText.isNotBlank()) {
                val currentWeight = weightText.toFloatOrNull()
                if (currentWeight == null) {
                    Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // Save new weight entry to Firebase
                val entry = mapOf(
                    "date" to date,
                    "weight" to "$currentWeight lbs"
                )

                dbRef.push().setValue(entry)
                    .addOnSuccessListener {
                        checkGoalAndMaybeSendSMS(currentWeight)
                        Toast.makeText(this, "Weight saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save weight", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Retrieves the user's goal from Firebase and sends an SMS if the current weight
     * meets or beats the goal.
     */
    private fun checkGoalAndMaybeSendSMS(currentWeight: Float) {
        val userId = auth.currentUser?.uid ?: return
        val goalRef = FirebaseDatabase.getInstance().getReference("goals/$userId/goalWeight")

        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goalWeightStr = snapshot.getValue(String::class.java)?.replace(" lbs", "")
                val goalWeight = goalWeightStr?.toFloatOrNull()

                if (goalWeight != null && currentWeight <= goalWeight) {
                    sendGoalReachedSMS()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: Log or notify of failure to read goal
            }
        })
    }

    /**
     * Sends an SMS alert if permission is granted.
     */
    private fun sendGoalReachedSMS() {
        // Customize this number for testing or use dynamic user profile data
        val phoneNumber = "1234567890"
        val message = "🎉 Congrats! You've reached your goal weight on Weight Trackit!"

        // Check SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "Goal reached! SMS sent.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send SMS: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "SMS permission not granted. No notification sent.", Toast.LENGTH_SHORT).show()
        }
    }
}
