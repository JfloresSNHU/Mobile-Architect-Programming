package com.example.weighttrackit

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var adapter: WeightAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var weightList: MutableList<WeightEntry>
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()

        // Initialize Firebase Auth and reference to this user's weight log
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance()
            .getReference("weights/${auth.currentUser?.uid}")

        // Initialize weight list and RecyclerView
        weightList = mutableListOf()
        recyclerView = findViewById(R.id.recyclerViewWeights)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up adapter with delete handler
        adapter = WeightAdapter(weightList) { entryToDelete ->
            deleteWeight(entryToDelete)
        }
        recyclerView.adapter = adapter

        // Button: Navigate to weight entry screen
        findViewById<Button>(R.id.btnAddWeight).setOnClickListener {
            startActivity(Intent(this, WeightEntryActivity::class.java))
        }

        // Button: Navigate to goal setting screen
        findViewById<Button>(R.id.btnSetGoal).setOnClickListener {
            startActivity(Intent(this, GoalSettingActivity::class.java))
        }


        findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        // Fetch and display user's weight log from Firebase
        fetchWeights()
    }

    // Listen for data changes and update the list accordingly
    private fun fetchWeights() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                weightList.clear()
                for (entrySnap in snapshot.children) {
                    val date = entrySnap.child("date").getValue(String::class.java)
                    val weight = entrySnap.child("weight").getValue(String::class.java)
                    if (date != null && weight != null) {
                        weightList.add(WeightEntry(date, weight))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardActivity", "Failed to load weights", error.toException())
            }
        })
    }

    // Delete a specific weight entry by matching its date
    private fun deleteWeight(entry: WeightEntry) {
        dbRef.orderByChild("date").equalTo(entry.date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DashboardActivity", "Failed to delete entry", error.toException())
                }
            })
    }
}
