package com.aarogyam.domain

import java.math.RoundingMode
import java.text.DecimalFormat

object UnitConverter {

    private const val KG_TO_LBS_FACTOR = 2.20462
    private const val LBS_TO_KG_FACTOR = 0.453592

    fun kgToLbs(kg: Double): Double = kg * KG_TO_LBS_FACTOR

    fun lbsToKg(lbs: Double): Double = lbs * LBS_TO_KG_FACTOR

    fun format(kg: Double, unit: WeightUnit): String {
        val df = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.HALF_UP
        }
        val value = toDisplay(kg, unit)
        return "${df.format(value)} ${unitLabel(unit)}"
    }

    fun toStorageKg(displayValue: Double, unit: WeightUnit): Double {
        return when (unit) {
            WeightUnit.KG -> displayValue
            WeightUnit.LBS -> lbsToKg(displayValue)
        }
    }

    fun toDisplay(kg: Double, unit: WeightUnit): Double {
        return when (unit) {
            WeightUnit.KG -> kg
            WeightUnit.LBS -> kgToLbs(kg)
        }
    }

    fun unitLabel(unit: WeightUnit): String = unit.label
}
