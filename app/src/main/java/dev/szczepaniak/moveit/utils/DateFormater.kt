package dev.szczepaniak.moveit.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToHourAndMinutes(date: Date): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)
    return dateFormat.format(date)
}