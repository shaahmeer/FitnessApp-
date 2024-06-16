package com.example.myfitness.data

data class Message(
    val senderId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val timestamp: Long = 0L // Updated to Long type
)
