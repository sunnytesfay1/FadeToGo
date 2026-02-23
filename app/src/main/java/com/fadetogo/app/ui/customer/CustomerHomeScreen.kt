package com.fadetogo.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadetogo.app.ui.theme.*
import com.fadetogo.app.viewmodel.AuthViewModel
import com.fadetogo.app.viewmodel.BookingViewModel
import com.fadetogo.app.viewmodel.MessageViewModel

@Composable
fun CustomerHomeScreen(
    onNavigateToBooking: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToInbox: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel(),
    messageViewModel: MessageViewModel = viewModel()
) {
    // observe current logged in user
    val currentUser by authViewModel.currentUser.collectAsState()

    // observe barber settings to check availability
    val barberSettings by bookingViewModel.barberSettings.collectAsState()

    // observe unread message count for inbox badge
    val unreadCount by messageViewModel.unreadCount.collectAsState()

    // track which bottom nav item is selected
    var selectedNavItem by remember { mutableStateOf(0) }

    // load barber settings and unread count when screen opens
    // for now we use a hardcoded barber id
    // later this will be fetched dynamically
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            messageViewModel.loadUnreadCount(user.uid)
        }
    }

    Scaffold(
        // BOTTOM NAVIGATION BAR
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceBlack,
                contentColor = TextWhite
            ) {
                // HOME TAB
                NavigationBarItem(
                    selected = selectedNavItem == 0,
                    onClick = { selectedNavItem = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = CardBlack
                    )
                )

                // HISTORY TAB
                NavigationBarItem(
                    selected = selectedNavItem == 1,
                    onClick = {
                        selectedNavItem = 1
                        onNavigateToHistory()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History"
                        )
                    },
                    label = {
                        Text(
                            text = "History",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = CardBlack
                    )
                )

                // INBOX TAB with unread badge
                NavigationBarItem(
                    selected = selectedNavItem == 2,
                    onClick = {
                        selectedNavItem = 2
                        onNavigateToInbox()
                    },
                    icon = {
                        // show badge if there are unread messages
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = ErrorRed
                                    ) {
                                        Text(
                                            text = unreadCount.toString(),
                                            fontSize = 10.sp,
                                            color = TextWhite
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Inbox"
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Inbox",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = TextWhite,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = CardBlack
                    )
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // GREETING HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hey, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "there"} 👋",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Ready for a fresh cut?",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }

                    // USER AVATAR - shows first letter of their name
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.firstOrNull()?.toString() ?: "?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // BARBER STATUS CARD
                // shows whether the barber is currently available
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceBlack
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // AVAILABILITY INDICATOR DOT
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (barberSettings?.isAvailable == true)
                                            SuccessGreen else TextGray
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (barberSettings?.isAvailable == true)
                                    "Barber is Available" else "Barber is Offline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (barberSettings?.isAvailable == true)
                                    SuccessGreen else TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (barberSettings?.isAvailable == true)
                                "Book now and get a fresh cut at your location"
                            else
                                "Check back later when the barber is online",
                            fontSize = 13.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // BOOK NOW BUTTON
                        // only enabled when barber is available
                        Button(
                            onClick = { onNavigateToBooking() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TextWhite,
                                contentColor = DeepBlack,
                                disabledContainerColor = CardBlack,
                                disabledContentColor = TextGray
                            ),
                            enabled = barberSettings?.isAvailable == true
                        ) {
                            Text(
                                text = if (barberSettings?.isAvailable == true)
                                    "Book Now" else "Unavailable",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // PRICING INFO CARD
                // gives the customer a heads up about travel fees
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBlack
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "How Pricing Works",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PricingInfoRow(
                            icon = "📍",
                            text = "Within ${barberSettings?.baseRadiusMiles?.toInt() ?: 10} miles — no travel fee"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PricingInfoRow(
                            icon = "🚗",
                            text = "Beyond that — \$${barberSettings?.costPerMile ?: 1.50}/mile surcharge"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PricingInfoRow(
                            icon = "🚫",
                            text = "Beyond ${barberSettings?.maxRadiusMiles?.toInt() ?: 30} miles — outside service area"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // LOGOUT BUTTON - subtle at the bottom
                TextButton(
                    onClick = { authViewModel.logout() }
                ) {
                    Text(
                        text = "Sign Out",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// reusable row for the pricing info section
@Composable
fun PricingInfoRow(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextSilver
        )
    }
}