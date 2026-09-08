package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.KeyboardDataRepository
import com.example.data.preferences.KeyboardPreferences
import com.example.security.EncryptedCredentialStorageService

class NXVApplication : Application() {

    lateinit var preferences: KeyboardPreferences
        private set

    lateinit var credentialStorageService: EncryptedCredentialStorageService
        private set

    lateinit var repository: KeyboardDataRepository
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            preferences = KeyboardPreferences(this)
            database = AppDatabase.getInstance(this)
            credentialStorageService = EncryptedCredentialStorageService(this)
            repository = KeyboardDataRepository(
                clipboardDao = database.clipboardDao(),
                shortcutDao = database.shortcutDao(),
                dictionaryDao = database.dictionaryDao(),
                credentialDao = database.credentialDao(),
                analyticsDao = database.analyticsDao(),
                encryptedCredentialStorage = credentialStorageService
            )

            // Extract demo media files in background
            Thread {
                try {
                    val demoTargetDir = java.io.File(filesDir, "DemoMedia").apply { mkdirs() }
                    val assetList = assets.list("demo_media") ?: emptyArray()
                    for (assetName in assetList) {
                        val dest = java.io.File(demoTargetDir, assetName)
                        if (!dest.exists() || dest.length() == 0L) {
                            try {
                                assets.open("demo_media/$assetName").use { input ->
                                    dest.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.w("NXVApplication", "Could not extract demo media assets", e)
                }
            }.start()
        } catch (e: Throwable) {
            android.util.Log.e("NXVApplication", "Error initializing application dependencies", e)
        }
    }

    companion object {
        @Volatile
        lateinit var instance: NXVApplication
            private set
    }
}

