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
    LOWERCASE,
    SHIFT_ONCE,
    CAPS_LOCK;

    val isUppercase: Boolean
        get() = this == SHIFT_ONCE || this == CAPS_LOCK

    val isCapsLock: Boolean
        get() = this == CAPS_LOCK

    val isShiftOnce: Boolean
        get() = this == SHIFT_ONCE

    val isLowercase: Boolean
        get() = this == LOWERCASE

    companion object {
        val OFF: ShiftState get() = LOWERCASE
        val ON: ShiftState get() = SHIFT_ONCE
    }
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
