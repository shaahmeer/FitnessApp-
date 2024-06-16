package com.example.myfitness.ui


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BodyTypeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var selectedBodyType: String
    private lateinit var imageEndomorph: ImageView
    private lateinit var imageMesomorph: ImageView
    private lateinit var imageEctomorph: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_type)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        imageEndomorph = findViewById(R.id.imageEndomorph)
        imageMesomorph = findViewById(R.id.imageMesomorph)
        imageEctomorph = findViewById(R.id.imageEctomorph)
        val nextButton = findViewById<Button>(R.id.nextButton)

        imageEndomorph.setOnClickListener { selectBodyType("Endomorph", it) }
        imageMesomorph.setOnClickListener { selectBodyType("Mesomorph", it) }
        imageEctomorph.setOnClickListener { selectBodyType("Ectomorph", it) }

        nextButton.setOnClickListener {
            if (::selectedBodyType.isInitialized) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userMap = mapOf("bodyType" to selectedBodyType)

                    db.collection("users")
                        .document(user.uid)
                        .update(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@BodyTypeActivity,
                                "Body type saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToNextActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@BodyTypeActivity,
                                "Error saving body type: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } ?: run {
                    Toast.makeText(
                        this@BodyTypeActivity,
                        "User not authenticated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@BodyTypeActivity,
                    "Please select your body type",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun selectBodyType(bodyType: String, view: View) {
        selectedBodyType = bodyType
        imageEndomorph.isSelected = false
        imageMesomorph.isSelected = false
        imageEctomorph.isSelected = false
        view.isSelected = true
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this, AgePreferenceActivity::class.java) // Replace with your next activity
        startActivity(intent)
    }
}
