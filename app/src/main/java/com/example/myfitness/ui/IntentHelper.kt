package com.example.myfitness.ui

import android.content.Context
import android.content.Intent

object IntentHelper {

    fun startNewActivity(context: Context, targetActivity: Class<*>) {
        val intent = Intent(context, targetActivity)
        context.startActivity(intent)
    }


}
