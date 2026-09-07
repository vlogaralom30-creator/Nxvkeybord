package com.example.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BackupRestoreSummary
import com.example.data.KeyboardDataRepository
import com.example.data.preferences.KeyboardPreferences
import com.example.data.preferences.KeyboardSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    settings: KeyboardSettings,
    preferences: KeyboardPreferences,
    repository: KeyboardDataRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var jsonInputText by remember { mutableStateOf("") }
    var restoreSummary by remember { mutableStateOf<BackupRestoreSummary?>(null) }
    var showConfirmRestoreDialog by remember { mutableStateOf(false) }
    var pendingRestoreJson by remember { mutableStateOf("") }

    // SAF File Picker for JSON backup file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    isProcessing = true
                    val content = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BufferedReader(InputStreamReader(stream)).readText()
                        } ?: ""
                    }
                    if (content.isNotBlank()) {
                        jsonInputText = content
                        pendingRestoreJson = content
                        showConfirmRestoreDialog = true
                    } else {
                        Toast.makeText(context, "ফাইলটি খালি বা পড়া যায়নি", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "ত্রুটি: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                } finally {
                    isProcessing = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ব্যাকআপ ও ডেটা রিকভারি (Backup & Restore)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "কিবোর্ড আনইন্সটল বা নতুন ফোনে সেটিংস ট্রান্সফার",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_backup_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High level info banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "কিবোর্ড ডেটা সুরক্ষিত রাখুন (100% Offline)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "JSON ফাইল ডাউনলোড করে রাখলে কিবোর্ড আনইন্সটল করলেও বা নতুন ডিভাইসে গেলে থিম, ক্লিপবোর্ড লিংক, ডিকশনারি ও টাইপিং ডেটা ফিরিয়ে আনা যাবে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // SECTION 1: EXPORT / DOWNLOAD BACKUP
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth().testTag("card_export_backup")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "১. ব্যাকআপ ফাইল ডাউনলোড / শেয়ার (Export)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ব্যাকআপে অন্তর্ভুক্ত থাকবে:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    BackupItemPill(icon = Icons.Default.Tune, label = "সব কিবোর্ড সেটিংস ও সাউন্ড/ভাইব্রেশন")
                    BackupItemPill(icon = Icons.Default.Palette, label = "সিলেক্টেড থিম (Ridmik / NXV) ও কালার")
                    BackupItemPill(icon = Icons.Default.ContentPaste, label = "ক্লিপবোর্ডে থাকা কপিড টেক্সট ও লিংক")
                    BackupItemPill(icon = Icons.Default.Book, label = "পার্সোনাল ডিকশনারির শেখা বাংলা ও ইংরেজি শব্দ")
                    BackupItemPill(icon = Icons.Default.Lock, label = "টাইপিং অ্যানালিটিক্স, বর্ণ/শব্দ ফ্রিকোয়েন্সি ও মেসেজ")

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Share JSON File Button
                        Button(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        val backupJson = repository.createFullBackupJson(settings)
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, backupJson)
                                            putExtra(Intent.EXTRA_TITLE, "NXV_Keyboard_Backup.json")
                                            type = "application/json"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Save or Share NXV Backup JSON"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "ব্যর্থ হয়েছে: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("btn_share_backup_json"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("JSON ফাইল শেয়ার", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Copy JSON to Clipboard Button
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        val backupJson = repository.createFullBackupJson(settings)
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        cm?.setPrimaryClip(ClipData.newPlainText("NXV_Backup_JSON", backupJson))
                                        Toast.makeText(context, "ব্যাকআপ JSON ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "ব্যর্থ হয়েছে: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("btn_copy_backup_json"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("JSON কপি করুন", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // SECTION 2: IMPORT / RESTORE FROM DEVICE
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth().testTag("card_import_recovery")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "২. ডেটা রিকভারি / ব্যাকআপ লোড (Import)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "নতুন ফোনে বা রিনস্টল করার পর আপনার ব্যাকআপ JSON ফাইল সিলেক্ট করুন বা নিচে পেস্ট করুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose JSON file from phone
                    Button(
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_select_backup_file"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ডিভাইস থেকে JSON ব্যাকআপ ফাইল সিলেক্ট করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(" অথবা সরাসরি পেস্ট করুন ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Text Field for JSON
                    OutlinedTextField(
                        value = jsonInputText,
                        onValueChange = { jsonInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("input_json_backup"),
                        placeholder = { Text("এখানে ব্যাকআপ JSON কোড পেস্ট করুন...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Restore Button
                    Button(
                        onClick = {
                            if (jsonInputText.trim().isBlank()) {
                                Toast.makeText(context, "দয়া করে JSON কোড পেস্ট করুন বা ফাইল বেছে নিন", Toast.LENGTH_SHORT).show()
                            } else {
                                pendingRestoreJson = jsonInputText.trim()
                                showConfirmRestoreDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_trigger_restore"),
                        shape = RoundedCornerShape(10.dp),
                        enabled = jsonInputText.isNotBlank() && !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("রিকভারি হচ্ছে...")
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("সব ডেটা পুনরুদ্ধার করুন (Restore Everything)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Success Summary Banner
            restoreSummary?.let { summary ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (summary.success) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (summary.success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("card_restore_summary")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (summary.success) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (summary.success) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = summary.message,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (summary.success) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        if (summary.success) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "পুনরুদ্ধারকৃত আইটেমসমূহ:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• কিবোর্ড সেটিংস: ${summary.restoredSettingsCount} টি কনফিগ", fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text("• ক্লিপবোর্ড ও নোটস: ${summary.restoredClipboardCount} টি", fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text("• ডিকশনারি ও শব্দ: ${summary.restoredDictionaryCount} টি", fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text("• টেক্সট শর্টকাট: ${summary.restoredShortcutsCount} টি", fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text("• বর্ণ/শব্দ অ্যানালিটিক্স ও লগ: ${summary.restoredLetterStatsCount + summary.restoredWordStatsCount} টি", fontSize = 12.sp, color = Color(0xFF1B5E20))
                        }
                    }
                }
            }
        }
    }

    // Restore Confirmation Dialog
    if (showConfirmRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmRestoreDialog = false },
            title = { Text("ডেটা রিকভারি নিশ্চিতকরণ") },
            text = {
                Text("আপনার আগের সমস্ত কিবোর্ড সেটিংস, নির্বাচিত থিম, ক্লিপবোর্ডে থাকা টেক্সট ও সংরক্ষিত শব্দ বর্তমান অ্যাপে লোড হবে। আপনি কি রিকভারি করতে চান?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmRestoreDialog = false
                        scope.launch {
                            isProcessing = true
                            val result = repository.restoreFullBackupFromJson(pendingRestoreJson, preferences)
                            restoreSummary = result
                            isProcessing = false
                            if (result.success) {
                                Toast.makeText(context, "রিকভারি সফল হয়েছে!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("হ্যাঁ, পুনরুদ্ধার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmRestoreDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun BackupItemPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
