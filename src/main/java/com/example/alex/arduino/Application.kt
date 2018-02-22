package com.example.alex.arduino

/**
 * Created by Alex on 14/02/2018.
 */
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException

class Application : android.app.Application() {
    private val logTag = Application::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler {
            if (it is IOException) {
                Log.w(logTag, "Unhandled Rx I/O error: " + it.message)
            } else {
                throw it
            }
        }
    }
}