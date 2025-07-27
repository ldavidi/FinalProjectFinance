package com.example.finalprojectfinance

import android.app.Application
import com.google.firebase.FirebaseApp

class MyFinanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
