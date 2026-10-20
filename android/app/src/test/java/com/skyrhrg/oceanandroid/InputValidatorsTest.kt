package com.skyrhrg.oceanandroid

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class InputValidatorsTest {
    @Test fun acceptsValidPassengerAndRejectsInvalidAgeOrGender() {
        assertTrue(InputValidators.isValidPassenger("Rani Ghosh", "29", "F"))
        assertFalse(InputValidators.isValidPassenger("", "29", "F"))
        assertFalse(InputValidators.isValidPassenger("Rani", "121", "F"))
        assertFalse(InputValidators.isValidPassenger("Rani", "abc", "F"))
        assertFalse(InputValidators.isValidPassenger("Rani", "29", "X"))
    }

    @Test fun optionalContactFieldsCanBeBlankButValidateWhenPresent() {
        assertTrue(InputValidators.isValidOptionalPhone(""))
        assertTrue(InputValidators.isValidOptionalPhone("+91 98765 43210"))
        assertFalse(InputValidators.isValidOptionalPhone("bad-phone-xxxxxxxxxxxxxxxx"))
        assertTrue(InputValidators.isValidOptionalEmail(""))
        assertTrue(InputValidators.isValidOptionalEmail("user@example.com"))
        assertFalse(InputValidators.isValidOptionalEmail("not-an-email"))
    }

    @Test fun journeyDateRejectsImpossibleDatesAndPastDates() {
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.US).parse("20-10-2026")!!
        assertTrue(InputValidators.isValidJourneyDate("20-10-2026", today))
        assertFalse(InputValidators.isValidJourneyDate("31-02-2027", today))
        assertFalse(InputValidators.isValidJourneyDate("19-10-2026", today))
        assertFalse(InputValidators.isValidJourneyDate("2026-10-20", today))
    }

    @Test fun stationsMustBeDifferentAndNonEmpty() {
        assertTrue(InputValidators.areDistinctStations("HWH", "NDLS"))
        assertFalse(InputValidators.areDistinctStations("", "NDLS"))
        assertFalse(InputValidators.areDistinctStations("HWH", " hwh "))
    }
}
