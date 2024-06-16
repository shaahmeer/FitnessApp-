package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SelectTrainingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_training)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val buttonYoga = findViewById<Button>(R.id.buttonYoga)
        val buttonFatLoss = findViewById<Button>(R.id.buttonFatLoss)
        val buttonGainMuscle = findViewById<Button>(R.id.buttonGainMuscle)
        val buttonBulk = findViewById<Button>(R.id.buttonBulk)
        val buttonModelBody = findViewById<Button>(R.id.buttonModelBody)

        buttonYoga.setOnClickListener {
            storeSessionData("Yoga")
        }

        buttonFatLoss.setOnClickListener {
            storeSessionData("Fat Loss")
        }

        buttonGainMuscle.setOnClickListener {
            storeSessionData("Gain Muscle")
            navigateToMuscleGain()
        }

        buttonBulk.setOnClickListener {
            storeSessionData("Bulk")
            navigateToBulkTraining()
        }

        buttonModelBody.setOnClickListener {
            storeSessionData("Model Body")
             navigateToModelBody()

        }

        // Retrieve current user ID
        val currentUser = auth.currentUser
        userId = currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(
                this@SelectTrainingActivity,
                "User ID is empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun storeSessionData(trainingType: String) {
        // Ensure userId is not empty
        if (userId.isEmpty()) {
            Toast.makeText(
                this@SelectTrainingActivity,
                "User ID is empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create session data map
        val sessionData = hashMapOf(
            "type" to trainingType,
            "timestamp" to System.currentTimeMillis()
            // Add more fields as needed
        )

        // Document ID for the current session (example: "currentSession")
        val currentSessionDocId = "currentSession"

        // Set the session data for the current user and current session
        db.collection("users").document(userId)
            .collection("trainingSessions")
            .document(currentSessionDocId)
            .set(sessionData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(
                    this@SelectTrainingActivity,
                    "Session updated to $trainingType successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to the training activity
                navigateToTraining(trainingType)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@SelectTrainingActivity,
                    "Error updating session to $trainingType: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun navigateToTraining(trainingType: String) {
        // Navigate to the training activity based on the selected type
        // Adjust intent creation based on your actual activity names and logic
        when (trainingType) {
            "Yoga" -> {
                val intent = Intent(this, YogaTrainingActivity::class.java)
                intent.putExtra("trainingType", trainingType)
                startActivity(intent)
            }
            // Add cases for other training types as needed
            else -> {
                Toast.makeText(
                    this@SelectTrainingActivity,
                    "Unknown training type",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun navigateToModelBody() {
        // Example: Navigate to the next activity for Model Body training
        val intent = Intent(this, ModelBodyActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToBulkTraining() {
        // Example: Navigate to the next activity for Bulk Training
        val intent = Intent(this, BulkTrainingActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToMuscleGain() {
        // Example: Navigate to the next activity for Muscle Gain training
        val intent = Intent(this, MuscleGainActivity::class.java)
        startActivity(intent)
    }
}
