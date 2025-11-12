package com.rainhockey.apps.mtavz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rainhockey.apps.mtavz.data.models.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY date DESC")
    fun getAllMatches(): Flow<List<Match>>
    
    @Query("SELECT * FROM matches WHERE league = :league ORDER BY date DESC")
    fun getMatchesByLeague(league: String): Flow<List<Match>>
    
    @Insert
    suspend fun insert(match: Match)
    
    @Query("DELETE FROM matches")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM matches WHERE homeTeamId = :teamId OR awayTeamId = :teamId")
    fun getMatchesByTeam(teamId: Long): Flow<List<Match>>
}

