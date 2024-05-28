package com.example.quizz

import android.app.Application

class QuizApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}