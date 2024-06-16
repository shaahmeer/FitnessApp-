package com.example.myfitness.ui

import android.content.Intent

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePictureActivity : AppCompatActivity() {

    private lateinit var uploadImageButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        // Initialize views
        uploadImageButton = findViewById(R.id.uploadImageButton)
        profileImageView = findViewById(R.id.profileImageView)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Get current user ID from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid ?: ""

        // Handle image upload button click
        uploadImageButton.setOnClickListener {
            // Start an image picker activity
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data

            // Update profile image in Firestore (example implementation)
            selectedImageUri?.let { uri ->
                // Example: Update profile image URL in Firestore user document
                db.collection("users").document(userId)
                    .update("profileImage", uri.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this@ProfilePictureActivity, "Profile image uploaded", Toast.LENGTH_SHORT).show()
                        profileImageView.setImageURI(uri)
                        navigateToProfilePictureActivity()// Display selected image in ImageView
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@ProfilePictureActivity, "Failed to upload profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun navigateToProfilePictureActivity() {
        val intent = Intent(this, SelectTrainingActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Optionally handle back navigation behavior
    }
}
