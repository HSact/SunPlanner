package com.hsact.sunplanner.domain.model

data class MonthNameItem(
    val value: Int,
    val label: String
)
/*private fun prepareDate(month: String, day: Int): String {
    val monthNumber = when (month) {
        "January" -> "01"
        "February" -> "02"
        "March" -> "03"
        "April" -> "04"
        "May" -> "05"
        "June" -> "06"
        "July" -> "07"
        "August" -> "08"
        "September" -> "09"
        "October" -> "10"
        "November" -> "11"
        "December" -> "12"
        else -> "00"
    }
    val dayFormatted = day.toString().padStart(2, '0')
    return "$monthNumber-$dayFormatted"
}*/