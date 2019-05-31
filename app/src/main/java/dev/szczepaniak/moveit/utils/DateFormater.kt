package dev.szczepaniak.moveit.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToHourAndMinutes(date: Date): String {
    val dateFormat = SimpleDateFormat("hh:mm", Locale.ITALIAN)
    return dateFormat.format(date)
}