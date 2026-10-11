package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.anime.AnimeStyleCatalog
import com.example.data.model.AnimeLora
import com.example.data.model.Creation
import com.example.data.model.Folder
import com.example.ui.YukitsuViewModel

@Composable
fun NotebooksScreen(
    viewModel: YukitsuViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val userCreations by viewModel.userCreations.collectAsState()
    val context = LocalContext.current

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0=Carpetas & Obras, 1=Entrenar LoRA, 2=Remix & Memoria
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var newFolderDesc by remember { mutableStateOf("") }
    var newFolderColor by remember { mutableStateOf("#FF2E63") }

    // LoRA Studio local state
    var loraName by remember { mutableStateOf("Yukitsu-NeoGhibli") }
    var loraTriggerWord by remember { mutableStateOf("<lora:yukitsu_neoghibli:0.8>") }
    var loraStyle1 by remember { mutableStateOf("ghibli") }
    var loraStyle2 by remember { mutableStateOf("cyberpunk") }
    var loraWeight1 by remember { mutableFloatStateOf(0.6f) }
    var loraImageCount by remember { mutableIntStateOf(24) }
    var isTrainingLora by remember { mutableStateOf(false) }
    var loraTrainingProgress by remember { mutableFloatStateOf(0f) }
    var savedLoras by remember {
        mutableStateOf(
            listOf(
                AnimeLora(
                    id = "l1",
                    name = "Shōnen Dragon Ki",
                    triggerWord = "<lora:dragon_ki:0.85>",
                    primaryStyle = "shonen",
                    secondaryStyle = "retro_90s",
                    styleWeights = mapOf("shonen" to 0.7f, "retro_90s" to 0.3f),
                    sampleImagesCount = 30,
                    description = "Fusión de aura de combate clásica 90s con iluminación digital"
                ),
                AnimeLora(
                    id = "l2",
                    name = "Cyberpunk Ghibli",
                    triggerWord = "<lora:cyber_ghibli:0.75>",
                    primaryStyle = "ghibli",
                    secondaryStyle = "cyberpunk",
                    styleWeights = mapOf("ghibli" to 0.55f, "cyberpunk" to 0.45f),
                    sampleImagesCount = 25,
                    description = "Naturaleza mística pintada a gouache con rascacielos y neones"
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "雪津スタジオ • ORGANIZACIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = uiState.activeTheme.secondaryColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Cuadernos & LoRA Lab",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            IconButton(
                onClick = { showCreateFolderDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(uiState.activeTheme.primaryColor)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Cuaderno", tint = Color.White)
            }
        }

        // Sub Tabs Row
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFF131626),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = uiState.activeTheme.primaryColor
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Mis Cuadernos", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Laboratorio LoRA", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ModelTraining, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Remix & Memoria", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedSubTab == 3,
                onClick = { selectedSubTab = 3 },
                text = { Text("ComfyUI Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedSubTab == 4,
                onClick = { selectedSubTab = 4 },
                text = { Text("Rastreo Ref-Img", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        when (selectedSubTab) {
            0 -> {
                // Tab 0: Cuadernos & Creations Explorer
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Folders Horizontal List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (uiState.activeCuadernoId.isBlank()) uiState.activeTheme.primaryColor else Color(0xFF1D2236),
                            modifier = Modifier.clickable { viewModel.setActiveCuaderno("") }
                        ) {
                            Text(
                                text = "Todas (${userCreations.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }

                        folders.forEach { f ->
                            val isSelected = uiState.activeCuadernoId == f.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) uiState.activeTheme.primaryColor else Color(0xFF1D2236),
                                modifier = Modifier.clickable { viewModel.setActiveCuaderno(f.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${f.name} (${userCreations.count { it.folderId == f.id }})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    val filteredCreations = if (uiState.activeCuadernoId.isBlank()) {
                        userCreations
                    } else {
                        userCreations.filter { it.folderId == uiState.activeCuadernoId }
                    }

                    if (filteredCreations.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131726)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF6C757D), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No hay creaciones en este cuaderno", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Genera arte en la pestaña Crear y guárdalo aquí.", fontSize = 12.sp, color = Color(0xFF868E96))
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filteredCreations.forEach { c ->
                                CreationItemCard(
                                    creation = c,
                                    onDelete = { viewModel.deleteCreation(c.id, c.folderId) },
                                    onFavorite = { viewModel.toggleFavorite(c) }
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Laboratorio LoRA (Entrenamiento y Fusión de Estilos)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF2A3150), RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "ENTRENAR NUEVO LORA ANIME (Low-Rank Adaptation)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = uiState.activeTheme.secondaryColor
                            )
                            Text(
                                text = "Crea un modelo LoRA a medida combinando varios estilos y definiendo los pesos de influencia visual.",
                                fontSize = 12.sp,
                                color = Color(0xFFADB5BD)
                            )

                            OutlinedTextField(
                                value = loraName,
                                onValueChange = { loraName = it },
                                label = { Text("Nombre del LoRA") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = uiState.activeTheme.secondaryColor
                                )
                            )

                            OutlinedTextField(
                                value = loraTriggerWord,
                                onValueChange = { loraTriggerWord = it },
                                label = { Text("Palabra Disparadora (Trigger Word)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = uiState.activeTheme.secondaryColor
                                )
                            )

                            // Style 1 & Style 2 Weights
                            Text("Fusión de estilos para el LoRA:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estilo 1: $loraStyle1 (${(loraWeight1 * 100).toInt()}%)", fontSize = 11.sp, color = uiState.activeTheme.primaryColor)
                                Text("Estilo 2: $loraStyle2 (${((1f - loraWeight1) * 100).toInt()}%)", fontSize = 11.sp, color = uiState.activeTheme.secondaryColor)
                            }
                            Slider(
                                value = loraWeight1,
                                onValueChange = { loraWeight1 = it },
                                valueRange = 0.1f..0.9f,
                                colors = SliderDefaults.colors(thumbColor = uiState.activeTheme.primaryColor)
                            )

                            // Image count slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Imágenes de entrenamiento / dataset:", fontSize = 11.sp, color = Color(0xFFADB5BD))
                                Text("$loraImageCount imágenes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Slider(
                                value = loraImageCount.toFloat(),
                                onValueChange = { loraImageCount = it.toInt() },
                                valueRange = 10f..60f,
                                steps = 5
                            )

                            if (isTrainingLora) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearProgressIndicator(
                                        progress = { loraTrainingProgress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = uiState.activeTheme.secondaryColor
                                    )
                                    Text(
                                        text = "Entrenando LoRA con pesos de estilo: ${(loraTrainingProgress * 100).toInt()}%...",
                                        fontSize = 11.sp,
                                        color = Color(0xFF00F0FF)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isTrainingLora = true
                                    loraTrainingProgress = 0.2f
                                    // Complete training simulation
                                    val newLora = AnimeLora(
                                        id = "lora_${System.currentTimeMillis()}",
                                        name = loraName,
                                        triggerWord = loraTriggerWord,
                                        primaryStyle = loraStyle1,
                                        secondaryStyle = loraStyle2,
                                        styleWeights = mapOf(loraStyle1 to loraWeight1, loraStyle2 to (1f - loraWeight1)),
                                        sampleImagesCount = loraImageCount,
                                        description = "Mezcla de $loraStyle1 y $loraStyle2 con trigger $loraTriggerWord"
                                    )
                                    savedLoras = savedLoras + newLora
                                    isTrainingLora = false
                                    loraTrainingProgress = 1.0f
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ModelTraining, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("INICIAR ENTRENAMIENTO DE LORA", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Saved LoRAs library
                    Text(
                        text = "TUS LORAS ENTRENADOS (${savedLoras.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF868E96)
                    )

                    savedLoras.forEach { lora ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171B2D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(lora.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF06D6A0).copy(alpha = 0.2f)
                                    ) {
                                        Text("Listo", color = Color(0xFF06D6A0), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Trigger: ${lora.triggerWord}", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                                Text(lora.description, fontSize = 11.sp, color = Color(0xFF9097A6))
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.setPrompt("${uiState.prompt} ${lora.triggerWord}")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF272F4C)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Insertar en prompt actual", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Remix & Memoria Multihilo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141829))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "REMIX DE ESTILOS & FUENTES DE AYUDA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = uiState.activeTheme.secondaryColor
                            )
                            Text(
                                text = "Como en los cuadernos de IA avanzados, fusiona dos estilos y añade fuentes de ayuda visual o referencias para el modelo:",
                                fontSize = 12.sp,
                                color = Color(0xFF9097A6)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.remixPrimaryStyleId,
                                    onValueChange = { viewModel.setRemixStyles(it, uiState.remixSecondaryStyleId) },
                                    label = { Text("Estilo 1 (Ej: ghibli)", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = uiState.remixSecondaryStyleId,
                                    onValueChange = { viewModel.setRemixStyles(uiState.remixPrimaryStyleId, it) },
                                    label = { Text("Estilo 2 (Ej: cyberpunk)", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = uiState.remixVisualNotes,
                                onValueChange = { viewModel.setRemixVisualNotes(it) },
                                label = { Text("Fuentes de ayuda / Notas del Cuaderno") },
                                placeholder = { Text("Ej: Paleta cálida, fondo de molinos con cables de fibra óptica...", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Button(
                                onClick = { viewModel.executeStyleRemix() },
                                colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (uiState.isRemixing) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("FUSIONAR ESTILOS CON IA", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (uiState.remixedPromptResult.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F121F))
                                        .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(uiState.remixedPromptResult, fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    // Thread Memory Review
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192A))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("MEMORIA MULTIHILO ACTIVA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFD166))
                            Text(
                                "La IA recuerda los pasos previos para mantener coherencia de personajes y trazo en este hilo:",
                                fontSize = 11.sp,
                                color = Color(0xFF9097A6)
                            )

                            if (uiState.threadHistory.isEmpty()) {
                                Text("Aún no hay historial en este hilo de creación.", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                uiState.threadHistory.forEachIndexed { idx, turn ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1D2237),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${idx + 1}. $turn",
                                            fontSize = 11.sp,
                                            color = Color(0xFFE2E6EF),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                ComfyUiConsistencySection(viewModel = viewModel)
            }
            4 -> {
                CharacterTrackingStudioModule(viewModel = viewModel)
            }
        }
    }

    // Dialog Create Folder
    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("Nuevo Cuaderno de Anime") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Nombre del cuaderno (ej: Shonen 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFolderDesc,
                        onValueChange = { newFolderDesc = it },
                        label = { Text("Descripción o notas de referencia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCreateFolderDialog = false
                        viewModel.createFolder(newFolderName, newFolderColor, "book", newFolderDesc)
                        newFolderName = ""
                        newFolderDesc = ""
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CreationItemCard(
    creation: Creation,
    onDelete: () -> Unit,
    onFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A2B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val img = creation.mediaUrl.ifBlank {
                    "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop"
                }
                AsyncImage(
                    model = img,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(creation.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text(
                    text = "${creation.genre.uppercase()} • ${creation.style} • ${creation.mediaType}",
                    fontSize = 10.sp,
                    color = Color(0xFF00F0FF)
                )
                Text(
                    text = creation.prompt,
                    fontSize = 11.sp,
                    color = Color(0xFFADB5BD),
                    maxLines = 1
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFADB5BD), modifier = Modifier.size(18.dp))
            }
        }
    }
}
