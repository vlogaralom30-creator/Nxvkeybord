package com.example.ime

import android.os.Build
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class InputConnectionManager {

    private var inputConnection: InputConnection? = null
    private var editorInfo: EditorInfo? = null

    // Track active composing text (especially for Avro phonetic live typing)
    private var composingWord: StringBuilder = StringBuilder()

    fun update(ic: InputConnection?, info: EditorInfo?) {
        this.inputConnection = ic
        this.editorInfo = info
        this.composingWord.clear()
    }

    fun hasComposingText(): Boolean = composingWord.isNotEmpty()

    fun getComposingText(): String = composingWord.toString()

    fun clearComposing() {
        if (composingWord.isNotEmpty()) {
            inputConnection?.finishComposingText()
            composingWord.clear()
        }
    }

    fun isPasswordField(): Boolean {
        val info = editorInfo ?: return false
        val inputType = info.inputType
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        val clazz = inputType and InputType.TYPE_MASK_CLASS

        if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
            variation == 0x80 || // TYPE_TEXT_VARIATION_PASSWORD
            variation == 0x90 || // TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            variation == 0xe0    // TYPE_TEXT_VARIATION_WEB_PASSWORD
        ) {
            return true
        }

        if (clazz == InputType.TYPE_CLASS_NUMBER && (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0) {
            return true
        }

        // Additional hint detection
        val hint = info.hintText?.toString()?.lowercase() ?: ""
        if (hint.contains("password") || hint.contains("passcode") || hint.contains("pin code")) {
            return true
        }

        return false
    }

    fun isEmailOrAccountField(): Boolean {
        val info = editorInfo ?: return false
        val variation = info.inputType and InputType.TYPE_MASK_VARIATION
        if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS ||
            variation == InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        ) {
            return true
        }
        val hint = info.hintText?.toString()?.lowercase() ?: ""
        return hint.contains("email") || hint.contains("username") || hint.contains("login") || hint.contains("account")
    }

    fun isEmailField(): Boolean {
        val info = editorInfo ?: return false
        return (info.inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    fun isUrlField(): Boolean {
        val info = editorInfo ?: return false
        return (info.inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_URI
    }

    fun isNumberOnlyField(): Boolean {
        val info = editorInfo ?: return false
        val clazz = info.inputType and InputType.TYPE_MASK_CLASS
        return clazz == InputType.TYPE_CLASS_NUMBER || clazz == InputType.TYPE_CLASS_PHONE
    }

    fun isMultiline(): Boolean {
        val info = editorInfo ?: return false
        return (info.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
    }

    fun commitText(text: String) {
        val ic = inputConnection ?: return
        if (composingWord.isNotEmpty()) {
            ic.finishComposingText()
            composingWord.clear()
        }
        ic.commitText(text, 1)
    }

    fun setComposing(text: String, rawTypedWord: String) {
        val ic = inputConnection ?: return
        composingWord.clear()
        composingWord.append(rawTypedWord)
        ic.setComposingText(text, 1)
    }

    fun finishComposing(commitAs: String? = null) {
        val ic = inputConnection ?: return
        if (commitAs != null) {
            ic.commitText(commitAs, 1)
        } else {
            ic.finishComposingText()
        }
        composingWord.clear()
    }

    /**
     * Grapheme-aware safe backspace deletion.
     * Prevents breaking combining Bengali Unicode clusters and surrogate pairs.
     */
    fun handleDelete(): Boolean {
        val ic = inputConnection ?: return false

        // 1. If currently in active composing word (e.g. Avro live typing)
        if (composingWord.isNotEmpty()) {
            composingWord.deleteCharAt(composingWord.length - 1)
            if (composingWord.isEmpty()) {
                ic.finishComposingText()
                ic.deleteSurroundingText(1, 0)
                return false
            }
            return true // Caller should re-evaluate composing text with remaining chars
        }

        // 2. Safe deletion of selected text or surrounding code points
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val deleted = ic.deleteSurroundingTextInCodePoints(1, 0)
            if (deleted) return false
        }

        // Fallback for surrogate pairs or combining marks
        val before = ic.getTextBeforeCursor(2, 0)
        if (!before.isNullOrEmpty()) {
            if (Character.isSurrogate(before.last())) {
                ic.deleteSurroundingText(2, 0)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        } else {
            // Send backspace key event
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        }
        return false
    }

    fun handleEnter() {
        val ic = inputConnection ?: return
        finishComposing()

        val info = editorInfo
        val imeAction = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE

        if (imeAction != EditorInfo.IME_ACTION_NONE &&
            imeAction != EditorInfo.IME_ACTION_UNSPECIFIED &&
            !isMultiline()
        ) {
            ic.performEditorAction(imeAction)
        } else {
            ic.commitText("\n", 1)
        }
    }

    fun getCurrentWordBeforeCursor(): String {
        val ic = inputConnection ?: return ""
        val text = ic.getTextBeforeCursor(40, 0)?.toString() ?: return ""
        if (text.isEmpty()) return ""

        val lastIndex = text.indexOfLast { it.isWhitespace() || it in ".,;:!?()[]{}\"'/\\-+=|~" }
        return if (lastIndex == -1) {
            text
        } else {
            text.substring(lastIndex + 1)
        }
    }

    fun replaceCurrentWord(replacement: String) {
        val ic = inputConnection ?: return
        if (composingWord.isNotEmpty()) {
            ic.commitText(replacement, 1)
            composingWord.clear()
            return
        }

        val currentWord = getCurrentWordBeforeCursor()
        if (currentWord.isNotEmpty()) {
            ic.deleteSurroundingText(currentWord.length, 0)
        }
        ic.commitText(replacement, 1)
    }

    fun getEnterActionLabel(): String {
        val info = editorInfo ?: return "↵"
        return when (info.imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_SEARCH -> "Search"
            EditorInfo.IME_ACTION_SEND -> "Send"
            EditorInfo.IME_ACTION_NEXT -> "Next"
            EditorInfo.IME_ACTION_DONE -> "Done"
            else -> "↵"
        }
    }
}
