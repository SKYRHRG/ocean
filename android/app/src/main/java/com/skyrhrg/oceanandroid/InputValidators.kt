package com.skyrhrg.oceanandroid

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InputValidators {
    fun isValidPassenger(name: String, age: String, gender: String): Boolean {
        val parsedAge = age.trim().toIntOrNull() ?: return false
        return name.trim().isNotEmpty() && parsedAge in 0..120 && gender.trim().uppercase() in setOf("M", "F", "T")
    }

    fun isValidOptionalPhone(phone: String): Boolean =
        phone.isBlank() || phone.matches(Regex("[0-9+() -]{7,20}"))

    fun isValidOptionalEmail(email: String): Boolean =
        email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidJourneyDate(date: String, now: Date = Date()): Boolean {
        if (!date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) return false
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.US).apply { isLenient = false }
        val parsed = try { format.parse(date) } catch (_: Exception) { null } ?: return false
        val today = format.parse(format.format(now)) ?: return false
        return !parsed.before(today)
    }

    fun areDistinctStations(from: String, to: String): Boolean =
        from.trim().isNotEmpty() && to.trim().isNotEmpty() && !from.trim().equals(to.trim(), ignoreCase = true)
}
