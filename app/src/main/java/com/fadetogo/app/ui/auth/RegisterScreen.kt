package com.fadetogo.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadetogo.app.ui.theme.*
import com.fadetogo.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("customer") }
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val registrationSuccess by authViewModel.registrationSuccess.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            currentUser?.let { user ->
                onRegisterSuccess(user.role)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Join FadeToGo today",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ROLE SELECTOR
            Text(
                text = "I am a...",
                color = TextGray,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // CUSTOMER BUTTON
                Button(
                    onClick = { selectedRole = "customer" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == "customer")
                            TextWhite else CardBlack,
                        contentColor = if (selectedRole == "customer")
                            DeepBlack else TextSilver
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Customer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // BARBER BUTTON
                Button(
                    onClick = { selectedRole = "barber" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == "barber")
                            TextWhite else CardBlack,
                        contentColor = if (selectedRole == "barber")
                            DeepBlack else TextSilver
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Barber",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REUSABLE TEXT FIELD STYLING
            @Composable
            fun ThemedTextField(
                value: String,
                onValueChange: (String) -> Unit,
                label: String,
                isPassword: Boolean = false
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPassword)
                        PasswordVisualTransformation() else
                        androidx.compose.ui.text.input.VisualTransformation.None,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = DividerGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = SurfaceBlack,
                        unfocusedContainerColor = SurfaceBlack,
                        cursorColor = TextWhite
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            ThemedTextField(name, { name = it }, "Full Name")
            ThemedTextField(email, { email = it }, "Email")
            ThemedTextField(phone, { phone = it }, "Phone Number")
            ThemedTextField(password, { password = it }, "Password", true)
            ThemedTextField(confirmPassword, { confirmPassword = it }, "Confirm Password", true)

            // ERROR MESSAGE
            val displayError = localError ?: errorMessage
            displayError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // REGISTER BUTTON
            Button(
                onClick = {
                    when {
                        name.isBlank() -> localError = "Please enter your name."
                        email.isBlank() -> localError = "Please enter your email."
                        phone.isBlank() -> localError = "Please enter your phone number."
                        password.isBlank() -> localError = "Please enter a password."
                        password != confirmPassword -> localError = "Passwords do not match."
                        password.length < 6 -> localError = "Password must be at least 6 characters."
                        else -> {
                            localError = null
                            authViewModel.register(
                                email = email,
                                password = password,
                                name = name,
                                phone = phone,
                                role = selectedRole
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextWhite,
                    contentColor = DeepBlack,
                    disabledContainerColor = SurfaceBlack
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = DeepBlack,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text(
                    text = "Already have an account? ",
                    color = TextSilver,
                    fontSize = 14.sp
                )
                Text(
                    text = "Login",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}