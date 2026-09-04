package com.example.keyboard

enum class KeyboardMode {
    ENGLISH,
    BANGLA,
    AVRO,
    NUMBERS,
    SYMBOLS,
    EMOJI,
    CLIPBOARD,
    VAULT
}

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

sealed interface KeyAction {
    data class Character(
        val char: String,
        val shiftChar: String? = null,
        val altSymbol: String? = null
    ) : KeyAction

    object Space : KeyAction
    object Backspace : KeyAction
    object Enter : KeyAction
    object Shift : KeyAction
    object LanguageSwitch : KeyAction
    object ShowLanguageSelector : KeyAction
    data class SwitchMode(val targetMode: KeyboardMode) : KeyAction
    object CloseKeyboard : KeyAction
}
