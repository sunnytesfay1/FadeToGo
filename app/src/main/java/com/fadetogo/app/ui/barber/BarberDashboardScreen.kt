package com.fadetogo.app.ui.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fadetogo.app.ui.theme.DeepBlack
import com.fadetogo.app.ui.theme.TextWhite
import com.fadetogo.app.viewmodel.AuthViewModel

@Composable
fun BarberDashboardScreen(
    onNavigateToIncoming: () -> Unit,
    onNavigateToEarnings: () -> Unit,
    onNavigateToInbox: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Barber Dashboard",
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}