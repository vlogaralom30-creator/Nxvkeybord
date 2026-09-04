package com.example

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.test.core.app.ApplicationProvider
import com.example.ime.InputConnectionManager
import com.example.language.avro.AvroPhoneticEngine
import com.example.security.EncryptedCredentialStorageService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NXV Keyboard", appName)
    }

    @Test
    fun `test avro phonetic conversions`() {
        val engine = AvroPhoneticEngine()

        // Independent vowels
        assertEquals("আমি", engine.convertWord("ami"))
        assertEquals("তুমি", engine.convertWord("tumi"))
        assertEquals("ভালো", engine.convertWord("bhalo"))

        // Juktakkhor and clusters
        val bangladesh = engine.convertWord("bangladesh")
        assertTrue(bangladesh.contains("বাংলাদেশ"))

        // Candidates check
        val candidates = engine.getCandidates("kemon")
        assertTrue(candidates.isNotEmpty())
    }

    @Test
    fun `test encrypted credential storage service CRUD operations`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = EncryptedCredentialStorageService(context)

        // Clear initially
        storage.clearAllCredentials()

        // 1. Save new credential
        val saved = storage.saveCredential(
            serviceName = "Google",
            username = "test@gmail.com",
            password = "SecretPassword123!",
            packageName = "com.google.android.gm"
        )
        assertNotNull(saved)
        assertEquals("Google", saved.serviceName)
        assertEquals("test@gmail.com", saved.username)
        assertEquals("SecretPassword123!", saved.password)

        // 2. Retrieve credentials flow
        val list = storage.credentialsFlow.first()
        assertEquals(1, list.size)
        assertEquals("SecretPassword123!", list[0].password)

        // 3. Search by package or query
        val results = storage.getCredentialsForPackageOrQuery("com.google.android.gm")
        assertEquals(1, results.size)
        assertEquals("Google", results[0].serviceName)

        val queryResults = storage.getCredentialsForPackageOrQuery("", "google")
        assertEquals(1, queryResults.size)

        // 4. Update password
        val updated = storage.saveCredential(
            serviceName = "Google",
            username = "test@gmail.com",
            password = "NewUpdatedPassword456!",
            packageName = "com.google.android.gm"
        )
        assertEquals("NewUpdatedPassword456!", updated.password)
        val updatedList = storage.credentialsFlow.first()
        assertEquals(1, updatedList.size)
        assertEquals("NewUpdatedPassword456!", updatedList[0].password)

        // 5. Delete credential
        val deleted = storage.deleteCredential(updated.id)
        assertTrue(deleted)
        val emptyList = storage.credentialsFlow.first()
        assertTrue(emptyList.isEmpty())
    }

    @Test
    fun `test password and account field detection`() {
        val manager = InputConnectionManager()

        // Test standard password field
        val passwordInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        manager.update(null, passwordInfo)
        assertTrue(manager.isPasswordField())
        assertFalse(manager.isEmailOrAccountField())

        // Test web password field
        val webPassInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        manager.update(null, webPassInfo)
        assertTrue(manager.isPasswordField())

        // Test email/account field
        val emailInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        manager.update(null, emailInfo)
        assertFalse(manager.isPasswordField())
        assertTrue(manager.isEmailOrAccountField())
    }
}

