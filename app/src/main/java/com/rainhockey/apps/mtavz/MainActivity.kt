package com.rainhockey.apps.mtavz

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rainhockey.apps.mtavz.data.preferences.TokenPreferences
import com.rainhockey.apps.mtavz.navigation.Screen
import com.rainhockey.apps.mtavz.network.ServerService
import com.rainhockey.apps.mtavz.ui.screens.GameSimulationScreen
import com.rainhockey.apps.mtavz.ui.screens.HockeyScreen
import com.rainhockey.apps.mtavz.ui.screens.MainScreen
import com.rainhockey.apps.mtavz.ui.screens.SettingsScreen
import com.rainhockey.apps.mtavz.ui.screens.StatisticsScreen
import com.rainhockey.apps.mtavz.ui.screens.TeamSelectionScreen
import com.rainhockey.apps.mtavz.ui.theme.RainHockeyTheme
import com.rainhockey.apps.mtavz.utils.DeviceInfoUtil
import com.rainhockey.apps.mtavz.viewmodel.HockeyViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        setContent {
            RainHockeyTheme {
                HockeyApp()
            }
        }
    }
}

@Composable
fun HockeyApp() {
    val navController = rememberNavController()
    val viewModel: HockeyViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    
    var isInitializing by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf(Screen.Main.route) }
    
    LaunchedEffect(Unit) {
        val tokenPrefs = TokenPreferences(navController.context)
        
        if (tokenPrefs.hasToken()) {
            val address = tokenPrefs.getUrl() ?: ""
            if (address.isNotEmpty()) {
                startDestination = Screen.Hockey.createRoute(address)
            }
        } else {
            coroutineScope.launch {
                val serverLink = DeviceInfoUtil.buildServerUrl(navController.context)
                val result = ServerService.fetchServerData(serverLink)
                
                result.onSuccess { response ->
                    val (token, address) = ServerService.parseResponse(response)
                    if (token != null && address != null) {
                        tokenPrefs.saveTokenAndUrl(token, address)
                        startDestination = Screen.Hockey.createRoute(address)
                    } else {
                        startDestination = Screen.Main.route
                    }
                }.onFailure {
                    startDestination = Screen.Main.route
                }
                
                isInitializing = false
            }
            return@LaunchedEffect
        }
        
        isInitializing = false
    }
    
    BackHandler {
    }
    
    if (isInitializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(
                route = Screen.Hockey.route,
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val encodedAddress = backStackEntry.arguments?.getString("address") ?: ""
                val address = URLDecoder.decode(encodedAddress, "UTF-8")
                
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    onDispose {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
                
                HockeyScreen(address = address)
            }
            
            composable(Screen.Main.route) {
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onDispose { }
                }
                
                MainScreen(
                    onSimulationClick = { navController.navigate(Screen.TeamSelection.route) },
                    onStatisticsClick = { navController.navigate(Screen.Statistics.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            
            composable(Screen.TeamSelection.route) {
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onDispose { }
                }
                
                TeamSelectionScreen(
                    viewModel = viewModel,
                    onStartSimulation = { homeId, awayId ->
                        navController.navigate(Screen.GameSimulation.createRoute(homeId, awayId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.GameSimulation.route,
                arguments = listOf(
                    navArgument("homeTeamId") { type = NavType.LongType },
                    navArgument("awayTeamId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val homeTeamId = backStackEntry.arguments?.getLong("homeTeamId") ?: 0L
                val awayTeamId = backStackEntry.arguments?.getLong("awayTeamId") ?: 0L
                
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onDispose { }
                }
                
                GameSimulationScreen(
                    viewModel = viewModel,
                    homeTeamId = homeTeamId,
                    awayTeamId = awayTeamId,
                    onFinish = {
                        navController.popBackStack(Screen.Main.route, inclusive = false)
                    }
                )
            }
            
            composable(Screen.Statistics.route) {
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onDispose { }
                }
                
                StatisticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Settings.route) {
                val context = LocalContext.current
                val activity = context as? ComponentActivity
                
                DisposableEffect(Unit) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onDispose { }
                }
                
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}