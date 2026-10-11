package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.anime.AnimeStyleCatalog
import com.example.data.model.CommunityCreation
import com.example.ui.YukitsuViewModel

@Composable
fun CommunityGalleryScreen(
    viewModel: YukitsuViewModel,
    onRemixPrompt: (String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val communityCreations by viewModel.communityCreations.collectAsState()
    val context = LocalContext.current

    var selectedGenreFilter by remember { mutableStateOf("all") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Column {
            Text(
                text = "雪津スタジオ • COMUNIDAD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = uiState.activeTheme.secondaryColor,
                letterSpacing = 1.sp
            )
            Text(
                text = "Galería Comunitaria",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Explora el arte anime creado por la comunidad, vota por tus creaciones favoritas y remezcla sus prompts con un solo toque.",
                fontSize = 12.sp,
                color = Color(0xFF9097A6)
            )
        }

        // Genre Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedGenreFilter == "all",
                onClick = { selectedGenreFilter = "all" },
                label = { Text("Todos") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = uiState.activeTheme.primaryColor,
                    selectedLabelColor = Color.White
                )
            )

            AnimeStyleCatalog.genres.forEach { genre ->
                FilterChip(
                    selected = selectedGenreFilter == genre.id,
                    onClick = { selectedGenreFilter = genre.id },
                    label = { Text("${genre.name} (${genre.japaneseName})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = uiState.activeTheme.primaryColor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        val filteredList = if (selectedGenreFilter == "all") {
            communityCreations
        } else {
            communityCreations.filter { it.genre.equals(selectedGenreFilter, ignoreCase = true) }
        }

        // Default seeds if empty
        val displayList = if (filteredList.isEmpty()) {
            listOf(
                CommunityCreation(
                    id = "seed_1",
                    userId = "artist_1",
                    authorName = "Takeshi_Art",
                    title = "Espadachín de Fuego Azul",
                    prompt = "Guerrero shōnen desatando katana de llamas azules bajo cielo nocturno",
                    genre = "shonen",
                    style = "modern_digital",
                    mediaType = "image",
                    mediaUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop",
                    aspectRatio = "16:9",
                    likesCount = 42L
                ),
                CommunityCreation(
                    id = "seed_2",
                    userId = "artist_2",
                    authorName = "SakuraSky",
                    title = "Atardecer en Neo-Tokio",
                    prompt = "Chica cyberpunk con implantes de neón mirando la lluvia desde un tren",
                    genre = "seinen",
                    style = "cyberpunk",
                    mediaType = "image",
                    mediaUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop",
                    aspectRatio = "1:1",
                    likesCount = 89L
                ),
                CommunityCreation(
                    id = "seed_3",
                    userId = "artist_3",
                    authorName = "GhibliFan99",
                    title = "El Valle del Molino Flotante",
                    prompt = "Colinas de gouache con flores silvestres y una pequeña criatura mágica",
                    genre = "kodomo",
                    style = "ghibli",
                    mediaType = "image",
                    mediaUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop",
                    aspectRatio = "16:9",
                    likesCount = 115L
                )
            )
        } else {
            filteredList
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(displayList) { item ->
                CommunityItemCard(
                    item = item,
                    onLike = { viewModel.toggleCommunityLike(item.id) },
                    onRemix = {
                        onRemixPrompt(item.prompt, item.genre, item.style)
                    }
                )
            }
        }
    }
}

@Composable
private fun CommunityItemCard(
    item: CommunityCreation,
    onLike: () -> Unit,
    onRemix: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var likes by remember { mutableStateOf(item.likesCount) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192A)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF262D4A), RoundedCornerShape(14.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.mediaUrl.ifBlank { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop" },
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Genre Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = item.genre.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                    maxLines = 1
                )

                Text(
                    text = "Por @${item.authorName}",
                    fontSize = 10.sp,
                    color = Color(0xFF868E96)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            isLiked = !isLiked
                            likes = if (isLiked) likes + 1 else (likes - 1).coerceAtLeast(0)
                            onLike()
                        }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Votar",
                            tint = if (isLiked) Color(0xFFFF2E63) else Color(0xFFADB5BD),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$likes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Remix button
                    Button(
                        onClick = onRemix,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242A46)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFD166))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Remix", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
