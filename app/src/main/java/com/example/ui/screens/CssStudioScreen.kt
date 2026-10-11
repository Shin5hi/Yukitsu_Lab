package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.YukitsuViewModel
import com.example.ui.theme.css.CssEngine

@Composable
fun CssStudioScreen(
    viewModel: YukitsuViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
                text = "雪津スタジオ • ESTILOS CSS EN VIVO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = uiState.activeTheme.secondaryColor,
                letterSpacing = 1.sp
            )
            Text(
                text = "Motor CSS Personalizado",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Personaliza tanto la apariencia de la interfaz de la app como el diseño y borde de tus tarjetas de arte anime con código CSS en tiempo real.",
                fontSize = 12.sp,
                color = Color(0xFF9097A6)
            )
        }

        // Section 1: Preset Themes Carousel
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PLANTILLAS DE TEMA CSS PREDEFINIDAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9097A6)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CssEngine.PRESET_THEMES.forEach { preset ->
                    val isSelected = uiState.activeTheme.name == preset.name
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF241C34) else Color(0xFF141728)
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) preset.primaryColor else Color(0xFF242A46),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectPresetTheme(preset) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(preset.primaryColor))
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(preset.secondaryColor))
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = preset.primaryColor, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(preset.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            Text("Efecto: ${preset.filterEffect}", fontSize = 11.sp, color = Color(0xFF868E96))
                        }
                    }
                }
            }
        }

        // Section 2: Live Rendered Card Preview under CSS properties
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VISTA PREVIA DE TARJETA EN VIVO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = uiState.activeTheme.secondaryColor
                    )
                    Text(
                        text = "Radio: ${uiState.activeTheme.cardCornerRadius.value}dp • Borde: ${uiState.activeTheme.cardBorderWidth.value}dp",
                        fontSize = 10.sp,
                        color = Color(0xFF868E96)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(uiState.activeTheme.cardCornerRadius.coerceAtMost(16.dp)))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val img = uiState.lastGeneratedMediaUrl.ifBlank {
                        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop"
                    }
                    AsyncImage(
                        model = img,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay card badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = uiState.activeTheme.primaryColor.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "YUKITSU CUSTOM CSS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Section 3: Live CSS Code Editor
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111E)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF262C46), RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EDITOR DE CÓDIGO CSS (Declaraciones en Vivo)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD166)
                    )

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Yukitsu CSS", uiState.rawCssInput)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "¡Código CSS copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar CSS", tint = Color(0xFFADB5BD), modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedTextField(
                    value = uiState.rawCssInput,
                    onValueChange = { viewModel.updateRawCss(it) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF00F0FF)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF00F0FF),
                        unfocusedTextColor = Color(0xFF00F0FF),
                        focusedBorderColor = uiState.activeTheme.primaryColor,
                        unfocusedBorderColor = Color(0xFF2E3557),
                        focusedContainerColor = Color(0xFF0A0C16),
                        unfocusedContainerColor = Color(0xFF0A0C16)
                    ),
                    minLines = 8,
                    maxLines = 14
                )

                Text(
                    text = "Variables admitidas: --primary-color, --secondary-color, --card-bg, --card-border-color, --border-radius, --border-width, --filter-effect (scanlines, halftone, glow, aura, vignette)",
                    fontSize = 10.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }

        // Section 4: Apply to entire Application Action
        Button(
            onClick = { viewModel.applyThemeToApp() },
            colors = ButtonDefaults.buttonColors(containerColor = uiState.activeTheme.primaryColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "APLICAR ESTE CSS A TODA LA APLICACIÓN",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
