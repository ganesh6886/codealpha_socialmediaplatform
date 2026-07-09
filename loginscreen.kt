package com.example.project.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.ui.theme.*
import com.example.project.data.SocialRepository
import com.example.project.ui.components.AppLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LoginScreen(repository: SocialRepository) {
    var isSignUp by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(MidnightBlack)) {
        DynamicBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header is always visible now for better UX, but scales when loading
            val headerScale by animateFloatAsState(if (isLoading) 0.8f else 1f, label = "")
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(headerScale)
            ) {
                LogoPulseAnimation()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "FriendLink",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.linearGradient(colors = listOf(ElectricPurple, CyberCyan, HotPink))
                    )
                )
                Text(
                    text = if (isSignUp) "INITIALIZING NODE" else "IDENTITY RECOGNITION",
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Show inputs OR Loading in a smoother cross-fade
            Box(contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = ""
                ) { loading ->
                    if (loading) {
                        CyberLoadingAnimation()
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            AdvancedTextField(
                                value = username,
                                onValueChange = { username = it; error = null },
                                label = "Username"
                            )

                            if (isSignUp) {
                                AdvancedTextField(
                                    value = email,
                                    onValueChange = { email = it; error = null },
                                    label = "Email Address"
                                )
                            }

                            AdvancedTextField(
                                value = password,
                                onValueChange = { password = it; error = null },
                                label = "Access Key (8+ Chars)",
                                isPassword = true
                            )

                            if (!isSignUp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(checkmarkColor = Color.White, checkedColor = ElectricPurple)
                                    )
                                    Text("Maintain Connection", color = Color.LightGray, fontSize = 14.sp)
                                }
                            }

                            if (error != null) {
                                Text(
                                    text = error!!,
                                    color = ErrorRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (password.length < 8) {
                                        error = "PROTOCOL ERROR: MINIMUM 8 CHARACTERS"
                                        return@Button
                                    }
                                    
                                    scope.launch {
                                        isLoading = true
                                        delay(400) // Brief visual confirmation
                                        val success = if (isSignUp) {
                                            repository.signUp(username, email, password)
                                        } else {
                                            repository.login(username, password, rememberMe)
                                        }
                                        
                                        if (!success) {
                                            error = "AUTHORIZATION FAILED"
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                            ) {
                                Text(
                                    if (isSignUp) "CREATE IDENTITY" else "DECRYPT ACCESS",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = { isSignUp = !isSignUp; error = null },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    if (isSignUp) "RETURNING PIONEER? LOGIN" else "NEW NODE? REGISTER",
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = GlassBorder,
            focusedBorderColor = ElectricPurple,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
fun DynamicBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color1 by infiniteTransition.animateColor(
        initialValue = DeepSpace,
        targetValue = Color(0xFF1A1A2E),
        animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Reverse), label = ""
    )
    Box(modifier = Modifier.fillMaxSize().background(color1)) {
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
            repeat(50) {
                drawCircle(
                    color = Color.White,
                    radius = Random.nextFloat() * 2f,
                    center = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height)
                )
            }
        }
    }
}

@Composable
fun LogoPulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = ""
    )
    Box(contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(90.dp).blur(30.dp).alpha(0.4f).background(ElectricPurple, CircleShape))
        Box(modifier = Modifier.scale(scale)) { AppLogo(size = 100.dp) }
    }
}

@Composable
fun CyberLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing)), label = ""
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 40.dp)) {
        Box(modifier = Modifier.size(60.dp).rotate(rotation).border(3.dp, Brush.sweepGradient(listOf(ElectricPurple, CyberCyan, HotPink, ElectricPurple)), CircleShape))
        Spacer(modifier = Modifier.height(24.dp))
        Text("VERIFYING IDENTITY...", color = CyberCyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
    }
}
