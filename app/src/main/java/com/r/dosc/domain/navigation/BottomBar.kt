package com.r.dosc.domain.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.get
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.r.dosc.domain.ui.theme.Ocean_Red
import com.r.dosc.presentation.appDestination
import com.r.dosc.presentation.destinations.Destination
import com.r.dosc.presentation.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.navigation.navigate

@ExperimentalAnimationApi
@ExperimentalPermissionsApi
@Composable
fun BottomBar(
    navController: NavController,
) {
    val currentDestination: Destination? =
        navController.currentBackStackEntryAsState()
            .value?.appDestination()

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        BottomNavigation(
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            BottomBarDestination.values().forEach { destination ->

                BottomNavigationItem(
                    selected = currentDestination == destination.direction,
                    onClick = {
                        navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                            popUpTo(navController.graph[HomeScreenDestination.route].id) {
                                saveState = true
                            }

                            launchSingleTop = true

                            restoreState = true
                        })

                    },
                    icon = {
                        NavigationItemIcon(
                            destination = destination,
                            selected = currentDestination == destination.direction
                        )
                    },
                    unselectedContentColor = Color.Gray,
                    selectedContentColor = Color.Black
                )
            }
        }
    }
}

@Composable
private fun NavigationItemIcon(destination: BottomBarDestination, selected: Boolean) {

    if (selected) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = destination.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onBackground,

                    )
                Spacer(modifier = Modifier.height(2.dp))

                androidx.compose.foundation.Canvas(modifier = Modifier.size(10.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Ocean_Red,
                                Color.Transparent,
                            ),
                            radius = 15f
                        ),
                        radius = 15f,
                    )
                }
            }
        }

    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .size(40.dp)
                .padding(top = 5.dp),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = destination.title,
                fontSize = 16.sp
            )
        }
    }
}

object ClearRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Transparent
    @Composable
    override fun rippleAlpha() = RippleAlpha(
        draggedAlpha = 0.0f,
        focusedAlpha = 0.0f,
        hoveredAlpha = 0.0f,
        pressedAlpha = 0.0f,
    )
}