package com.example.myfitness.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AgePreferenceActivity : AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var dayEditText: EditText
    private lateinit var monthEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_preference)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        nextButton = findViewById(R.id.nextButton)
        dayEditText = findViewById(R.id.editTextDay)
        monthEditText = findViewById(R.id.editTextMonth)
        yearEditText = findViewById(R.id.editTextYear)

        // Get current user ID from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid ?: ""

        // Handle button click
        nextButton.setOnClickListener {
            // Attempt to save age preference to Firestore
            if (saveAgePreferenceToFirestore()) {
                // Navigate to Profile Picture activity only if age preference is saved successfully
                navigateToProfilePictureActivity()
            }
        }
    }

    private fun saveAgePreferenceToFirestore(): Boolean {
        // Ensure userId is not empty
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate date input
        val day = dayEditText.text.toString().toIntOrNull()
        val month = monthEditText.text.toString().toIntOrNull()
        val year = yearEditText.text.toString().toIntOrNull()

        // Check if the date is valid
        if (day == null || month == null || year == null || !isValidDate(day, month, year)) {
            Toast.makeText(this, "Invalid date of birth", Toast.LENGTH_SHORT).show()
            return false
        }

        // Calculate age based on entered date of birth
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - year
        if (month > currentMonth || (month == currentMonth && day > currentDay)) {
            age--
        }

        // Check if age is within the allowed range
        if (age < 15 || age > 70) {
            Toast.makeText(this, "Age must be between 15 and 70 years old", Toast.LENGTH_SHORT).show()
            return false
        }

        // Log the values to be saved
        Log.d(TAG, "Saving Date of Birth: Day: $day, Month: $month, Year: $year")

        // Create a reference to the "users" collection and update the user document
        val userDocRef = db.collection("users").document(userId)

        // Save date of birth directly under the user document in Firestore
        val dob = hashMapOf(
            "day" to day,
            "month" to month,
            "year" to year
        )
        userDocRef.update("dateOfBirth", dob)
            .addOnSuccessListener {
                Log.d(TAG, "Date of birth saved to Firestore")
                Toast.makeText(
                    this@AgePreferenceActivity,
                    "Date of birth saved to Firestore",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save date of birth", e)
                Toast.makeText(
                    this@AgePreferenceActivity,
                    "Failed to save date of birth: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Return true to indicate successful saving
        return true
    }

    private fun isValidDate(day: Int, month: Int, year: Int): Boolean {
        // Simple validation for date
        if (day < 1 || month < 1 || month > 12) {
            return false
        }
        val maxDaysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> return false
        }
        return day <= maxDaysInMonth
    }

    private fun navigateToProfilePictureActivity() {
        val intent = Intent(this, ProfilePictureActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "AgePreferenceActivity"
    }
}
