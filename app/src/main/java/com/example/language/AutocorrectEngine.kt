package com.example.language

class AutocorrectEngine {

    companion object {
        private val ENGLISH_CORRECTIONS = mapOf(
            "teh" to "the",
            "adn" to "and",
            "youre" to "you're",
            "recieve" to "receive",
            "seperate" to "separate",
            "definately" to "definitely",
            "wont" to "won't",
            "cant" to "can't",
            "dont" to "don't",
            "didnt" to "didn't",
            "isnt" to "isn't",
            "arent" to "aren't",
            "wasnt" to "wasn't",
            "werent" to "weren't",
            "hasnt" to "hasn't",
            "havent" to "haven't",
            "hadnt" to "hadn't",
            "couldnt" to "couldn't",
            "wouldnt" to "wouldn't",
            "shouldnt" to "shouldn't",
            "im" to "I'm",
            "ive" to "I've",
            "id" to "I'd",
            "ill" to "I'll",
            "thier" to "their",
            "freind" to "friend",
            "becuase" to "because",
            "untill" to "until",
            "tommorow" to "tomorrow",
            "beleive" to "believe",
            "alot" to "a lot"
        )

        private val BANGLA_CORRECTIONS = mapOf(
            "পাখী" to "পাখি",
            "বাড়ী" to "বাড়ি",
            "পোষ্ট" to "পোস্ট",
            "মাষ্টার" to "মাস্টার",
            "শ্রেণী" to "শ্রেণি",
            "ধণ্যবাদ" to "ধন্যবাদ",
            "পূজো" to "পুজো",
            "পুরষ্কার" to "পুরস্কার",
            "আবিস্কার" to "আবিষ্কার",
            "সূচী" to "সূচি",
            "ধরণ" to "ধরন",
            "শহীদ" to "শহিদ",
            "লাইব্রেরী" to "লাইব্রেরি",
            "একডেমী" to "একাডেমি",
            "ষ্টেশন" to "স্টেশন",
            "ষ্টাইল" to "স্টাইল",
            "পোষ্টার" to "পোস্টার",
            "ইন্টারন্যাশানাল" to "ইন্টারন্যাশনাল",
            "সরকারী" to "সরকারি",
            "দাবী" to "দাবি",
            "প্রতিযোগীতা" to "প্রতিযোগিতা",
            "সহযোগীতা" to "সহযোগিতা"
        )

        private val BANGLISH_CORRECTIONS = mapOf(
            "kivabe" to "কিভাবে",
            "kibhabe" to "কিভাবে",
            "valo" to "ভালো",
            "bhalo" to "ভালো",
            "kemon" to "কেমন",
            "kmne" to "কেমনে",
            "shobai" to "সবাই",
            "sobai" to "সবাই",
            "dhonnobad" to "ধন্যবাদ",
            "dhonnobaad" to "ধন্যবাদ",
            "plz" to "please",
            "pls" to "please",
            "thnx" to "thanks",
            "tq" to "thank you"
        )
    }

    fun getCorrection(word: String, locale: String): String? {
        val trimmed = word.trim()
        if (trimmed.isEmpty()) return null

        return when (locale.lowercase()) {
            "bn", "bangla" -> {
                BANGLA_CORRECTIONS[trimmed] ?: BANGLISH_CORRECTIONS[trimmed.lowercase()]
            }
            "avro" -> {
                BANGLISH_CORRECTIONS[trimmed.lowercase()] ?: BANGLA_CORRECTIONS[trimmed]
            }
            else -> {
                val lower = trimmed.lowercase()
                val corrected = ENGLISH_CORRECTIONS[lower]
                if (corrected != null) {
                    if (trimmed.first().isUpperCase()) {
                        corrected.replaceFirstChar { it.uppercase() }
                    } else {
                        corrected
                    }
                } else null
            }
        }
    }
}
