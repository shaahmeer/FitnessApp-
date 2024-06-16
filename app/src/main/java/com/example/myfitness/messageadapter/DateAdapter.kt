package com.example.myfitness

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(
    private val dates: List<Date>,
    private var selectedDate: Date,
    private val onItemClick: (Date) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]
        holder.bind(date)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    fun setSelectedDate(date: Date) {
        selectedDate = date
        notifyDataSetChanged()
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(dates[position])
                }
            }
        }

        fun bind(date: Date) {
            val sdf = SimpleDateFormat("EEE\ndd/MM", Locale.getDefault()) // Example: "Mon\n16/06"
            val formattedDate = sdf.format(date)

            textViewDate.text = formattedDate

            // Highlight the selected date
            if (date == selectedDate) {
                textViewDate.setBackgroundResource(R.drawable.circle) // Use a drawable for background
                textViewDate.setTextColor(Color.WHITE)
            } else {
                textViewDate.setBackgroundResource(R.drawable.curvy) // Default background drawable
                textViewDate.setTextColor(Color.WHITE)
            }
        }
    }
}
