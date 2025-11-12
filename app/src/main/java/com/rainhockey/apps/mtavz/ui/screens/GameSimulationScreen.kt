package com.rainhockey.apps.mtavz.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.view.WindowCompat
import com.rainhockey.apps.mtavz.R
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainhockey.apps.mtavz.data.models.Match
import com.rainhockey.apps.mtavz.data.models.Team
import com.rainhockey.apps.mtavz.viewmodel.HockeyViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameSimulationScreen(
    viewModel: HockeyViewModel,
    homeTeamId: Long,
    awayTeamId: Long,
    onFinish: () -> Unit
) {
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }
    var homeScore by remember { mutableIntStateOf(0) }
    var awayScore by remember { mutableIntStateOf(0) }
    var currentPeriod by remember { mutableIntStateOf(1) }
    var timeRemaining by remember { mutableIntStateOf(20 * 60) }
    var isGameActive by remember { mutableStateOf(true) }
    var isPeriodTransition by remember { mutableStateOf(false) }
    var transitionText by remember { mutableStateOf("") }
    var showPuck by remember { mutableStateOf(false) }
    var puckTargetTeam by remember { mutableStateOf(0) }
    var showGameEnd by remember { mutableStateOf(false) }
    val view = LocalView.current
    
    LaunchedEffect(Unit) {
        homeTeam = viewModel.getTeamById(homeTeamId)
        awayTeam = viewModel.getTeamById(awayTeamId)
    }
    
    LaunchedEffect(showGameEnd) {
        if (showGameEnd) {
            view.post {
                val activity = view.context as? android.app.Activity
                activity?.window?.let { window ->
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.hide(WindowInsetsCompat.Type.systemBars())
                    insetsController.systemBarsBehavior = 
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
    
    LaunchedEffect(isGameActive, currentPeriod) {
        if (isGameActive) {
            while (timeRemaining > 0 && isGameActive) {
                delay(6)
                timeRemaining--
                
                val goalChance = if (currentPeriod == 1) 600 else 300
                if (Random.nextInt(goalChance) < 1) {
                    val scoringTeam = Random.nextInt(2)
                    if (scoringTeam == 0) {
                        homeScore++
                        puckTargetTeam = 1
                    } else {
                        awayScore++
                        puckTargetTeam = 0
                    }
                    showPuck = true
                    delay(1500)
                    showPuck = false
                }
            }
            
            if (timeRemaining == 0 && isGameActive) {
                if (currentPeriod < 3) {
                    isPeriodTransition = true
                    transitionText = "Period $currentPeriod Ended"
                    delay(2000)
                    transitionText = "Period ${currentPeriod + 1} Starting"
                    delay(2000)
                    isPeriodTransition = false
                    currentPeriod++
                    timeRemaining = 20 * 60
                } else {
                    isGameActive = false
                    showGameEnd = true
                }
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            ScoreBoard(
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                homeScore = homeScore,
                awayScore = awayScore,
                period = currentPeriod,
                timeRemaining = timeRemaining
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HockeyRink()
                
                if (showPuck) {
                    AnimatedPuck(targetTeam = puckTargetTeam)
                }
                
                if (isPeriodTransition) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Text(
                            text = transitionText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
        
        if (showGameEnd) {
            GameEndDialog(
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                homeScore = homeScore,
                awayScore = awayScore,
                onFinish = {
                    if (homeTeam != null && awayTeam != null) {
                        viewModel.saveMatch(
                            Match(
                                homeTeamId = homeTeamId,
                                awayTeamId = awayTeamId,
                                homeTeamName = "${homeTeam!!.city} ${homeTeam!!.name}",
                                awayTeamName = "${awayTeam!!.city} ${awayTeam!!.name}",
                                homeScore = homeScore,
                                awayScore = awayScore,
                                date = System.currentTimeMillis(),
                                league = homeTeam!!.league
                            )
                        )
                    }
                    onFinish()
                }
            )
        }
    }
}

@Composable
fun ScoreBoard(
    homeTeam: Team?,
    awayTeam: Team?,
    homeScore: Int,
    awayScore: Int,
    period: Int,
    timeRemaining: Int
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (homeTeam != null) {
                        val logoResId = homeTeam.getLogoResourceId(context)
                        if (logoResId != 0) {
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = "${homeTeam.city} ${homeTeam.name} logo",
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(homeTeam.logoColor)),
                                        shape = CircleShape
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = homeTeam.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = homeTeam.city,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "$homeScore - $awayScore",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Period $period",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${timeRemaining / 60}:${String.format("%02d", timeRemaining % 60)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (awayTeam != null) {
                        val logoResId = awayTeam.getLogoResourceId(context)
                        if (logoResId != 0) {
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = "${awayTeam.city} ${awayTeam.name} logo",
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(awayTeam.logoColor)),
                                        shape = CircleShape
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = awayTeam.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = awayTeam.city,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HockeyRink() {
    Image(
        painter = painterResource(id = R.drawable.ice_hockey_rink),
        contentDescription = "Hockey Rink",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun AnimatedPuck(targetTeam: Int) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        val targetX = if (targetTeam == 0) -150f else 150f
        val targetY = Random.nextFloat() * 100f - 50f
        
        offsetX.animateTo(
            targetValue = targetX,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    
    Box(
        modifier = Modifier
            .size(30.dp)
            .offset { IntOffset(offsetX.value.toInt(), offsetY.value.toInt()) }
            .background(Color.Black, CircleShape)
    )
}

@Composable
fun GameEndDialog(
    homeTeam: Team?,
    awayTeam: Team?,
    homeScore: Int,
    awayScore: Int,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Final Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (homeTeam != null) {
                            val logoResId = homeTeam.getLogoResourceId(context)
                            if (logoResId != 0) {
                                Image(
                                    painter = painterResource(id = logoResId),
                                    contentDescription = "${homeTeam.city} ${homeTeam.name} logo",
                                    modifier = Modifier.size(80.dp).padding(bottom = 8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Text(
                                text = homeTeam.city,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = homeTeam.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(0.3f))
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (awayTeam != null) {
                            val logoResId = awayTeam.getLogoResourceId(context)
                            if (logoResId != 0) {
                                Image(
                                    painter = painterResource(id = logoResId),
                                    contentDescription = "${awayTeam.city} ${awayTeam.name} logo",
                                    modifier = Modifier.size(80.dp).padding(bottom = 8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Text(
                                text = awayTeam.city,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = awayTeam.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$homeScore",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (homeScore > awayScore) Color.Green else Color.Red
                    )
                    
                    Text(
                        text = " - ",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Text(
                        text = "$awayScore",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (awayScore > homeScore) Color.Green else Color.Red
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Back to Main Menu",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

suspend fun HockeyViewModel.getTeamById(teamId: Long): Team? {
    return teams.value.find { it.id == teamId }
}

