package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ConsistentCharacter
import com.example.ui.YukitsuViewModel

/**
 * Character-Tracking Studio Module
 * 
 * Allows users to define a character's reference image and maintain its consistency
 * across multiple generations by implementing a character-tracking prompt modifier.
 */
@Composable
fun CharacterTrackingStudioModule(
    viewModel: YukitsuViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val activeChar = uiState.activeConsistentCharacter ?: uiState.consistentCharacters.firstOrNull()

    // Photo Picker launcher for zero-permission reference image selection
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && activeChar != null) {
            viewModel.setCharacterReferenceImage(activeChar.id, uri.toString(), isLocalUri = true)
            Toast.makeText(context, "Imagen de referencia local fijada", Toast.LENGTH_SHORT).show()
        }
    }

    var customUrlInput by remember { mutableStateOf("") }
    var showUrlDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E18))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF00F0FF), Color(0xFF7B2CBF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MÓDULO DE RASTREO DE PERSONAJE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00F0FF),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Reference-Image Character Tracking & Prompt Modifier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD6DBE7)
                        )
                    }
                }
            }
        }

        // Active Character Selector Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECCIONA EL PERSONAJE A RASTREAR:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFADB5BD)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.consistentCharacters) { char ->
                        val isSelected = activeChar?.id == char.id
                        Card(
                            onClick = {
                                viewModel.selectActiveConsistentCharacter(char)
                                viewModel.refreshTrackingPreview()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF002B36) else Color(0xFF161A2D)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF00F0FF) else Color(0xFF262C44)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val thumb = char.referenceImageUrl ?: char.referenceImageUri ?: char.turnaroundViews.firstOrNull()?.previewUrl
                                AsyncImage(
                                    model = thumb,
                                    contentDescription = char.name,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = char.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF00F0FF) else Color.White
                                    )
                                    Text(
                                        text = "${char.consistencyScore}% Consistencia",
                                        fontSize = 10.sp,
                                        color = Color(0xFF06D6A0)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (activeChar != null) {
            // Reference Image Definition Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14182B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3557)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8A00),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "IMAGEN DE REFERENCIA DEL PERSONAJE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1F243B)
                            ) {
                                Text(
                                    text = "IP-Adapter Bound",
                                    fontSize = 10.sp,
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        val currentRef = activeChar.referenceImageUrl ?: activeChar.referenceImageUri
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Reference Image Preview Box
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0A0C16))
                                    .border(1.5.dp, Color(0xFF00F0FF), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!currentRef.isNullOrBlank()) {
                                    AsyncImage(
                                        model = currentRef,
                                        contentDescription = "Imagen de Referencia de ${activeChar.name}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = Color(0xFF6C757D),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Sin imagen",
                                            fontSize = 10.sp,
                                            color = Color(0xFF6C757D)
                                        )
                                    }
                                }
                            }

                            // Actions to define or replace reference image
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Esta imagen fijará el rostro, paleta y proporción del personaje en todas tus generaciones.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFADB5BD),
                                    lineHeight = 15.sp
                                )

                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("pick_reference_image_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Seleccionar de Galería", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                if (activeChar.turnaroundViews.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = {
                                            val firstTurnaround = activeChar.turnaroundViews.first().previewUrl
                                            viewModel.setCharacterReferenceImage(activeChar.id, firstTurnaround, isLocalUri = false)
                                            Toast.makeText(context, "Asignada vista frontal como referencia", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Usar Turnaround Frontal", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tracking Engine Controls & Mode Selector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14182B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3557)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RASTREO ACTIVO EN EL PROMPT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Aplica el modificador de consistencia a cada generación",
                                    fontSize = 10.sp,
                                    color = Color(0xFF9097A6)
                                )
                            }
                            Switch(
                                checked = uiState.isCharacterTrackingActive,
                                onCheckedChange = { viewModel.toggleCharacterTracking(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF00F0FF)
                                ),
                                modifier = Modifier.testTag("character_tracking_switch")
                            )
                        }

                        // Tracking Mode Selector
                        Text(
                            text = "MODO DEL MODIFICADOR:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFADB5BD)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val modes = listOf(
                                Triple("AUTO_ADAPTIVE", "Auto Adaptativo", "Equilibrio entre rostro fijo y adaptación a la escena"),
                                Triple("STRICT_LOCK", "Estricto (Lock)", "Bloqueo rígido de rostro, peinado y atuendo"),
                                Triple("CREATIVE_FUSION", "Fusión Creativa", "Preserva rasgos faciales clave en universos alternos")
                            )

                            modes.forEach { (modeKey, modeTitle, _) ->
                                val isSelected = activeChar.trackingMode == modeKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setCharacterTrackingMode(activeChar.id, modeKey) },
                                    label = { Text(modeTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF003847),
                                        selectedLabelColor = Color(0xFF00F0FF)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Sliders: IP-Adapter Weight & Style Lock Strength
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ponderación de Imagen (IP-Adapter Weight)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD0D6E2)
                                )
                                Text(
                                    text = "${"%.2f".format(activeChar.ipAdapterWeight)}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00F0FF)
                                )
                            }
                            Slider(
                                value = activeChar.ipAdapterWeight,
                                onValueChange = { newWeight ->
                                    viewModel.updateCharacterTrackingHyperparams(
                                        characterId = activeChar.id,
                                        ipWeight = newWeight,
                                        styleLock = activeChar.styleLockStrength,
                                        seed = activeChar.fixedSeed
                                    )
                                },
                                valueRange = 0.5f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00F0FF),
                                    activeTrackColor = Color(0xFF00F0FF)
                                )
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Bloqueo de Estilo y Semilla Fija",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD0D6E2)
                                )
                                Text(
                                    text = "Seed: ${activeChar.fixedSeed ?: "Aleatoria"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD166)
                                )
                            }
                            Slider(
                                value = activeChar.styleLockStrength,
                                onValueChange = { newStrength ->
                                    viewModel.updateCharacterTrackingHyperparams(
                                        characterId = activeChar.id,
                                        ipWeight = activeChar.ipAdapterWeight,
                                        styleLock = newStrength,
                                        seed = activeChar.fixedSeed
                                    )
                                },
                                valueRange = 0.5f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFD166),
                                    activeTrackColor = Color(0xFFFFD166)
                                )
                            )
                        }
                    }
                }
            }

            // Live Character-Tracking Prompt Modifier Inspector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101424)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SIMULADOR DEL MODIFICADOR EN VIVO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "Turno #${uiState.characterTrackingTurn}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06D6A0)
                            )
                        }

                        // Input for Scene Prompt
                        OutlinedTextField(
                            value = uiState.prompt,
                            onValueChange = {
                                viewModel.setPrompt(it)
                                viewModel.refreshTrackingPreview()
                            },
                            placeholder = {
                                Text(
                                    "Escribe una escena: ej. comiendo ramen bajo lluvia o luchando en tejado...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6C757D)
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("tracking_prompt_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF262C44),
                                focusedContainerColor = Color(0xFF090A14),
                                unfocusedContainerColor = Color(0xFF090A14)
                            ),
                            maxLines = 2
                        )

                        // Realtime Result Card
                        val trackingResult = uiState.characterTrackingResult
                        if (trackingResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070912))
                                    .border(1.dp, Color(0xFF1D233D), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "PROMPT MODIFICADO RESULTANTE:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00F0FF)
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(trackingResult.fullModifiedPrompt))
                                                Toast.makeText(context, "Prompt copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copiar Prompt",
                                                tint = Color(0xFFADB5BD),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = trackingResult.fullModifiedPrompt,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE2E6EF),
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "PROMPT NEGATIVO ANTI-DERIVA:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF6B6B)
                                    )
                                    Text(
                                        text = trackingResult.negativePrompt,
                                        fontSize = 10.sp,
                                        color = Color(0xFFADB5BD),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // Generate with Consistent Character Button
                        Button(
                            onClick = {
                                viewModel.generateMedia()
                                Toast.makeText(context, "Generando con consistencia de ${activeChar.name}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_with_tracking_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF0C0E18),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GENERAR CON RASTREO PERSISTENTE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0C0E18)
                            )
                        }
                    }
                }
            }

            // Tracking Generations History (Multi-Generation Verification)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Color(0xFF06D6A0),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HISTORIAL DE GENERACIONES RASTREADAS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${activeChar.trackingGenerations.size} Registros",
                            fontSize = 11.sp,
                            color = Color(0xFF9097A6)
                        )
                    }

                    if (activeChar.trackingGenerations.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141728)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Aún no hay generaciones registradas con este personaje. Escribe un prompt arriba y pulsa 'Generar con Rastreo Persistente'.",
                                fontSize = 12.sp,
                                color = Color(0xFF8C95A6),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeChar.trackingGenerations.forEach { entry ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121526)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222842)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = entry.previewUrl,
                                            contentDescription = "Generación Turno ${entry.turnNumber}",
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFF0A2E28)
                                                ) {
                                                    Text(
                                                        text = "Turno #${entry.turnNumber}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF06D6A0),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = Color(0xFF06D6A0),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "Identidad OK",
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF06D6A0),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = entry.rawPrompt.ifBlank { "Escena anime consistente" },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
