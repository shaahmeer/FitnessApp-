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

class WeightActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val editTextWeight = findViewById<EditText>(R.id.editTextWeight)
        val nextButton = findViewById<Button>(R.id.nextButton)

        nextButton.setOnClickListener {
            val weightText = editTextWeight.text.toString().trim()
            if (weightText.isNotEmpty()) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("weight" to weightText.toDouble())

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@WeightActivity,
                                "Weight saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToDesiredWeightActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@WeightActivity,
                                "Error saving weight: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@WeightActivity,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@WeightActivity,
                    "Please enter your weight",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun navigateToDesiredWeightActivity() {
        val intent = Intent(this, DesiredWeightActivity::class.java)
        startActivity(intent)
    }
}
