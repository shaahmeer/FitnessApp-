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

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val editTextHeight = findViewById<EditText>(R.id.editTextHeight)
        val nextButton = findViewById<Button>(R.id.nextButton)

        nextButton.setOnClickListener {
            val heightText = editTextHeight.text.toString().trim()
            if (heightText.isNotEmpty()) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("height" to heightText.toDouble())

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@Register,
                                "Height saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToWeightActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@Register,
                                "Error saving height: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@Register,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@Register,
                    "Please enter your height",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Optionally, you can override this method to perform additional actions on back press.
    }

    private fun navigateToWeightActivity() {
        val intent = Intent(this, WeightActivity::class.java)
        startActivity(intent)
    }
}
