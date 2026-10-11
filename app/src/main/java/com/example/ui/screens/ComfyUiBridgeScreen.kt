package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.ai.ComfyUiWorkflowGenerator
import com.example.data.model.ConsistentCharacter
import com.example.ui.YukitsuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Conexión Fase 1 ➜ Fase 2 (Yukitsu Studio a ComfyUI)
 * 
 * En la app se crea el personaje y sus estilos (Fase 1).
 * La ejecución pesada, nodos IP-Adapter y consistencia técnica se delegan a ComfyUI (Fase 2).
 */
@Composable
fun ComfyUiBridgeScreen(
    viewModel: YukitsuViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val activeChar = uiState.activeConsistentCharacter ?: uiState.consistentCharacters.firstOrNull()

    var comfyUiServerUrl by remember { mutableStateOf("http://127.0.0.1:8188") }
    var isSendingToComfyUi by remember { mutableStateOf(false) }
    var comfyUiResponseStatus by remember { mutableStateOf<String?>(null) }
    var showWorkflowJsonPreview by remember { mutableStateOf(false) }

    val httpClient = remember {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E18))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera del Flujo en 2 Fases
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172A)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00F0FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00F0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = Color(0xFF0C0E18),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "PUENTE A COMFYUI • FASE 2",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00F0FF),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "De la idea en móvil al renderizado de nodos en PC/Cloud",
                                fontSize = 11.sp,
                                color = Color(0xFFD6DBE7)
                            )
                        }
                    }

                    // Stepper Visual: Fase 1 -> Fase 2
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A0C16))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0B3B2F)
                            ) {
                                Text(
                                    text = "FASE 1 (APP)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF06D6A0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Creación de estilo, rasgos faciales e imagen base",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF281C3D)
                            ) {
                                Text(
                                    text = "FASE 2 (COMFYUI)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8A00),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nodos IP-Adapter, FaceDetailer y render pesado",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Selector de Personaje Activo
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECCIONA EL PERSONAJE A ENVIAR A COMFYUI:",
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
                            onClick = { viewModel.selectActiveConsistentCharacter(char) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF003847) else Color(0xFF161A2D)
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
                                        .size(36.dp)
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
                                        text = "Listo para Fase 2",
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
            val workflowJson = remember(activeChar, uiState.prompt) {
                ComfyUiWorkflowGenerator.generateComfyUiWorkflowJson(
                    character = activeChar,
                    scenePrompt = uiState.prompt.ifBlank { "en una escena anime cinematográfica de alta calidad" }
                )
            }

            // Tarjeta de Exportación de Flujo JSON
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
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8A00),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "WORKFLOW LISTO PARA COMFYUI",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E243D)
                            ) {
                                Text(
                                    text = ".JSON DIRECTO",
                                    fontSize = 10.sp,
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Yukitsu Studio ha generado la red completa de 12 nodos (IP-Adapter, LoadImage, ControlNet, FaceDetailer y KSampler) para ${activeChar.name}.",
                            fontSize = 12.sp,
                            color = Color(0xFFADB5BD),
                            lineHeight = 16.sp
                        )

                        // Botones de Acción para el Workflow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(workflowJson))
                                    Toast.makeText(context, "¡Workflow JSON copiado! Pégalo o arrástralo a ComfyUI.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("copy_workflow_json_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color(0xFF0C0E18),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copiar JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0C0E18))
                            }

                            Button(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, workflowJson)
                                        putExtra(Intent.EXTRA_SUBJECT, "comfyui_workflow_${activeChar.name.lowercase().replace(" ", "_")}.json")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Enviar Workflow ComfyUI a PC o Drive")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("share_workflow_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compartir .json", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        TextButton(
                            onClick = { showWorkflowJsonPreview = !showWorkflowJsonPreview },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (showWorkflowJsonPreview) "Ocultar vista previa del JSON" else "Ver código de nodos ComfyUI ➜",
                                fontSize = 11.sp,
                                color = Color(0xFF00F0FF)
                            )
                        }

                        if (showWorkflowJsonPreview) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF070810))
                                    .border(1.dp, Color(0xFF1D233D), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = workflowJson,
                                    fontSize = 10.sp,
                                    color = Color(0xFF88C0D0),
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Conexión Directa por API (HTTP /prompt)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121627)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF252B46)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cable,
                                contentDescription = null,
                                tint = Color(0xFF06D6A0),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONEXIÓN DIRECTA CON COMFYUI SERVER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Si tienes ComfyUI corriendo en tu ordenador (con --listen) o en un servidor en la nube, puedes enviar la Fase 2 directamente a la cola de render:",
                            fontSize = 11.sp,
                            color = Color(0xFFADB5BD)
                        )

                        OutlinedTextField(
                            value = comfyUiServerUrl,
                            onValueChange = { comfyUiServerUrl = it },
                            label = { Text("URL de ComfyUI (ej. http://192.168.1.50:8188)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF06D6A0),
                                unfocusedBorderColor = Color(0xFF2D3553)
                            )
                        )

                        Button(
                            onClick = {
                                isSendingToComfyUi = true
                                comfyUiResponseStatus = null
                                coroutineScope.launch {
                                    val result = sendPromptToComfyUi(
                                        client = httpClient,
                                        serverUrl = comfyUiServerUrl.trim(),
                                        payloadJson = workflowJson
                                    )
                                    withContext(Dispatchers.Main) {
                                        isSendingToComfyUi = false
                                        comfyUiResponseStatus = result
                                    }
                                }
                            },
                            enabled = !isSendingToComfyUi && comfyUiServerUrl.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("dispatch_to_comfyui_button")
                        ) {
                            if (isSendingToComfyUi) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF0C0E18),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conectando con ComfyUI...", color = Color(0xFF0C0E18), fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = Color(0xFF0C0E18),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ENVIAR A COLA DE COMFYUI (FASE 2)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0C0E18))
                            }
                        }

                        if (comfyUiResponseStatus != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF081C17),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = comfyUiResponseStatus ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFF06D6A0),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Guía Rápida de la Fase 2
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1222)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF202744)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 CÓMO USAR EN TU COMFYUI DE PC:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD166)
                        )
                        Text(
                            text = "1. Pulsa 'Copiar JSON' o 'Compartir .json' y guárdalo.\n" +
                                   "2. Abre tu ComfyUI en el navegador y simplemente arrastra el archivo sobre el lienzo.\n" +
                                   "3. Se cargarán automáticamente todos los nodos con los pesos de IP-Adapter y FaceDetailer fijados en la app.\n" +
                                   "4. Pulsa 'Queue Prompt' en ComfyUI para disfrutar de renderizado acelerado por tu GPU.",
                            fontSize = 11.sp,
                            color = Color(0xFFADB5BD),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

private suspend fun sendPromptToComfyUi(
    client: OkHttpClient,
    serverUrl: String,
    payloadJson: String
): String = withContext(Dispatchers.IO) {
    try {
        val cleanUrl = serverUrl.removeSuffix("/") + "/prompt"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = payloadJson.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(cleanUrl)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val respBody = response.body?.string() ?: ""
                "✓ ¡Éxito! Workflow enviado a la cola de ComfyUI. Respuesta: ${respBody.take(120)}"
            } else {
                "⚠️ ComfyUI respondió con código ${response.code}: Asegúrate de haber iniciado ComfyUI con --listen"
            }
        }
    } catch (e: Exception) {
        "ℹ️ No se pudo conectar directamente con ComfyUI en $serverUrl (${e.localizedMessage}). Puedes usar el botón 'Copiar JSON' o 'Compartir .json' para cargarlo manualmente."
    }
}
