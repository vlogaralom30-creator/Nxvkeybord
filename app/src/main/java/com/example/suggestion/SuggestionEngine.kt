package com.example.suggestion

import com.example.data.KeyboardDataRepository
import com.example.language.avro.AvroPhoneticEngine
import com.example.language.bangla.BanglaLayouts
import com.example.language.english.EnglishEngine

data class SuggestionItem(
    val displayText: String,
    val replacementText: String,
    val isPrimary: Boolean = false,
    val isShortcut: Boolean = false
)

class SuggestionEngine(
    private val repository: KeyboardDataRepository
) {
    private val avroEngine = AvroPhoneticEngine()
    private val englishEngine = EnglishEngine()

    suspend fun getSuggestions(
        currentWord: String,
        mode: String, // "english", "bangla", "avro"
        isPasswordField: Boolean = false
    ): List<SuggestionItem> {
        if (isPasswordField || currentWord.isBlank()) {
            return getDefaultSuggestions(mode)
        }

        val results = mutableListOf<SuggestionItem>()
        val trimmed = currentWord.trim()

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

        when (mode.lowercase()) {
            "avro" -> {
                // 1. Avro phonetic conversion & dictionary candidate matches
                val candidates = avroEngine.getCandidates(trimmed, maxCandidates = 4)
                candidates.forEachIndexed { index, candidate ->
                    results.add(
                        SuggestionItem(
                            displayText = candidate,
                            replacementText = candidate,
                            isPrimary = index == 0 && !results.any { it.isShortcut }
                        )
                    )
                }

                // 2. Frequent word prefix suggestions from Avro engine
                val frequent = avroEngine.getFrequentWordSuggestions(trimmed, maxCount = 3)
                frequent.forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }

                // 3. User learned words from local Room database
                val learned = repository.getWordSuggestions(trimmed, "bn")
                learned.take(2).forEach { word ->
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
                    results.add(
                        SuggestionItem(
                            displayText = word,
                            replacementText = word,
                            isPrimary = index == 0
                        )
                    )
                }
                val learned = repository.getWordSuggestions(trimmed, "bn")
                learned.take(2).forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }
            }

            else -> {
                // English mode
                val englishWords = englishEngine.getSuggestions(trimmed, maxCount = 3)
                englishWords.forEachIndexed { index, word ->
                    results.add(
                        SuggestionItem(
                            displayText = word,
                            replacementText = word,
                            isPrimary = index == 0 && !results.any { it.isShortcut }
                        )
                    )
                }
                val learned = repository.getWordSuggestions(trimmed, "en")
                learned.take(2).forEach { word ->
                    if (results.none { it.replacementText == word }) {
                        results.add(SuggestionItem(displayText = word, replacementText = word))
                    }
                }
            }
        }

        return results.take(5)
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
