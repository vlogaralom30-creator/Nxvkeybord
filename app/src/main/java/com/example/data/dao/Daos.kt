package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ClipboardItem
import com.example.data.entity.DictionaryWord
import com.example.data.entity.SavedCredential
import com.example.data.entity.TextShortcut
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_items ORDER BY isPinned DESC, timestamp DESC LIMIT 500")
    fun getAllItems(): Flow<List<ClipboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClipboardItem): Long

    @Query("SELECT * FROM clipboard_items WHERE text = :text LIMIT 1")
    suspend fun findByText(text: String): ClipboardItem?

    @Query("UPDATE clipboard_items SET timestamp = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: Long, timestamp: Long)

    @Update
    suspend fun updateItem(item: ClipboardItem)

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_items WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("DELETE FROM clipboard_items")
    suspend fun clearAll()
}

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM text_shortcuts ORDER BY shortcut ASC")
    fun getAllShortcuts(): Flow<List<TextShortcut>>

    @Query("SELECT * FROM text_shortcuts WHERE LOWER(shortcut) = LOWER(:shortcut) LIMIT 1")
    suspend fun getExpansionFor(shortcut: String): TextShortcut?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: TextShortcut)

    @Query("DELETE FROM text_shortcuts WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary_words WHERE locale = :locale AND word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT 10")
    suspend fun getWordsMatching(locale: String, prefix: String): List<DictionaryWord>

    @Query("SELECT * FROM dictionary_words WHERE word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT 10")
    suspend fun getWordsMatchingAny(prefix: String): List<DictionaryWord>

    @Query("SELECT * FROM dictionary_words WHERE locale = :locale ORDER BY frequency DESC LIMIT 50")
    fun getTopWords(locale: String): Flow<List<DictionaryWord>>

    @Query("SELECT * FROM dictionary_words ORDER BY frequency DESC")
    fun getAllWords(): Flow<List<DictionaryWord>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: DictionaryWord): Long

    @Query("UPDATE dictionary_words SET frequency = frequency + 1 WHERE word = :word AND locale = :locale")
    suspend fun incrementFrequency(word: String, locale: String)

    @Query("DELETE FROM dictionary_words WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM dictionary_words")
    suspend fun clearAll()
}

@Dao
interface CredentialDao {
    @Query("SELECT * FROM saved_credentials ORDER BY isPinned DESC, timestamp DESC")
    fun getAllCredentials(): Flow<List<SavedCredential>>

    @Query("SELECT * FROM saved_credentials WHERE packageName = :packageName OR LOWER(serviceName) LIKE '%' || LOWER(:query) || '%' ORDER BY timestamp DESC")
    suspend fun getCredentialsFor(packageName: String, query: String): List<SavedCredential>

    @Query("SELECT * FROM saved_credentials WHERE LOWER(serviceName) = LOWER(:serviceName) AND LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun findMatching(serviceName: String, username: String): SavedCredential?

    @Query("SELECT * FROM saved_credentials WHERE password = :password LIMIT 1")
    suspend fun findByPassword(password: String): SavedCredential?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: SavedCredential): Long

    @Update
    suspend fun updateCredential(credential: SavedCredential)

    @Query("DELETE FROM saved_credentials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_credentials")
    suspend fun clearAll()
}

@Dao
interface AnalyticsDao {
    // Letters
    @Query("SELECT * FROM letter_usage_stats ORDER BY count DESC LIMIT :limit")
    fun getTopLetters(limit: Int = 100): Flow<List<com.example.data.entity.LetterUsageStat>>

    @Query("SELECT * FROM letter_usage_stats ORDER BY count DESC")
    suspend fun getAllLettersList(): List<com.example.data.entity.LetterUsageStat>

    @Query("SELECT * FROM letter_usage_stats WHERE letter = :letter LIMIT 1")
    suspend fun findLetter(letter: String): com.example.data.entity.LetterUsageStat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetterStat(stat: com.example.data.entity.LetterUsageStat): Long

    @Query("UPDATE letter_usage_stats SET count = count + 1, lastUsed = :timestamp WHERE letter = :letter")
    suspend fun incrementLetter(letter: String, timestamp: Long)

    @Query("DELETE FROM letter_usage_stats")
    suspend fun clearLetters()

    // Words
    @Query("SELECT * FROM word_usage_stats ORDER BY count DESC LIMIT :limit")
    fun getTopWords(limit: Int = 150): Flow<List<com.example.data.entity.WordUsageStat>>

    @Query("SELECT * FROM word_usage_stats ORDER BY count DESC")
    suspend fun getAllWordsList(): List<com.example.data.entity.WordUsageStat>

    @Query("SELECT * FROM word_usage_stats WHERE LOWER(word) = LOWER(:word) AND locale = :locale LIMIT 1")
    suspend fun findWord(word: String, locale: String): com.example.data.entity.WordUsageStat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordStat(stat: com.example.data.entity.WordUsageStat): Long

    @Query("UPDATE word_usage_stats SET count = count + 1, lastUsed = :timestamp WHERE LOWER(word) = LOWER(:word) AND locale = :locale")
    suspend fun incrementWord(word: String, locale: String, timestamp: Long)

    @Query("DELETE FROM word_usage_stats WHERE id = :id")
    suspend fun deleteWordStat(id: Long)

    @Query("DELETE FROM word_usage_stats")
    suspend fun clearWords()

    // Long Text / Message Logs
    @Query("SELECT * FROM long_text_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllTextLogs(): Flow<List<com.example.data.entity.LongTextLog>>

    @Query("SELECT * FROM long_text_logs ORDER BY timestamp DESC")
    suspend fun getAllTextLogsList(): List<com.example.data.entity.LongTextLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextLog(log: com.example.data.entity.LongTextLog): Long

    @Query("DELETE FROM long_text_logs WHERE id = :id")
    suspend fun deleteTextLog(id: Long)

    @Query("DELETE FROM long_text_logs")
    suspend fun clearAllTextLogs()
}

