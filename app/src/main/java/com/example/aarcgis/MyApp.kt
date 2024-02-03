package com.example.aarcgis

import android.app.Application
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ArcGISRuntimeEnvironment.setApiKey("YOUR_API_KEY")
    }
}