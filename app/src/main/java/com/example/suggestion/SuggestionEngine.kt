package com.example.suggestion

import android.content.Context
import com.example.data.KeyboardDataRepository
import com.example.dictionary.AndroidUserDictionary
import com.example.language.AutocorrectEngine
import com.example.language.NextWordPredictor
import com.example.language.avro.AvroPhoneticEngine
import com.example.language.bangla.BanglaLayouts
import com.example.language.english.EnglishEngine

data class SuggestionItem(
    val displayText: String,
    val replacementText: String,
    val isPrimary: Boolean = false,
    val isShortcut: Boolean = false,
    val isAutocorrect: Boolean = false,
    val isPrediction: Boolean = false
)

class SuggestionEngine(
    private val repository: KeyboardDataRepository,
    context: Context? = null
) {
    private val avroEngine = AvroPhoneticEngine()
    private val englishEngine = EnglishEngine()
    val autocorrectEngine = AutocorrectEngine()
    val nextWordPredictor = NextWordPredictor()
    private val androidUserDictionary = context?.let { AndroidUserDictionary(it) }

    suspend fun getSuggestions(
        currentWord: String,
        mode: String, // "english", "bangla", "avro"
        contextTextBeforeCursor: String = "",
        isPasswordField: Boolean = false,
        autoCorrectEnabled: Boolean = true
    ): List<SuggestionItem> {
        if (isPasswordField) {
            return emptyList()
        }

        val trimmed = currentWord.trim()

        // When currentWord is empty, provide context-aware next-word predictions
        if (trimmed.isEmpty()) {
            if (contextTextBeforeCursor.isNotBlank()) {
                val predictions = nextWordPredictor.getNextWordPredictions(
                    contextTextBeforeCursor = contextTextBeforeCursor,
                    mode = mode,
                    maxCount = 4
                )
                if (predictions.isNotEmpty()) {
                    return predictions.mapIndexed { index, word ->
                        SuggestionItem(
                            displayText = word,
                            replacementText = word,
                            isPrimary = index == 0,
                            isPrediction = true
                        )
                    }
                }
            }
            return getDefaultSuggestions(mode)
        }

        val results = mutableListOf<SuggestionItem>()

        // 1. Check for Text Shortcuts
        val shortcutExpansion = repository.getShortcutExpansion(trimmed)
        if (shortcutExpansion != null) {
            results.add(
                SuggestionItem(
                    displayText = "⚡ $shortcutExpansion",
                    replacementText = shortcutExpansion,
                    isPrimary = true,
                    isShortcut = true
                )
            )
        }

        // 2. Check for Autocorrection match
        if (autoCorrectEnabled) {
            val correction = autocorrectEngine.getCorrection(trimmed, mode)
            if (correction != null && !correction.equals(trimmed, ignoreCase = true)) {
                results.add(
                    SuggestionItem(
                        displayText = correction,
                        replacementText = correction,
                        isPrimary = results.none { it.isShortcut },
                        isAutocorrect = true
                    )
                )
            }
        }

        when (mode.lowercase()) {
            "avro" -> {
                // 1. Avro phonetic conversion & dictionary candidate matches
                val candidates = avroEngine.getCandidates(trimmed, maxCandidates = 4)
                candidates.forEachIndexed { index, candidate ->
                    if (results.none { it.replacementText == candidate }) {
                        results.add(
                            SuggestionItem(
                                displayText = candidate,
                                replacementText = candidate,
                                isPrimary = index == 0 && !results.any { it.isShortcut || it.isAutocorrect }
                            )
                        )
                    }
                }

                // 2. Mixed-language candidate support (e.g. "Facebook", "meeting", "post", "phone")
                val mixedCandidates = nextWordPredictor.getMixedEnglishCandidates(trimmed, maxCount = 2)
                for (mixed in mixedCandidates) {
                    if (results.none { it.replacementText.equals(mixed, ignoreCase = true) }) {
                        results.add(
                            SuggestionItem(
                                displayText = mixed,
                                replacementText = mixed,
                                isPrimary = false
                            )
                        )
                    }
                }

                // 3. Frequent word prefix suggestions from Avro engine
                val frequent = avroEngine.getFrequentWordSuggestions(trimmed, maxCount = 3)
                frequent.forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }

                // 4. User learned words from local Room database
                val learned = repository.getWordSuggestions(trimmed, "bn")
                learned.take(3).forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }
            }

            "bangla" -> {
                // Bangla prefix matching
                val matching = BanglaLayouts.COMMON_WORDS
                    .filter { it.startsWith(trimmed) }
                    .take(4)
                matching.forEachIndexed { index, word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(
                            SuggestionItem(
                                displayText = word,
                                replacementText = word,
                                isPrimary = index == 0 && !results.any { it.isShortcut || it.isAutocorrect }
                            )
                        )
                    }
                }
                val learned = repository.getWordSuggestions(trimmed, "bn")
                learned.take(3).forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }
            }

            else -> {
                // English mode
                val englishWords = englishEngine.getSuggestions(trimmed, maxCount = 4)
                englishWords.forEachIndexed { index, word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(
                            SuggestionItem(
                                displayText = word,
                                replacementText = word,
                                isPrimary = index == 0 && !results.any { it.isShortcut || it.isAutocorrect }
                            )
                        )
                    }
                }

                // Query Android system User Dictionary if available
                androidUserDictionary?.let { aud ->
                    val userWords = aud.queryWords(trimmed, maxCount = 2)
                    for (uw in userWords) {
                        if (results.none { it.replacementText.equals(uw, ignoreCase = true) }) {
                            results.add(SuggestionItem(displayText = uw, replacementText = uw))
                        }
                    }
                }

                // Local Room learned words
                val learned = repository.getWordSuggestions(trimmed, "en")
                learned.take(3).forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }
            }
        }

        // Return up to 5 best ranked, non-duplicated suggestions
        return results.distinctBy { it.replacementText }.take(5)
    }

    private fun getDefaultSuggestions(mode: String): List<SuggestionItem> {
        return when (mode.lowercase()) {
            "avro" -> listOf(
                SuggestionItem("আমি", "আমি"),
                SuggestionItem("তুমি", "তুমি"),
                SuggestionItem("কেমন", "কেমন"),
                SuggestionItem("ভালো", "ভালো")
            )
            "bangla" -> listOf(
                SuggestionItem("বাংলাদেশ", "বাংলাদেশ"),
                SuggestionItem("ধন্যবাদ", "ধন্যবাদ"),
                SuggestionItem("কেমন", "কেমন"),
                SuggestionItem("আছো", "আছো")
            )
            else -> listOf(
                SuggestionItem("Hello", "Hello"),
                SuggestionItem("Thanks", "Thanks"),
                SuggestionItem("How", "How"),
                SuggestionItem("Hope", "Hope")
            )
        }
    }
}
