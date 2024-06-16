package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GenderActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val nextButton = findViewById<Button>(R.id.nextButton)

        nextButton.setOnClickListener {
            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            if (selectedGenderId != -1) {
                val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("gender" to selectedGender)

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@GenderActivity,
                                "Gender saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToGenderActivity()                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@GenderActivity,
                                "Error saving gender: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@GenderActivity,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@GenderActivity,
                    "Please select your gender",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun navigateToGenderActivity() {
        val intent = Intent(this, BodyTypeActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
