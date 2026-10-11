package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.repository.AnimeRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.screens.CommunityGalleryScreen
import com.example.ui.screens.CreateStudioScreen
import com.example.ui.screens.CssStudioScreen
import com.example.ui.screens.EditorFxScreen
import com.example.ui.screens.NotebooksScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun YukitsuApp() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val context = LocalContext.current
        val databaseId = context.getString(R.string.firestore_database_id)
        val firestore = remember { FirebaseFirestore.getInstance(databaseId) }
        val repository = remember { AnimeRepository(firestore) }

        val viewModel: YukitsuViewModel = viewModel(
            key = user.uid,
            factory = viewModelFactory {
                initializer {
                    YukitsuViewModel(repository, user.uid)
                }
            }
        )

        MainAppScaffold(
            viewModel = viewModel,
            onSignOut = {
                viewModel.signOut { currentUser = null }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScaffold(
    viewModel: YukitsuViewModel,
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFFFF7A00), CircleShape),
                            color = Color(0xFF0E101D)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "Yukitsu Studio Kitsune Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "YUKITSU STUDIO • 雪津",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0C0E18)
                ),
                actions = {
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFFADB5BD)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0D0F1B),
                contentColor = Color.White
            ) {
                val navItems = listOf(
                    Triple(0, "Crear", Icons.Default.Palette),
                    Triple(1, "Editor FX", Icons.Default.Tune),
                    Triple(2, "Cuadernos", Icons.Default.Folder),
                    Triple(3, "Comunidad", Icons.Default.Groups),
                    Triple(4, "CSS Studio", Icons.Default.Brush)
                )

                navItems.forEach { (index, label, icon) ->
                    val isSelected = uiState.selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedTab(index) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp),
                                tint = if (isSelected) uiState.activeTheme.primaryColor else Color(0xFF868E96)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF868E96)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = uiState.activeTheme.primaryColor.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0C0D17))
        ) {
            when (uiState.selectedTab) {
                0 -> CreateStudioScreen(
                    viewModel = viewModel,
                    onNavigateToEditorFx = { viewModel.setSelectedTab(1) },
                    onNavigateToNotebooks = { viewModel.setSelectedTab(2) },
                    onNavigateToCommunity = { viewModel.setSelectedTab(3) }
                )
                1 -> EditorFxScreen(
                    viewModel = viewModel
                )
                2 -> NotebooksScreen(
                    viewModel = viewModel
                )
                3 -> CommunityGalleryScreen(
                    viewModel = viewModel,
                    onRemixPrompt = { prompt, genre, style ->
                        viewModel.setPrompt(prompt)
                        viewModel.selectGenre(genre)
                        viewModel.selectStyle(style)
                        viewModel.setSelectedTab(0)
                        viewModel.enhancePrompt()
                    }
                )
                4 -> CssStudioScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
