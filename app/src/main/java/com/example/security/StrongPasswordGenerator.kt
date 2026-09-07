package com.example.security

import java.security.SecureRandom
import androidx.compose.ui.graphics.Color

enum class StrengthRating(val label: String, val color: Color) {
    VERY_WEAK("Very Weak", Color(0xFFE53935)),
    WEAK("Weak", Color(0xFFFF9800)),
    FAIR("Fair", Color(0xFFFFC107)),
    STRONG("Strong", Color(0xFF4CAF50)),
    VERY_STRONG("Very Strong", Color(0xFF00C853))
}

data class PasswordStrength(
    val score: Int, // 0 to 100
    val rating: StrengthRating,
    val feedback: String
)

object StrongPasswordGenerator {

    private val random = SecureRandom()

    private const val UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ" // Omitted confusing characters O, I
    private const val LOWERCASE = "abcdefghijkmnopqrstuvwxyz" // Omitted l
    private const val DIGITS = "23456789" // Omitted 0, 1
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    private val WORDS = listOf(
        "falcon", "cobalt", "nexus", "quantum", "amber", "silver", "phoenix", "beacon", "galaxy",
        "orbit", "velvet", "thunder", "cipher", "matrix", "aurora", "breeze", "summit", "vanguard",
        "pulse", "shadow", "zenith", "vertex", "vector", "prism", "starlight", "horizon", "echo"
    )

    fun generatePassword(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        val effectiveLength = length.coerceIn(8, 32)
        val charPool = StringBuilder()

        val mandatory = mutableListOf<Char>()

        if (includeUpper) {
            charPool.append(UPPERCASE)
            mandatory.add(UPPERCASE[random.nextInt(UPPERCASE.length)])
        }
        if (includeLower) {
            charPool.append(LOWERCASE)
            mandatory.add(LOWERCASE[random.nextInt(LOWERCASE.length)])
        }
        if (includeDigits) {
            charPool.append(DIGITS)
            mandatory.add(DIGITS[random.nextInt(DIGITS.length)])
        }
        if (includeSymbols) {
            charPool.append(SYMBOLS)
            mandatory.add(SYMBOLS[random.nextInt(SYMBOLS.length)])
        }

        if (charPool.isEmpty()) {
            charPool.append(LOWERCASE).append(DIGITS)
        }

        val pool = charPool.toString()
        val result = mutableListOf<Char>()
        result.addAll(mandatory)

        while (result.size < effectiveLength) {
            result.add(pool[random.nextInt(pool.length)])
        }

        // Shuffle securely
        java.util.Collections.shuffle(result, random)
        return result.joinToString("")
    }

    fun generatePassphrase(wordCount: Int = 4, separator: String = "-"): String {
        val count = wordCount.coerceIn(3, 6)
        val selectedWords = (1..count).map {
            WORDS[random.nextInt(WORDS.size)].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }.toMutableList()

        val digit = random.nextInt(90) + 10
        val symbols = listOf("!", "@", "#", "$", "%", "&")
        val symbol = symbols[random.nextInt(symbols.size)]

        return selectedWords.joinToString(separator) + digit + symbol
    }

    fun calculateStrength(password: String): PasswordStrength {
        if (password.isBlank()) {
            return PasswordStrength(0, StrengthRating.VERY_WEAK, "Enter or generate a password")
        }

        var score = 0
        val length = password.length

        // Length score
        score += when {
            length >= 16 -> 40
            length >= 12 -> 30
            length >= 8 -> 15
            else -> 5
        }

        // Diversity score
        var hasUpper = false
        var hasLower = false
        var hasDigit = false
        var hasSymbol = false

        for (ch in password) {
            when {
                ch.isUpperCase() -> hasUpper = true
                ch.isLowerCase() -> hasLower = true
                ch.isDigit() -> hasDigit = true
                else -> hasSymbol = true
            }
        }

        val typesCount = listOf(hasUpper, hasLower, hasDigit, hasSymbol).count { it }
        score += typesCount * 12

        // Entropy / uniqueness boost
        val uniqueChars = password.toSet().size
        score += (uniqueChars.toFloat() / length * 12).toInt()

        // Penalize repeating sequential patterns
        if (password.contains("1234") || password.contains("qwerty") || password.contains("password")) {
            score -= 30
        }

        score = score.coerceIn(0, 100)

        val (rating, feedback) = when {
            score >= 85 -> StrengthRating.VERY_STRONG to "High entropy: Resistant to brute-force attacks"
            score >= 68 -> StrengthRating.STRONG to "Great password strength"
            score >= 48 -> StrengthRating.FAIR to "Good, but adding symbols or length improves security"
            score >= 25 -> StrengthRating.WEAK to "Too simple or short; easily crackable"
            else -> StrengthRating.VERY_WEAK to "Warning: Unsafe password!"
        }

        return PasswordStrength(score, rating, feedback)
    }
}
