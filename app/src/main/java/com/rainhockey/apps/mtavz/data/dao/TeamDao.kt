package com.rainhockey.apps.mtavz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rainhockey.apps.mtavz.data.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<Team>>
    
    @Query("SELECT * FROM teams WHERE league = :league")
    fun getTeamsByLeague(league: String): Flow<List<Team>>
    
    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeamById(teamId: Long): Team?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teams: List<Team>)
    
    @Query("DELETE FROM teams")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM teams")
    suspend fun getCount(): Int
}

