package com.example.myfitness.ui

import java.io.Serializable

data class UserDetails(
    val name: String,
//   val name: String,
    val gender: String,
    val realWeight: String,
    val height: String,
//    val fitnessLevel: String,
//    val medicalConditions: String,
//    val injuries: String
) : Serializable
