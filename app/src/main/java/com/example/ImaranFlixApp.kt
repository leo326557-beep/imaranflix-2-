package com.example

import android.app.Application
import com.example.di.AppContainer
import com.google.firebase.FirebaseApp

class ImaranFlixApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
