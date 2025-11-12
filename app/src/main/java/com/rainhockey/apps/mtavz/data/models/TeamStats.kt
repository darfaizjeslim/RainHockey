package com.rainhockey.apps.mtavz.data.models

data class TeamStats(
    val team: Team,
    val wins: Int = 0,
    val losses: Int = 0,
    val overtimeLosses: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0
) {
    val points: Int
        get() = wins * 2 + overtimeLosses
    
    val gamesPlayed: Int
        get() = wins + losses + overtimeLosses
    
    val goalDifference: Int
        get() = goalsFor - goalsAgainst
}

