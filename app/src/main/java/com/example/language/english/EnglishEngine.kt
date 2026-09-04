package com.example.language.english

class EnglishEngine {

    companion object {
        val QWERTY_ROW_1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
        val QWERTY_ROW_2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        val QWERTY_ROW_3 = listOf("z", "x", "c", "v", "b", "n", "m")

        val NUMBER_ROW = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

        val SYMBOL_ROW_1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val SYMBOL_ROW_2 = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/")
        val SYMBOL_ROW_3 = listOf("*", "\"", "'", ":", ";", "!", "?")

        val MORE_SYMBOLS_ROW_1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
        val MORE_SYMBOLS_ROW_2 = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
        val MORE_SYMBOLS_ROW_3 = listOf("%", "©", "®", "™", "✓", "[", "]")

        // English high-frequency word dictionary for fast autocomplete
        val COMMON_ENGLISH_WORDS = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
            "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
            "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
            "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
            "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
            "great", "hello", "thanks", "please", "morning", "night", "hope", "today", "tomorrow"
        )
    }

    fun getSuggestions(prefix: String, maxCount: Int = 4): List<String> {
        val query = prefix.lowercase().trim()
        if (query.isEmpty()) return listOf("I", "The", "Hello", "Thanks")
        return COMMON_ENGLISH_WORDS
            .filter { it.startsWith(query) && it != query }
            .take(maxCount)
            .map { if (prefix.first().isUpperCase()) it.replaceFirstChar { c -> c.uppercase() } else it }
    }
}
