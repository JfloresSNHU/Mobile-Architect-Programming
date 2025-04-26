package com.example.weighttrackit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GoalSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)

        // UI references
        val etGoalWeight = findViewById<EditText>(R.id.etGoalWeight)
        val btnSaveGoal = findViewById<Button>(R.id.btnSaveGoal)

        findViewById<Button>(R.id.btnCancelGoal).setOnClickListener {
            finish() // Go back to dashboard
        }


        // Save goal weight to Firebase on click
        btnSaveGoal.setOnClickListener {
            val goalWeightText = etGoalWeight.text.toString().trim()

            if (goalWeightText.isNotBlank()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid ?: return@setOnClickListener

                // Save goal to Firebase at /goals/<uid>/goalWeight
                FirebaseDatabase.getInstance()
                    .getReference("goals/$userId")
                    .setValue(mapOf("goalWeight" to "$goalWeightText lbs"))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Goal weight saved", Toast.LENGTH_SHORT).show()
                        finish() // Return to dashboard
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save goal", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter a goal weight", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
