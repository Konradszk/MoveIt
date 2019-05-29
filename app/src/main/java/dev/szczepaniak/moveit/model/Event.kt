package dev.szczepaniak.moveit.model

import java.util.*

data class Event(
    val id: Long,
    val name: String,
    val participants: List<String>,
    val location: String,
    val startDate: Date,
    val endDate: Date,
    var nextEventId: Long?
)