package com.rainhockey.apps.mtavz.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val city: String,
    val league: String,
    val logoColor: String,
    val logoResName: String = ""
) {
    @Ignore
    fun getLogoResourceId(context: android.content.Context): Int {
        return if (logoResName.isNotEmpty()) {
            context.resources.getIdentifier(logoResName, "drawable", context.packageName)
        } else {
            0
        }
    }
}

