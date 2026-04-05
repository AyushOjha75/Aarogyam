package com.aarogyam

import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    @Test
    fun kgToLbs_knownValue() {
        val result = UnitConverter.kgToLbs(1.0)
        assertEquals(2.20462, result, 0.0001)
    }

    @Test
    fun lbsToKg_knownValue() {
        val result = UnitConverter.lbsToKg(1.0)
        assertEquals(0.453592, result, 0.0001)
    }

    @Test
    fun roundTrip_kg_to_lbs_and_back() {
        val originalKg = 70.0
        val lbs = UnitConverter.kgToLbs(originalKg)
        val backToKg = UnitConverter.lbsToKg(lbs)
        assertEquals(originalKg, backToKg, 0.0001)
    }

    @Test
    fun toStorageKg_withKgUnit_returnsSameValue() {
        val result = UnitConverter.toStorageKg(75.0, WeightUnit.KG)
        assertEquals(75.0, result, 0.0001)
    }

    @Test
    fun toStorageKg_withLbsUnit_convertsToKg() {
        val lbs = 165.347
        val expectedKg = UnitConverter.lbsToKg(lbs)
        val result = UnitConverter.toStorageKg(lbs, WeightUnit.LBS)
        assertEquals(expectedKg, result, 0.0001)
    }

    @Test
    fun toDisplay_withKgUnit_returnsSameValue() {
        val result = UnitConverter.toDisplay(80.0, WeightUnit.KG)
        assertEquals(80.0, result, 0.0001)
    }

    @Test
    fun toDisplay_withLbsUnit_convertsFromKg() {
        val kg = 80.0
        val expectedLbs = UnitConverter.kgToLbs(kg)
        val result = UnitConverter.toDisplay(kg, WeightUnit.LBS)
        assertEquals(expectedLbs, result, 0.0001)
    }

    @Test
    fun format_kg_unit_containsKg() {
        val result = UnitConverter.format(70.0, WeightUnit.KG)
        assert(result.contains("kg")) { "Expected 'kg' in result: $result" }
    }

    @Test
    fun format_lbs_unit_containsLbs() {
        val result = UnitConverter.format(70.0, WeightUnit.LBS)
        assert(result.contains("lbs")) { "Expected 'lbs' in result: $result" }
    }
}
