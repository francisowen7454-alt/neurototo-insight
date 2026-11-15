package com.example.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * SecurityUtils: Provides defensive cryptographic operations and input validation
 * to protect admin authentication, user data, and intent execution.
 */
object SecurityUtils {

    private val secureRandom = SecureRandom()

    /**
     * Generates a cryptographically secure random hexadecimal salt.
     */
    fun generateSalt(byteLength: Int = 16): String {
        val saltBytes = ByteArray(byteLength)
        secureRandom.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Computes a salted SHA-256 cryptographic hash of a plaintext password.
     */
    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        // Hash combination of salt + password to resist rainbow table precomputation
        val saltedInput = "$salt:$password"
        val hashBytes = digest.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies a candidate password against the stored salted hash using
     * constant-time comparison to prevent side-channel timing attacks.
     */
    fun verifyPassword(candidate: String, storedHash: String, storedSalt: String): Boolean {
        if (candidate.isBlank() || storedHash.isBlank() || storedSalt.isBlank()) {
            return false
        }
        val computedHash = hashPassword(candidate, storedSalt)
        val computedBytes = computedHash.toByteArray(Charsets.UTF_8)
        val storedBytes = storedHash.toByteArray(Charsets.UTF_8)

        return MessageDigest.isEqual(computedBytes, storedBytes)
    }

    /**
     * Validates password strength policy for administrative privileges.
     * Enforces:
     * - Minimum 8 characters
     * - At least one letter (a-z or A-Z)
     * - At least one digit (0-9)
     * - Rejection of common weak sequences
     */
    fun validatePasswordPolicy(password: String): PasswordValidationResult {
        if (password.length < 8) {
            return PasswordValidationResult(
                isValid = false,
                errorMessageSwahili = "Nenosiri lazima liwe na angalau herufi au tarakimu 8.",
                errorMessageEnglish = "Password must be at least 8 characters long."
            )
        }
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        if (!hasLetter || !hasDigit) {
            return PasswordValidationResult(
                isValid = false,
                errorMessageSwahili = "Nenosiri lazima liwe na mchanganyiko wa herufi na namba.",
                errorMessageEnglish = "Password must contain both letters and numbers."
            )
        }

        val lower = password.lowercase()
        val bannedPasswords = listOf("password", "12345678", "admin1234", "qwerty123")
        if (bannedPasswords.any { lower == it }) {
            return PasswordValidationResult(
                isValid = false,
                errorMessageSwahili = "Nenosiri hili ni jepesi sana. Chagua nenosiri salama zaidi.",
                errorMessageEnglish = "This password is too predictable. Please choose a stronger one."
            )
        }

        return PasswordValidationResult(isValid = true)
    }

    /**
     * Sanitizes external URLs before passing to Android Intent to prevent
     * scheme injection (e.g. javascript:, file:, content:, or malicious custom schemes).
     */
    fun isSafeWebUrl(url: String): Boolean {
        val trimmed = url.trim().lowercase()
        return trimmed.startsWith("https://")
    }

    /**
     * Validates that telephone dialer inputs only contain valid phone characters.
     */
    fun isSafePhoneNumber(phone: String): Boolean {
        val trimmed = phone.trim()
        return trimmed.matches(Regex("^\\+?[0-9\\s\\-()]{5,20}$"))
    }
}

data class PasswordValidationResult(
    val isValid: Boolean,
    val errorMessageSwahili: String = "",
    val errorMessageEnglish: String = ""
)
