package com.example.myfitness.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MuscleGainActivity : AppCompatActivity() {

    private lateinit var imageTraining: ImageView
    private lateinit var textTrainingTitle: TextView
    private lateinit var textTrainingDescription: TextView
    private lateinit var startTrainingButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_session)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        imageTraining = findViewById(R.id.imageTraining)
        textTrainingTitle = findViewById(R.id.textTrainingTitle)
        textTrainingDescription = findViewById(R.id.textTrainingDescription)
        startTrainingButton = findViewById(R.id.startTrainingButton)

        // Set content for Muscle Gain training session
        imageTraining.setImageResource(R.drawable.muscle) // Replace with your muscle gain image
        textTrainingTitle.text = getString(R.string.muscle_gain_training_title) // Title
        textTrainingDescription.text = getString(R.string.muscle_gain_training_description) // Description

        // Handle button click
        startTrainingButton.setOnClickListener {
            // Store muscle gain training session data in Firestore
            storeMuscleGainSessionData()
        }
    }

    private fun storeMuscleGainSessionData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // Example: Data to store for muscle gain session
            val sessionData = hashMapOf(
                "userId" to user.uid,
                "type" to "Muscle Gain",
                "timestamp" to System.currentTimeMillis()
                // Add more fields as needed
            )

            // Add a new document with a generated ID
            db.collection("trainingSessions")
                .add(sessionData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        this@MuscleGainActivity,
                        "Muscle Gain training session stored successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Retrieve user details and navigate to UserDetailsActivity
                    retrieveUserDetailsAndNavigate()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@MuscleGainActivity,
                        "Error storing muscle gain training session: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Toast.makeText(
                this@MuscleGainActivity,
                "User not authenticated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun retrieveUserDetailsAndNavigate() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Safely retrieve fields and handle possible type mismatches
                        val name = document.getString("name") ?: ""
                        val age = document.getString("age") ?: ""
                        val gender = document.getString("gender") ?: ""
                        val realWeight = document.get("realWeight")?.toString() ?: ""
                        val height = document.getString("height") ?: ""
                        val fitnessLevel = document.getString("fitnessLevel") ?: ""
                        val medicalConditions = document.getString("medicalConditions") ?: ""
                        val injuries = document.getString("injuries") ?: ""

                        val userDetails = UserDetails(
                            name = name,
//
                            gender = gender,
                            realWeight = realWeight,
                            height = height
                        )
//                            fitnessLevel = fitnessLevel,
//                            medicalConditions = medicalConditions
//                            injuries = injuries


                        // Navigate to UserDetailsActivity with user details
                        val intent = Intent(this, UserDetailsActivity::class.java)
                        intent.putExtra("userDetails", userDetails)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error retrieving user details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

}

