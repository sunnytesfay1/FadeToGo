package com.fadetogo.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadetogo.app.R
import com.fadetogo.app.ui.theme.DeepBlack
import com.fadetogo.app.ui.theme.TextGray
import com.fadetogo.app.ui.theme.TextSilver
import com.fadetogo.app.ui.theme.TextWhite
import com.fadetogo.app.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToCustomerHome: () -> Unit,
    onNavigateToBarberDashboard: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        if (currentUser != null) {
            if (currentUser!!.role == "barber") {
                onNavigateToBarberDashboard()
            } else {
                onNavigateToCustomerHome()
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // logo large and prominent
            Image(
                painter = painterResource(id = R.drawable.fadetogo_logo),
                contentDescription = "FadeToGo Logo",
                modifier = Modifier.size(320.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Barber. Your Location.",
                fontSize = 20.sp,
                color = TextSilver,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(56.dp))

            CircularProgressIndicator(
                color = TextWhite,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}