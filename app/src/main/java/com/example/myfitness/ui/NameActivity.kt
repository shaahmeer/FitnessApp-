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

class NameActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var editTextName: EditText
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        editTextName = findViewById(R.id.textName)
        nextButton = findViewById(R.id.nextButton)

        // Handle next button click
        nextButton.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                saveNameToFirestore(name)
            }
        }
    }

    private fun saveNameToFirestore(name: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userMap = hashMapOf(
                "name" to name
            )

            db.collection("users").document(user.uid)
                .set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Name saved successfully", Toast.LENGTH_SHORT).show()
                    navigateToWeightActivity()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving name: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToWeightActivity() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}
