package com.example.myfitness.ui

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ExerciseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val client = OkHttpClient()
    private val API_KEY = "sk-proj-0HEHshOal86ZGvwDf7QXT3BlbkFJ34YxyaYP2rrkxg4LtLuP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()
        generateExerciseSuggestions()
    }

    // Function to generate exercise suggestions using AI
    private fun generateExerciseSuggestions() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        // Send user data to AI model and get exercise suggestions
                        getExerciseSuggestionsFromAI(userData)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e("ExerciseActivity", "Error fetching user data", exception)
                }
        }
    }

    // Function to send user data to AI model and get exercise suggestions
    private fun getExerciseSuggestionsFromAI(userData: Map<String, Any>?) {
        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo") // Specify the model parameter
            put("prompt", "Generate a list of exercise suggestions for the following user data: ${JSONObject(userData).toString()}")
            put("max_tokens", 150)
        }

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonObject.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ExerciseActivity", "Error calling OpenAI API", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseString = responseBody.string()
                    Log.d("ExerciseActivity", "API Response: $responseString") // Debug print the response
                    try {
                        val exerciseSuggestions = parseExerciseSuggestions(responseString)
                        runOnUiThread {
                            displayExerciseSuggestions(exerciseSuggestions)
                        }
                    } catch (e: Exception) {
                        Log.e("ExerciseActivity", "Error parsing exercise suggestions", e)
                    }
                }
            }
        })
    }

    // Function to parse exercise suggestions from the AI response
    private fun parseExerciseSuggestions(response: String): List<String> {
        val jsonResponse = JSONObject(response)
        return if (jsonResponse.has("choices")) {
            val choicesArray = jsonResponse.getJSONArray("choices")
            val text = choicesArray.getJSONObject(0).getString("text")
            text.split("\n").filter { it.isNotBlank() }
        } else {
            Log.e("ExerciseActivity", "No 'choices' key in the response: $response")
            emptyList() // Return an empty list if the expected key is not present
        }
    }

    // Function to display exercise suggestions in the UI
    private fun displayExerciseSuggestions(exerciseSuggestions: List<String>) {
        val exerciseLayout = findViewById<LinearLayout>(R.id.exerciseLayout)

        // Clear previous exercise suggestions
        exerciseLayout.removeAllViews()

        // Add exercise suggestions to the layout
        for (exercise in exerciseSuggestions) {
            val exerciseTextView = TextView(this)
            exerciseTextView.text = exercise
            exerciseLayout.addView(exerciseTextView)
        }
    }
}
