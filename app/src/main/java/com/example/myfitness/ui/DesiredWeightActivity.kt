package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DesiredWeightActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desired_weight)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val editTextDesiredWeight = findViewById<EditText>(R.id.editTextDesiredWeight)
        val nextButton = findViewById<Button>(R.id.nextButton)

        nextButton.setOnClickListener {
            val desiredWeightText = editTextDesiredWeight.text.toString().trim()
            if (desiredWeightText.isNotEmpty()) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("desiredWeight" to desiredWeightText.toDouble())

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@DesiredWeightActivity,
                                "Desired weight saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToGenderActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@DesiredWeightActivity,
                                "Error saving desired weight: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@DesiredWeightActivity,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@DesiredWeightActivity,
                    "Please enter your desired weight",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun navigateToGenderActivity() {
        val intent = Intent(this, GenderActivity::class.java)
        startActivity(intent)
    }
}
