package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.anime.AnimeStyleCatalog
import com.example.data.model.ConsistentCharacter
import com.example.ui.YukitsuViewModel

@Composable
fun ComfyUiConsistencySection(
    viewModel: YukitsuViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Form inputs for training new consistent character
    var charName by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("shonen") }
    var selectedStyle by remember { mutableStateOf("modern_digital") }
    var hairDesc by remember { mutableStateOf("Cabello negro azabache en puntas con mechón carmesí") }
    var eyeDesc by remember { mutableStateOf("Ojos dorados rasgados con pupila felina") }
    var outfitDesc by remember { mutableStateOf("Haori táctico con runas luminosas y bufanda blanca") }
    var marksDesc by remember { mutableStateOf("Cicatriz en la mejilla izquierda y pendiente de dragón") }

    // ComfyUI hyperparameters
    var ipAdapterWeight by remember { mutableFloatStateOf(0.88f) }
    var faceDenoise by remember { mutableFloatStateOf(0.40f) }
    var controlNetGuidance by remember { mutableFloatStateOf(0.80f) }
    var styleLock by remember { mutableFloatStateOf(0.92f) }

    var showTrainingForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131728)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF00F0FF), Color(0xFFFF2E63)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COMFYUI CHARACTER ENGINE",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color(0xFF00F0FF),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Entrenamiento de Consistencia de Personaje",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1F263E)
                    ) {
                        Text(
                            text = "IP-Adapter v2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD166),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Como en ComfyUI, entrena la identidad visual para que el rostro, peinado, fisionomía y vestuario se mantengan exactamente idénticos en cualquier escena, ángulo o acción.",
                    fontSize = 11.sp,
                    color = Color(0xFFADB5BD),
                    lineHeight = 15.sp
                )
            }
        }

        // Active Character Badge
        val activeChar = uiState.activeConsistentCharacter
        if (activeChar != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00F0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PERSONAJE ACTIVO EN ESTUDIO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                            Text(
                                text = activeChar.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Fidelidad ComfyUI: ${activeChar.consistencyScore}% • IP-Weight: ${activeChar.ipAdapterWeight}",
                                fontSize = 10.sp,
                                color = Color(0xFFFFD166)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { viewModel.applyCharacterToStudioPrompt(activeChar) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Usar en Crear", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        IconButton(
                            onClick = { viewModel.selectActiveConsistentCharacter(null) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Desactivar", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Action Buttons Row: Train new or view existing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PERSONAJES ENTRENADOS (${uiState.consistentCharacters.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD166)
            )

            Button(
                onClick = { showTrainingForm = !showTrainingForm },
                colors = ButtonDefaults.buttonColors(containerColor = if (showTrainingForm) Color(0xFF262C47) else uiState.activeTheme.primaryColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (showTrainingForm) Icons.Default.Close else Icons.Default.ModelTraining,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showTrainingForm) "Cerrar Formulario" else "Entrenar Nuevo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Interactive ComfyUI Node Training Form
        AnimatedVisibility(visible = showTrainingForm) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161A2B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D3558)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🛠️ PIPELINE DE ENTRENAMIENTO COMFYUI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00F0FF)
                    )

                    OutlinedTextField(
                        value = charName,
                        onValueChange = { charName = it },
                        label = { Text("Nombre del Personaje (ej: Ren Kusanagi, Hikari)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00F0FF),
                            unfocusedBorderColor = Color(0xFF333C61)
                        )
                    )

                    // Demographics & Genre selector
                    Text("GÉNERO / DEMOGRAFÍA BASE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFADB5BD))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnimeStyleCatalog.genres.forEach { g ->
                            val isSel = selectedGenre == g.id
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedGenre = g.id },
                                label = { Text("${g.name} (${g.japaneseName})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = uiState.activeTheme.primaryColor
                                )
                            )
                        }
                    }

                    // Anime Style
                    Text("ESTILO DE ANIMACIÓN BASE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFADB5BD))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnimeStyleCatalog.styles.take(6).forEach { st ->
                            val isSel = selectedStyle == st.id
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedStyle = st.id },
                                label = { Text(st.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00F0FF),
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    // Anatomical Features
                    Text("ANCLAJES VISUALES INMUTABLES (COMFYUI IDENTITY):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD166))

                    OutlinedTextField(
                        value = hairDesc,
                        onValueChange = { hairDesc = it },
                        label = { Text("Peinado y Color de Cabello", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = eyeDesc,
                        onValueChange = { eyeDesc = it },
                        label = { Text("Forma y Color de Ojos", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = outfitDesc,
                        onValueChange = { outfitDesc = it },
                        label = { Text("Traje y Vestuario Característico", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = marksDesc,
                        onValueChange = { marksDesc = it },
                        label = { Text("Marcas distintivas, cicatrices, accesorios", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ComfyUI Hyperparameters
                    Text("PARÁMETROS DEL NODO COMFYUI:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("IP-Adapter Face Weight (Fidelidad de Rostro)", fontSize = 11.sp, color = Color.White)
                            Text(String.format(java.util.Locale.US, "%.2f", ipAdapterWeight), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                        }
                        Slider(
                            value = ipAdapterWeight,
                            onValueChange = { ipAdapterWeight = it },
                            valueRange = 0.5f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF00F0FF), activeTrackColor = Color(0xFF00F0FF))
                        )
                    }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Face Detailer Denoise Strength", fontSize = 11.sp, color = Color.White)
                            Text(String.format(java.util.Locale.US, "%.2f", faceDenoise), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF2E63))
                        }
                        Slider(
                            value = faceDenoise,
                            onValueChange = { faceDenoise = it },
                            valueRange = 0.2f..0.7f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFF2E63), activeTrackColor = Color(0xFFFF2E63))
                        )
                    }

                    // Training Action
                    if (uiState.isTrainingCharacter) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { uiState.characterTrainingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF00F0FF),
                                trackColor = Color(0xFF1D2237)
                            )
                            Text(
                                text = uiState.lastStatusMessage,
                                fontSize = 11.sp,
                                color = Color(0xFFFFD166)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.trainConsistentCharacter(
                                    name = charName.ifBlank { "Héroe Anime Yukitsu" },
                                    genre = selectedGenre,
                                    style = selectedStyle,
                                    hair = hairDesc,
                                    eyes = eyeDesc,
                                    outfit = outfitDesc,
                                    distinctiveMarks = marksDesc,
                                    ipWeight = ipAdapterWeight,
                                    faceDenoise = faceDenoise,
                                    onComplete = {
                                        showTrainingForm = false
                                        charName = ""
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("train_character_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENTRENAR CONSISTENCIA (COMFYUI PIPELINE)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            }
        }

        // List of consistent characters
        uiState.consistentCharacters.forEach { character ->
            ConsistentCharacterCard(
                character = character,
                isActive = uiState.activeConsistentCharacter?.id == character.id,
                onSelectActive = { viewModel.selectActiveConsistentCharacter(character) },
                onUseInStudio = { viewModel.applyCharacterToStudioPrompt(character) }
            )
        }
    }
}

@Composable
private fun ConsistentCharacterCard(
    character: ConsistentCharacter,
    isActive: Boolean,
    onSelectActive: () -> Unit,
    onUseInStudio: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141829)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) Color(0xFF00F0FF) else Color(0xFF262C47)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActive) Color(0xFF00F0FF) else Color(0xFF262C47)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isActive) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = character.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = character.animeGenre.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                            Text("•", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                text = character.primaryStyle,
                                fontSize = 10.sp,
                                color = Color(0xFFADB5BD)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00F0FF).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF))
                ) {
                    Text(
                        text = "${character.consistencyScore}% Consistencia",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // ComfyUI Token Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B0E1B))
                    .border(1.dp, Color(0xFF1E2540), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOKEN DE ANCLAJE COMFYUI:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD166))
                        Text(character.identityToken, fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        text = "IP-Weight: ${character.ipAdapterWeight}",
                        fontSize = 10.sp,
                        color = Color(0xFFADB5BD)
                    )
                }
            }

            // Anatomical description
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("• Cabello: ${character.hairDescription}", fontSize = 11.sp, color = Color(0xFFE2E6EF))
                Text("• Ojos: ${character.eyeDescription}", fontSize = 11.sp, color = Color(0xFFE2E6EF))
                Text("• Traje: ${character.signatureOutfit}", fontSize = 11.sp, color = Color(0xFFE2E6EF))
                Text("• Rasgos distintivos: ${character.distinctiveFeatures}", fontSize = 11.sp, color = Color(0xFFADB5BD))
            }

            // Turnaround dataset (Vistas de giro / Dataset de consistencia)
            Text(
                text = "📸 HOJA DE GIRO / TURNAROUND DATASET (0°, 45°, 90°, ACCIÓN):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD166)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                character.turnaroundViews.forEach { angle ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0E1A)),
                        modifier = Modifier.width(110.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(angle.previewUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = angle.viewName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = angle.angleDegrees,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00F0FF),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = angle.viewName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Multi-Scene Consistency Verification
            if (character.testScenes.isNotEmpty()) {
                Text(
                    text = "🧪 PRUEBAS DE VERIFICACIÓN MULTI-ESCENA COMFYUI:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00F0FF)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    character.testScenes.forEach { scene ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0E1A)),
                            modifier = Modifier.width(140.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(scene.previewUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = scene.sceneTitle,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text(scene.sceneTitle, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(scene.environment, fontSize = 9.sp, color = Color(0xFFFFD166))
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUseInStudio,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inyectar en Estudio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Button(
                    onClick = onSelectActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) Color(0xFF222941) else Color(0xFFFF2E63)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isActive) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Activo", fontSize = 12.sp, color = Color(0xFF00F0FF))
                    } else {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Activar Rostro", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
