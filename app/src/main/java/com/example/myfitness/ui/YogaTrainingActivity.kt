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

class YogaTrainingActivity : AppCompatActivity() {

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

        // Set content for Yoga training session
        imageTraining.setImageResource(R.drawable.yoga_image) // Replace with your yoga image
        textTrainingTitle.text = "Yoga" // Title
        textTrainingDescription.text = getString(R.string.yoga_description) // Description

        // Handle button click
        startTrainingButton.setOnClickListener {
            // Store yoga training session data in Firestore
            storeYogaSessionData()
        }
    }

    private fun storeYogaSessionData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // Example: Data to store for yoga session
            val sessionData = hashMapOf(
                "userId" to user.uid,
                "type" to "Yoga",
                "timestamp" to System.currentTimeMillis()
                // Add more fields as needed
            )

            // Add a new document with a generated ID
            db.collection("trainingSessions")
                .add(sessionData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        this@YogaTrainingActivity,
                        "Yoga session stored successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to next activity upon successful addition
                    navigateToYogaTraining()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@YogaTrainingActivity,
                        "Error storing yoga session: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Toast.makeText(
                this@YogaTrainingActivity,
                "User not authenticated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToYogaTraining() {
        // Example: Navigate to the next activity for Yoga training
        val intent = Intent(this, UserDetailsActivity::class.java)
        startActivity(intent)
    }
}
