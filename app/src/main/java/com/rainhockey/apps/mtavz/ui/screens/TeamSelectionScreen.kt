package com.rainhockey.apps.mtavz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainhockey.apps.mtavz.data.models.Team
import com.rainhockey.apps.mtavz.viewmodel.HockeyViewModel

@Composable
fun TeamSelectionScreen(
    viewModel: HockeyViewModel,
    onStartSimulation: (Long, Long) -> Unit,
    onBack: () -> Unit
) {
    val teams by viewModel.teams.collectAsState()
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Teams",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Home Team",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = homeTeam?.let { "${it.city} ${it.name}" } ?: "Not selected",
                    fontSize = 14.sp,
                    color = if (homeTeam != null) Color.Green else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Away Team",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = awayTeam?.let { "${it.city} ${it.name}" } ?: "Not selected",
                    fontSize = 14.sp,
                    color = if (awayTeam != null) Color.Green else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teams) { team ->
                TeamCard(
                    team = team,
                    isHomeTeam = homeTeam?.id == team.id,
                    isAwayTeam = awayTeam?.id == team.id,
                    onHomeClick = {
                        if (awayTeam?.id != team.id) {
                            homeTeam = team
                        }
                    },
                    onAwayClick = {
                        if (homeTeam?.id != team.id) {
                            awayTeam = team
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            Button(
                onClick = {
                    if (homeTeam != null && awayTeam != null) {
                        onStartSimulation(homeTeam!!.id, awayTeam!!.id)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = homeTeam != null && awayTeam != null
            ) {
                Text("Start")
            }
        }
    }
}

@Composable
fun TeamCard(
    team: Team,
    isHomeTeam: Boolean,
    isAwayTeam: Boolean,
    onHomeClick: () -> Unit,
    onAwayClick: () -> Unit
) {
    val context = LocalContext.current
    val logoResId = team.getLogoResourceId(context)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (logoResId != 0) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "${team.city} ${team.name} logo",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(team.logoColor)),
                            shape = CircleShape
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = team.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = team.city,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp, 36.dp)
                        .background(
                            color = if (isHomeTeam) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable(onClick = onHomeClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Home",
                        fontSize = 12.sp,
                        color = if (isHomeTeam) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(60.dp, 36.dp)
                        .background(
                            color = if (isAwayTeam) MaterialTheme.colorScheme.secondary 
                                   else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable(onClick = onAwayClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Away",
                        fontSize = 12.sp,
                        color = if (isAwayTeam) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

