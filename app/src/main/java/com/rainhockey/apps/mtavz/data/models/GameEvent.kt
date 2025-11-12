package com.rainhockey.apps.mtavz.data.models

data class GameEvent(
    val type: EventType,
    val teamId: Long,
    val teamName: String,
    val period: Int,
    val time: Int
)

enum class EventType {
    GOAL,
    PERIOD_END,
    GAME_END
}

