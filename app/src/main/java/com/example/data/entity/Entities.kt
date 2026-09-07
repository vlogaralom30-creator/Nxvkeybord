package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_items")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(
    tableName = "text_shortcuts",
    indices = [Index(value = ["shortcut"], unique = true)]
)
data class TextShortcut(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shortcut: String,
    val expansion: String
)

@Entity(
    tableName = "dictionary_words",
    indices = [Index(value = ["word", "locale"], unique = true)]
)
data class DictionaryWord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val frequency: Int = 1,
    val locale: String = "en"
)

@Entity(
    tableName = "saved_credentials",
    indices = [Index(value = ["serviceName", "username"])]
)
data class SavedCredential(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceName: String,
    val username: String,
    val password: String,
    val packageName: String = "",
    val siteUrl: String = "",
    val appName: String = "",
    val category: String = "General",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(
    tableName = "letter_usage_stats",
    indices = [Index(value = ["letter"], unique = true)]
)
data class LetterUsageStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val letter: String,
    val count: Long = 1,
    val lastUsed: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "word_usage_stats",
    indices = [Index(value = ["word", "locale"], unique = true)]
)
data class WordUsageStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val count: Long = 1,
    val locale: String = "en",
    val lastUsed: Long = System.currentTimeMillis()
)

@Entity(tableName = "long_text_logs")
data class LongTextLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val appName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

