package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NextActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var placeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        placeSpinner = findViewById(R.id.placeSpinner)
        val nextButton = findViewById<Button>(R.id.nextButton)

        // Populate the spinner with data
        val places = arrayOf("Gym", "Home", "Outdoors")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        placeSpinner.adapter = adapter

        nextButton.setOnClickListener {
            val selectedPlace = placeSpinner.selectedItem.toString()
            if (selectedPlace.isNotEmpty()) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("exercisePlace" to selectedPlace)

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@NextActivity,
                                "Exercise place saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToBodyTypeActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@NextActivity,
                                "Error saving exercise place: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@NextActivity,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@NextActivity,
                    "Please select a place of exercise",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToBodyTypeActivity() {
        val intent = Intent(this, BodyTypeActivity::class.java)
        startActivity(intent)
    }
}
