package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.anime.AnimeTemplate
import com.example.data.anime.AnimeTemplateCatalog
import com.example.ui.YukitsuViewModel
import java.util.Locale

@Composable
fun CreateStudioScreen(
    viewModel: YukitsuViewModel,
    onNavigateToEditorFx: () -> Unit,
    onNavigateToNotebooks: () -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val context = LocalContext.current

    var showVoiceSimulationDialog by remember { mutableStateOf(false) }
    var simulatedVoiceInput by remember { mutableStateOf("un gato ninja con espada de leon bajo lluvia") }
    var showSaveToFolderDialog by remember { mutableStateOf(false) }

    // Android SpeechRecognizer launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                viewModel.onVoiceTranscriptionReceived(spokenText)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Studio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "雪津スタジオ • CREAR ARTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = uiState.activeTheme.secondaryColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Estudio de Animación",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = uiState.activeTheme.primaryColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, uiState.activeTheme.primaryColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00F0FF))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gemini AI Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Status Snackbar / Banner
        AnimatedVisibility(visible = uiState.lastStatusMessage.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2238)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, uiState.activeTheme.secondaryColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.lastStatusMessage,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearStatusMessage() }) {
                        Text("OK", color = uiState.activeTheme.secondaryColor, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 1: Predefined Anime Templates Library
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BIBLIOTECA DE PLANTILLAS PREDEFINIDAS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9097A6),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ajustes Optimizados",
                    fontSize = 11.sp,
                    color = uiState.activeTheme.secondaryColor
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(AnimeTemplateCatalog.templates) { template ->
                    AnimeTemplateCard(
                        template = template,
                        onSelect = {
                            viewModel.setPrompt(template.samplePrompt)
                            viewModel.selectGenre(template.demographic.lowercase())
                            viewModel.setAspectRatio(template.defaultAspectRatio)
                            viewModel.setImageQuality(template.defaultImageQuality)
                            viewModel.enhancePrompt()
                        }
                    )
                }
            }
        }

        // Section 2: Natural Language Prompt Input with Voice Context Repair
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = uiState.activeTheme.cardBackgroundColor),
            modifier = Modifier
                .fillMaxWidth()
                .border(uiState.activeTheme.cardBorderWidth, uiState.activeTheme.cardBorderColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                    Text(
                        text = "TU IDEA O DESCRIPCIÓN SIMPLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFADB5BD)
                    )

                    // Microphone Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF22263D),
                            modifier = Modifier.clickable {
                                // Launch Voice Intent or Fallback Dialog
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla tu idea de anime...")
                                }
                                try {
                                    speechRecognizerLauncher.launch(intent)
                                } catch (_: Exception) {
                                    showVoiceSimulationDialog = true
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Dictar por voz con IA",
                                    tint = uiState.activeTheme.primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Micrófono IA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = { viewModel.setPrompt(it) },
                    placeholder = {
                        Text(
                            "Ejemplo: un gato ninja con espada de neón bajo lluvia, o una guerrera mágica en el espacio...",
                            color = Color(0xFF6C757D),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prompt_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = uiState.activeTheme.primaryColor,
                        unfocusedBorderColor = Color(0xFF2E334D),
                        focusedContainerColor = Color(0xFF0F111E),
                        unfocusedContainerColor = Color(0xFF0F111E)
                    ),
                    minLines = 2,
                    maxLines = 4
                )

                // Otaku AI Prompt Enhancer Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.enhancePrompt() },
                        enabled = !uiState.isEnhancing && uiState.prompt.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1D38)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("enhance_prompt_button")
                    ) {
                        if (uiState.isEnhancing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = uiState.activeTheme.secondaryColor,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Interpretando...", fontSize = 12.sp, color = Color.White)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Optimizar con Director IA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (uiState.enhancedPrompt.isNotBlank()) {
                        Text(
                            text = "✓ Optimizado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06D6A0)
                        )
                    }
                }

                if (uiState.enhancedPrompt.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131728))
                            .border(1.dp, Color(0xFF2E3557), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "PROMPT ENRIQUECIDO DE DIRECTOR:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = uiState.activeTheme.secondaryColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.enhancedPrompt,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E6EF),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 2.5: ComfyUI Character Consistency Selector Bar
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.activeConsistentCharacter != null) Color(0xFF0E2228) else Color(0xFF141728)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (uiState.activeConsistentCharacter != null) Color(0xFF00F0FF) else Color(0xFF282E49)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = if (uiState.activeConsistentCharacter != null) Color(0xFF00F0FF) else Color(0xFFFFD166),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CONSISTENCIA DE PERSONAJE (COMFYUI)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.activeConsistentCharacter != null) Color(0xFF00F0FF) else Color(0xFFFFD166)
                        )
                    }

                    TextButton(
                        onClick = onNavigateToNotebooks,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Lab ComfyUI ➜", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = uiState.activeConsistentCharacter == null,
                        onClick = { viewModel.selectActiveConsistentCharacter(null) },
                        label = { Text("Sin personaje fijado", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2E3557)
                        )
                    )

                    uiState.consistentCharacters.forEach { char ->
                        val isSelected = uiState.activeConsistentCharacter?.id == char.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.selectActiveConsistentCharacter(null)
                                } else {
                                    viewModel.applyCharacterToStudioPrompt(char)
                                }
                            },
                            label = {
                                Text(
                                    text = "${char.name} (${char.consistencyScore}%)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00F0FF),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                if (uiState.activeConsistentCharacter != null) {
                    val activeChar = uiState.activeConsistentCharacter!!
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF08181D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val refImg = activeChar.referenceImageUrl ?: activeChar.referenceImageUri ?: activeChar.turnaroundViews.firstOrNull()?.previewUrl
                            AsyncImage(
                                model = refImg,
                                contentDescription = "Ref de ${activeChar.name}",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFF00F0FF), CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Rastreo: ${activeChar.name}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${activeChar.trackingMode})",
                                        fontSize = 9.sp,
                                        color = Color(0xFF00F0FF)
                                    )
                                }
                                Text(
                                    text = "IP-Adapter: ${"%.2f".format(activeChar.ipAdapterWeight)} • Seed: ${activeChar.fixedSeed ?: "Auto"}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFADB5BD)
                                )
                            }

                            Switch(
                                checked = uiState.isCharacterTrackingActive,
                                onCheckedChange = { viewModel.toggleCharacterTracking(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF00F0FF)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Genre / Demographic Filter Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "DEMOGRAFÍA / GÉNERO ANIME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9097A6),
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimeStyleCatalog.genres.forEach { genre ->
                    val isSelected = uiState.selectedGenreId == genre.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectGenre(genre.id) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(genre.name, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(genre.japaneseName, fontSize = 10.sp, color = if (isSelected) Color.White else Color(0xFFADB5BD))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = uiState.activeTheme.primaryColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF161A2B),
                            labelColor = Color(0xFFCBD2E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFF282E49),
                            selectedBorderColor = uiState.activeTheme.primaryColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Section 4: Aesthetic Era & Studio Style Carousel
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ESTILOS VISUALES & ÉPOCAS DE ANIMACIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9097A6),
                letterSpacing = 1.sp
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(AnimeStyleCatalog.styles) { style ->
                    val isSelected = uiState.selectedStyleId == style.id
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF2A1C30) else Color(0xFF161929)
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) uiState.activeTheme.primaryColor else Color(0xFF252A42),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectStyle(style.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = style.era,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) uiState.activeTheme.secondaryColor else Color(0xFF868E96)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = uiState.activeTheme.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = style.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = style.japaneseName,
                                fontSize = 10.sp,
                                color = Color(0xFF00F0FF)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = style.description,
                                fontSize = 11.sp,
                                color = Color(0xFFADB5BD),
                                maxLines = 2,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Format & Aspect Ratio Controls
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131626)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Media Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val mediaOptions = listOf(
                        Triple("image", "Imagen", Icons.Default.Palette),
                        Triple("video", "Video (Veo 3)", Icons.Default.Movie),
                        Triple("gif", "GIF Animado", Icons.Default.PlayArrow)
                    )
                    mediaOptions.forEach { (type, label, icon) ->
                        val isSelected = uiState.mediaType == type
                        Button(
                            onClick = { viewModel.setMediaType(type) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) uiState.activeTheme.primaryColor else Color(0xFF1E2338)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Video Duration & Frame-by-Frame Studio Mode
                if (uiState.mediaType == "video") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF191D30), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DURACIÓN DEL VIDEO (Máx 30 seg)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                            Text(
                                text = "Veo 3.1 Fast",
                                fontSize = 10.sp,
                                color = Color(0xFFADB5BD)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("5s", "10s", "15s", "30s (Máx)").forEach { sec ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (sec == "30s (Máx)") uiState.activeTheme.primaryColor else Color(0xFF22273F),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = sec,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Frame-by-Frame Storyboard Short Film Mode
                        Text(
                            text = "🎬 CREADOR DE CORTO ANIME POR FRAMES (1 - 2 MIN):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD166)
                        )
                        Text(
                            text = "Crea un mini-episodio o corto anime secuenciando frames con frases y subtítulos en japonés:",
                            fontSize = 11.sp,
                            color = Color(0xFFB0B7C6)
                        )

                        // Storyboard Scenes Preview
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val storyboardSamples = listOf(
                                Triple("Escena 1: Establecimiento (6s)", "「静寂が破られる時が来た…」", "Ha llegado la hora del destino..."),
                                Triple("Escena 2: Diálogo de Combate (8s)", "「貴様の覚悟を、ここで見せてもらおう！」", "¡Demuéstrame aquí tu determinación!"),
                                Triple("Escena 3: Clímax de Ataque (10s)", "「これが私の、全力の刃だーーっ！」", "¡Esta es mi espada con todo mi poder!"),
                                Triple("Escena 4: Cierre con Crédito (6s)", "「戦いは終わり、風が吹き抜ける。」", "La batalla ha terminado...")
                            )

                            storyboardSamples.forEachIndexed { index, (sceneTitle, jp, es) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF131626),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(uiState.activeTheme.primaryColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(sceneTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                            Text(jp, fontSize = 10.sp, color = Color(0xFF00F0FF))
                                            Text(es, fontSize = 9.sp, color = Color(0xFF9AA0A6))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Aspect Ratio Selector

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "RELACIÓN DE ASPECTO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF868E96)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val ratios = listOf("1:1", "16:9", "9:16", "4:3", "3:4", "2:3", "3:2", "21:9")
                        ratios.forEach { ratio ->
                            val isSelected = uiState.aspectRatio == ratio
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) uiState.activeTheme.secondaryColor else Color(0xFF1D2237),
                                modifier = Modifier.clickable { viewModel.setAspectRatio(ratio) }
                            ) {
                                Text(
                                    text = ratio,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Quality Selector (1K, 2K, 4K)
                if (uiState.mediaType == "image") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RESOLUCIÓN (Gemini Pro)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF868E96)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1K", "2K", "4K").forEach { q ->
                                val isSelected = uiState.imageQuality == q
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFFFFD166) else Color(0xFF1D2237),
                                    modifier = Modifier.clickable { viewModel.setImageQuality(q) }
                                ) {
                                    Text(
                                        text = q,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 6: Primary Generation Button
        Button(
            onClick = { viewModel.generateMedia() },
            enabled = !uiState.isGenerating,
            colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("generate_anime_button")
        ) {
            if (uiState.isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Generando arte anime...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GENERAR ARTE ANIME",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Section 7: Result Showcase Card with Live CSS Style Applied
        if (uiState.lastGeneratedMediaUrl.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(uiState.activeTheme.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = uiState.activeTheme.cardBackgroundColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = uiState.activeTheme.cardBorderWidth,
                        color = uiState.activeTheme.cardBorderColor,
                        shape = RoundedCornerShape(uiState.activeTheme.cardCornerRadius)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RESULTADO GENERADO (Estilo CSS en Vivo)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = uiState.activeTheme.secondaryColor
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Render Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uiState.lastGeneratedMediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Arte anime generado",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showSaveToFolderDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C47)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guardar", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onNavigateToEditorFx() },
                            colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editor FX", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onNavigateToCommunity() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Comunidad", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Voice Simulation / Fallback Dialog
    if (showVoiceSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceSimulationDialog = false },
            title = { Text("Micrófono con Memoria de Contexto") },
            text = {
                Column {
                    Text(
                        "Ingresa texto simulando palabras habladas por voz (incluso con palabras que la IA pueda escuchar confusas). Gemini deducirá el contexto anime automáticamente:",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = simulatedVoiceInput,
                        onValueChange = { simulatedVoiceInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVoiceSimulationDialog = false
                        viewModel.onVoiceTranscriptionReceived(simulatedVoiceInput)
                    }
                ) {
                    Text("Interpretar con IA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceSimulationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Save to Folder / Cuaderno Dialog
    if (showSaveToFolderDialog) {
        AlertDialog(
            onDismissRequest = { showSaveToFolderDialog = false },
            title = { Text("Guardar en Cuaderno") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona el cuaderno o álbum donde deseas organizar esta creación:", fontSize = 13.sp)
                    if (folders.isEmpty()) {
                        Text("No tienes cuadernos creados aún. Se guardará en la carpeta por defecto.", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        folders.forEach { f ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (uiState.activeCuadernoId == f.id) uiState.activeTheme.primaryColor.copy(alpha = 0.2f) else Color(0xFF1E2338),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setActiveCuaderno(f.id) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(f.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveToFolderDialog = false
                        viewModel.saveCreation(uiState.activeCuadernoId)
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveToFolderDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AnimeTemplateCard(
    template: AnimeTemplate,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151929)),
        modifier = Modifier
            .width(220.dp)
            .border(1.dp, Color(0xFF232840), RoundedCornerShape(14.dp))
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(android.graphics.Color.parseColor(template.badgeColor)).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = template.demographic,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(template.badgeColor)),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${template.defaultAspectRatio} • ${template.defaultImageQuality}",
                    fontSize = 10.sp,
                    color = Color(0xFF868E96)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = template.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = template.japaneseName,
                fontSize = 10.sp,
                color = Color(0xFF00F0FF)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = template.description,
                fontSize = 11.sp,
                color = Color(0xFFADB5BD),
                maxLines = 2,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FX: ${template.recommendedEditorFx}",
                    fontSize = 10.sp,
                    color = Color(0xFFFFD166)
                )
                Text(
                    text = "Cargar plantilla →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00F0FF)
                )
            }
        }
    }
}
