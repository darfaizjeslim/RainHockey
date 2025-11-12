package com.rainhockey.apps.mtavz.navigation

sealed class Screen(val route: String) {
    object Hockey : Screen("hockey/{address}") {
        fun createRoute(address: String) = "hockey/${java.net.URLEncoder.encode(address, "UTF-8")}"
    }
    object Main : Screen("main")
    object TeamSelection : Screen("team_selection")
    object GameSimulation : Screen("game_simulation/{homeTeamId}/{awayTeamId}") {
        fun createRoute(homeTeamId: Long, awayTeamId: Long) = "game_simulation/$homeTeamId/$awayTeamId"
    }
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

