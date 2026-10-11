package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.YukitsuViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EditorFxScreen(
    viewModel: YukitsuViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedOverlayFx by remember { mutableStateOf("speedlines") } // speedlines, halftone, aura, scanlines, sparkles, none
    var fxIntensity by remember { mutableFloatStateOf(0.7f) }
    var subtitleJapanese by remember { mutableStateOf("「お前はもう死んでいる…！」") }
    var subtitleSpanish by remember { mutableStateOf("¡Tu técnica ya no tiene efecto sobre mí!") }
    var showSubtitles by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "雪津スタジオ • EDICIÓN FX",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = uiState.activeTheme.secondaryColor,
                letterSpacing = 1.sp
            )
            Text(
                text = "Estudio de Post-Procesado",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Añade efectos visuales propios de la animación japonesa: líneas de velocidad, tramas manga, subtítulos bilingües y filtros analógicos.",
                fontSize = 12.sp,
                color = Color(0xFF9097A6)
            )
        }

        // Live Canvas Interactive Preview
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
            Column(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val imageUrl = uiState.lastGeneratedMediaUrl.ifBlank {
                        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop"
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Vista previa de edición",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay FX Canvas Rendering
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

                        when (selectedOverlayFx) {
                            "speedlines" -> {
                                val rayCount = (40 * fxIntensity).toInt().coerceAtLeast(10)
                                val innerRadius = canvasWidth * 0.25f
                                val outerRadius = canvasWidth * 0.75f

                                for (i in 0 until rayCount) {
                                    val angle = (i.toFloat() / rayCount) * (2 * Math.PI.toFloat())
                                    val startX = center.x + cos(angle) * (innerRadius + (i % 5) * 15)
                                    val startY = center.y + sin(angle) * (innerRadius + (i % 5) * 15)
                                    val endX = center.x + cos(angle) * outerRadius
                                    val endY = center.y + sin(angle) * outerRadius

                                    drawLine(
                                        color = Color.White.copy(alpha = 0.6f * fxIntensity),
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = (2f + (i % 3) * 2f)
                                    )
                                }
                            }
                            "halftone" -> {
                                val spacing = 20f / fxIntensity.coerceAtLeast(0.2f)
                                var x = 0f
                                while (x < canvasWidth) {
                                    var y = 0f
                                    while (y < canvasHeight) {
                                        drawCircle(
                                            color = Color.Black.copy(alpha = 0.4f * fxIntensity),
                                            radius = 3.5f * fxIntensity,
                                            center = Offset(x, y)
                                        )
                                        y += spacing
                                    }
                                    x += spacing
                                }
                            }
                            "aura" -> {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFFFF2E63).copy(alpha = 0.2f * fxIntensity),
                                            Color(0xFFFF9E00).copy(alpha = 0.5f * fxIntensity)
                                        ),
                                        center = center,
                                        radius = canvasWidth * 0.6f
                                    )
                                )
                            }
                            "scanlines" -> {
                                var y = 0f
                                while (y < canvasHeight) {
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.5f * fxIntensity),
                                        start = Offset(0f, y),
                                        end = Offset(canvasWidth, y),
                                        strokeWidth = 2f
                                    )
                                    y += 6f
                                }
                            }
                            "sparkles" -> {
                                for (i in 0..25) {
                                    val px = (i * 73f) % canvasWidth
                                    val py = (i * 97f) % canvasHeight
                                    drawCircle(
                                        color = Color(0xFFFFD166).copy(alpha = 0.8f * fxIntensity),
                                        radius = ((i % 4) + 2f) * fxIntensity,
                                        center = Offset(px, py)
                                    )
                                }
                            }
                        }
                    }

                    // Anime Subtitle Overlay
                    if (showSubtitles) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = subtitleJapanese,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = subtitleSpanish,
                                color = Color(0xFFFFEA00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // FX Selection Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SELECCIONAR EFECTO DE ANIMACIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9097A6)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val fxList = listOf(
                    "speedlines" to "Líneas Shōnen",
                    "halftone" to "Trama Manga",
                    "aura" to "Aura Ki",
                    "scanlines" to "CRT 90s",
                    "sparkles" to "Destellos"
                )

                fxList.forEach { (id, label) ->
                    val isSelected = selectedOverlayFx == id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) uiState.activeTheme.primaryColor else Color(0xFF1B2033),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedOverlayFx = if (isSelected) "none" else id }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Intensity Slider
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Intensidad del efecto", fontSize = 12.sp, color = Color.White)
                Text("${(fxIntensity * 100).toInt()}%", fontSize = 12.sp, color = uiState.activeTheme.secondaryColor)
            }
            Slider(
                value = fxIntensity,
                onValueChange = { fxIntensity = it },
                valueRange = 0.1f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = uiState.activeTheme.primaryColor,
                    activeTrackColor = uiState.activeTheme.primaryColor
                )
            )
        }

        // Subtitle Editor
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141726))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUBTÍTULOS ESTILO ANIME JAPONÉS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD166)
                    )
                    FilterChip(
                        selected = showSubtitles,
                        onClick = { showSubtitles = !showSubtitles },
                        label = { Text(if (showSubtitles) "Activado" else "Oculto", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = subtitleJapanese,
                    onValueChange = { subtitleJapanese = it },
                    label = { Text("Línea en Japonés (Kanji / Kana)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = uiState.activeTheme.secondaryColor
                    )
                )

                OutlinedTextField(
                    value = subtitleSpanish,
                    onValueChange = { subtitleSpanish = it },
                    label = { Text("Subtítulo en Español", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = uiState.activeTheme.secondaryColor
                    )
                )
            }
        }

        // Save Edits Button
        Button(
            onClick = {
                viewModel.saveCreation()
            },
            colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("GUARDAR VERSIÓN CON EFECTOS FX", fontWeight = FontWeight.Bold)
        }
    }
}
