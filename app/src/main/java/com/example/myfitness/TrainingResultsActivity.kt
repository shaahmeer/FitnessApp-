package com.example.myfitness.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfitness.DateAdapter
import com.example.myfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TrainingResultsActivity : AppCompatActivity() {

    private lateinit var recyclerViewDates: RecyclerView
    private lateinit var adapter: DateAdapter
    private lateinit var selectedDate: Date
    private lateinit var userId: String

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tableLayoutTrainingResults: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_results)

        // Initialize views
        recyclerViewDates = findViewById(R.id.recyclerViewDates)
        tableLayoutTrainingResults = findViewById(R.id.tableLayoutTrainingResults)

        // Initialize selected date with today's date
        selectedDate = Calendar.getInstance().time

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            // Handle the case where no user is logged in
            finish()
            return
        }

        setupDateRecyclerView()
        loadTrainingSessions(selectedDate)

        findViewById<Button>(R.id.buttonClose).setOnClickListener {
            finish()
        }
    }

    private fun setupDateRecyclerView() {
        val dates = getDatesInMonth()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDates.layoutManager = layoutManager
        adapter = DateAdapter(dates, selectedDate) { date ->
            selectedDate = date
            adapter.setSelectedDate(selectedDate) // Update selected date in the adapter
            loadTrainingSessions(selectedDate)
        }
        recyclerViewDates.adapter = adapter
    }

    private fun loadTrainingSessions(date: Date) {
        // Format the selected date to match the date format used in Firestore
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateKey = sdf.format(date)

        // Clear previous training session views
        tableLayoutTrainingResults.removeAllViews()

        // Fetch training sessions for the selected date from Firestore
        db.collection("users").document(userId)
            .collection("trainingSessions")
            .document(dateKey)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("TrainingResultsActivity", "DocumentSnapshot data: ${documentSnapshot.data}")

                    // Retrieve all fields
                    val additionalNotes = documentSnapshot.getString("additionalNotes") ?: ""
                    val session = documentSnapshot.getString("session") ?: ""
                    val trainingType = documentSnapshot.getString("trainingType") ?: ""
                    val duration = documentSnapshot.getString("duration") ?: ""
                    val mealPlan = documentSnapshot.getString("mealPlan") ?: ""
                    val equipment = documentSnapshot.getString("equipment") ?: ""
                    val intensity = documentSnapshot.getString("intensity") ?: ""
                    val focus = documentSnapshot.getString("focus") ?: ""
                    val location = documentSnapshot.getString("location") ?: ""
                    val trainer = documentSnapshot.getString("trainer") ?: ""

                    // Add training session details to the TableLayout
                    addLinearLayout("Дополнительные заметки", additionalNotes)
                    addLinearLayout("Дата", sdf.format(date))
                    addLinearLayout("Сессия", session)
                    addLinearLayout("Тип тренировки", trainingType)
                    addLinearLayout("Продолжительность", "$duration мин")
                    addLinearLayout("План питания", mealPlan)
                    addLinearLayout("Оборудование", equipment)
                    addLinearLayout("Интенсивность", intensity)
                    addLinearLayout("Фокус", focus)
                    addLinearLayout("Место", location)
                    addLinearLayout("Тренер", trainer)
                } else {
                    Log.d("TrainingResultsActivity", "No training session data found for date: $dateKey")
                    displayNoTrainingSession()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TrainingResultsActivity", "Error fetching training sessions", e)
                displayNoTrainingSession()
            }
    }

    private fun addLinearLayout(label: String, value: String) {
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(16, 8, 16, 8)
        linearLayout.setBackgroundResource(R.drawable.curvy)

        val labelTextView = TextView(this)
        labelTextView.text = label
        labelTextView.setTextColor(Color.WHITE)
        labelTextView.textSize = 18f

        val valueTextView = TextView(this)
        valueTextView.text = value
        valueTextView.setTextColor(Color.WHITE)
        valueTextView.textSize = 16f

        linearLayout.addView(labelTextView)
        linearLayout.addView(valueTextView)

        tableLayoutTrainingResults.addView(linearLayout)
    }

    private fun displayNoTrainingSession() {
        val textView = TextView(this)
        textView.text = "На этот день нет планируемых тренировок"
        textView.textSize = 18f
        textView.setTextColor(Color.WHITE)
        textView.setPadding(16, 8, 16, 8)
        tableLayoutTrainingResults.addView(textView)
    }

    private fun getDatesInMonth(): List<Date> {
        val cal = Calendar.getInstance()
        val dates = mutableListOf<Date>()

        cal.time = selectedDate
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until daysInMonth) {
            dates.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }
}
