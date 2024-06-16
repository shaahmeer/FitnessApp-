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

class BulkTrainingActivity : AppCompatActivity() {

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

        // Set content for Bulk Training session
        imageTraining.setImageResource(R.drawable.bulk) // Replace with your bulk training image
        textTrainingTitle.text = "Bulk Training" // Title
        textTrainingDescription.text = getString(R.string.bulk_training_description) // Description

        // Handle button click
        startTrainingButton.setOnClickListener {
            // Store bulk training session data in Firestore
            storeBulkTrainingSessionData()
        }
    }

    private fun storeBulkTrainingSessionData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // Example: Data to store for bulk training session
            val sessionData = hashMapOf(
                "userId" to user.uid,
                "type" to "Bulk Training",
                "timestamp" to System.currentTimeMillis()
                // Add more fields as needed
            )

            // Add a new document with a generated ID
            db.collection("trainingSessions")
                .add(sessionData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        this@BulkTrainingActivity,
                        "Bulk Training session stored successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to next activity upon successful addition
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@BulkTrainingActivity,
                        "Error storing Bulk Training session: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Toast.makeText(
                this@BulkTrainingActivity,
                "User not authenticated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
