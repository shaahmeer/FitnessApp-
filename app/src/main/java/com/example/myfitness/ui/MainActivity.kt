package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.example.myfitness.ui.Register
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // User is logged in
            checkUserRegistrationData(user.uid)
        } ?: run {
            // User is not logged in, proceed with login or registration
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish MainActivity so the user cannot go back to it after logging in
        }
    }

    private fun checkUserRegistrationData(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User's registration data exists, proceed to CoachDashboardActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish() // Finish MainActivity so the user cannot go back to it after updating their information
                } else {
                    // User's registration data doesn't exist, add it
                    val user = hashMapOf(
                        "age" to 0,
                        "weightGoal" to 0.0,
                        "realWeight" to 0.0,
                        "height" to 0.0
                    )
                    db.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            // Registration data added successfully, proceed to NameActivity
                            startActivity(Intent(this, NameActivity::class.java))
                            finish() // Finish MainActivity so the user cannot go back to it after adding their information
                        }
                        .addOnFailureListener { e ->
                            // Error occurred while adding registration data, handle appropriately
                            // For simplicity, just log the error
                            e.printStackTrace()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while checking user data, handle appropriately (e.g., show error message)
                // For simplicity, just log the error
                e.printStackTrace()
            }
    }
}
