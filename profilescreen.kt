package com.example.project.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
package com.example.project.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project.data.SocialRepository
import com.example.project.ui.components.GlassCard
import com.example.project.ui.components.XPProgressBar
import com.example.project.ui.theme.*

@Composable
fun ProfileScreen(
    repository: SocialRepository,
    innerPadding: PaddingValues
) {
    val user by repository.currentUserProfile.collectAsState()
    val posts by repository.posts.collectAsState()
    val userPosts = posts.filter { it.author.id == "me" }
    
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(MidnightBlack)
    ) {
        // High-Authority Header
        Box(modifier = Modifier.height(240.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Brush.verticalGradient(listOf(ElectricPurple, RoyalBlue, MidnightBlack)))
            )
            
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 0.dp)
            ) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MidnightBlack)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .border(2.dp, ElectricPurple, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Profile Info & Badge
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.username.uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, "Verified", tint = VerifiedBlue, modifier = Modifier.size(20.dp))
            }
            
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            // Advanced XP System
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                XPProgressBar(xp = user.xp, level = user.level)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ultimate Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                UltimateStat("${userPosts.size}", "NODES")
                UltimateStat("${(user.followersCount / 1000f).format(1)}K", "NETWORK")
                UltimateStat("${user.followingCount}", "UPLINKS")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Professional Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCharcoal, contentColor = Color.White)
                ) {
                    Text("RECONFIGURE NODE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledTonalIconButton(
                    onClick = { repository.logout() },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = ErrorRed.copy(alpha = 0.2f), contentColor = ErrorRed)
                ) {
                    Icon(Icons.Default.Logout, "Logout")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Content Matrix Tab
        TabRow(
            selectedTabIndex = 0, 
            containerColor = Color.Transparent, 
            contentColor = ElectricPurple,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[0]),
                    color = ElectricPurple,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            Tab(selected = true, onClick = {}, icon = { Icon(Icons.Default.GridView, null, modifier = Modifier.size(20.dp)) })
            Tab(selected = false, onClick = {}, icon = { Icon(Icons.Default.PlayCircleOutline, null, modifier = Modifier.size(20.dp), tint = Color.Gray) })
            Tab(selected = false, onClick = {}, icon = { Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(20.dp), tint = Color.Gray) })
        }

        // 4K-Style Post Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(userPosts) { post ->
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(DeepCharcoal),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { name, bio, pic ->
                repository.updateProfile(name, bio, pic)
                showEditDialog = false
            }
        )
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun UltimateStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, letterSpacing = 1.sp)
    }
}

@Composable
fun EditProfileDialog(
    user: com.example.project.models.User,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }
    var picUri by remember { mutableStateOf(user.profilePictureUrl) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                picUri = uri.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = { Text("CORE RECONFIGURATION", color = ElectricPurple, fontWeight = FontWeight.Black, fontSize = 16.sp) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Image Picker Preview
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MidnightBlack)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .border(2.dp, ElectricPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = picUri,
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White)
                    }
                }
                
                Text(
                    "TAP TO UPDATE AVATAR", 
                    fontSize = 10.sp, 
                    color = ElectricPurple, 
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username, 
                    onValueChange = { username = it }, 
                    label = { Text("NODE IDENTITY") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = bio, 
                    onValueChange = { bio = it }, 
                    label = { Text("NEURAL BIO") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(username, bio, picUri) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)) {
                Text("COMMIT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}
