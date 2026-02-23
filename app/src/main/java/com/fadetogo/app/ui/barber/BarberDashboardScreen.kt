package com.fadetogo.app.ui.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun BarberDashboardScreen(
    onNavigateToIncoming: () -> Unit,
    onNavigateToEarnings: () -> Unit,
    onNavigateToInbox: () -> Unit,
    onNavigateToSchedule: () -> Unit
) {
    // placeholder until we build the full barber dashboard
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Barber Dashboard",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}