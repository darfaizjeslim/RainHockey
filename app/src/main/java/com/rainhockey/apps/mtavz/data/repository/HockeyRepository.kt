package com.rainhockey.apps.mtavz.data.repository

import com.rainhockey.apps.mtavz.data.dao.MatchDao
import com.rainhockey.apps.mtavz.data.dao.TeamDao
import com.rainhockey.apps.mtavz.data.models.Match
import com.rainhockey.apps.mtavz.data.models.Team
import com.rainhockey.apps.mtavz.data.models.TeamStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class HockeyRepository(
    private val teamDao: TeamDao,
    private val matchDao: MatchDao
) {
    fun getAllTeams(): Flow<List<Team>> = teamDao.getAllTeams()
    
    fun getTeamsByLeague(league: String): Flow<List<Team>> = 
        teamDao.getTeamsByLeague(league)
    
    suspend fun getTeamById(teamId: Long): Team? = teamDao.getTeamById(teamId)
    
    fun getAllMatches(): Flow<List<Match>> = matchDao.getAllMatches()
    
    fun getMatchesByLeague(league: String): Flow<List<Match>> = 
        matchDao.getMatchesByLeague(league)
    
    suspend fun insertMatch(match: Match) = matchDao.insert(match)
    
    suspend fun insertTeams(teams: List<Team>) = teamDao.insertAll(teams)
    
    suspend fun deleteAllData() {
        teamDao.deleteAll()
        matchDao.deleteAll()
    }
    
    suspend fun getTeamsCount(): Int = teamDao.getCount()
    
    fun getTeamStats(league: String): Flow<List<TeamStats>> {
        return combine(
            teamDao.getTeamsByLeague(league),
            matchDao.getMatchesByLeague(league)
        ) { teams, matches ->
            teams.map { team ->
                val teamMatches = matches.filter { 
                    it.homeTeamId == team.id || it.awayTeamId == team.id 
                }
                
                var wins = 0
                var losses = 0
                var overtimeLosses = 0
                var goalsFor = 0
                var goalsAgainst = 0
                
                teamMatches.forEach { match ->
                    val isHome = match.homeTeamId == team.id
                    val teamScore = if (isHome) match.homeScore else match.awayScore
                    val opponentScore = if (isHome) match.awayScore else match.homeScore
                    
                    goalsFor += teamScore
                    goalsAgainst += opponentScore
                    
                    when {
                        teamScore > opponentScore -> wins++
                        teamScore < opponentScore -> losses++
                        else -> overtimeLosses++
                    }
                }
                
                TeamStats(team, wins, losses, overtimeLosses, goalsFor, goalsAgainst)
            }.sortedByDescending { it.points }
        }
    }
}

