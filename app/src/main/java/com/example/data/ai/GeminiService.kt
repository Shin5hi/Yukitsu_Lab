package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.anime.AnimeStyleCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY.ifBlank { "" }

    /**
     * Expands simple user descriptions into rich anime director prompts.
     */
    suspend fun enhancePrompt(
        prompt: String,
        genreId: String,
        styleId: String,
        threadHistory: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext fallbackEnhancedPrompt(prompt, genreId, styleId)
        }

        val genre = AnimeStyleCatalog.genres.find { it.id == genreId }
        val style = AnimeStyleCatalog.styles.find { it.id == styleId }

        val systemInstructionText = """
            Eres un director creativo legendario de la industria del anime japonés (Otaku Creative Director AI).
            Tu objetivo es transformar la idea simple del usuario en un prompt visual maestro y cinematográfico para generar imágenes/arte anime.
            
            Contexto del proyecto:
            - Demografía / Género anime: ${genre?.name ?: "Anime"} (${genre?.japaneseName ?: ""}). Pautas: ${genre?.promptModifiers ?: ""}
            - Estilo de animación / Época: ${style?.name ?: "Moderno"} (${style?.japaneseName ?: ""}). Pautas: ${style?.promptKeywords ?: ""}
            
            Instrucciones críticas:
            1. Mantén la esencia y los personajes pedidos por el usuario.
            2. Agrega detalles de iluminación, paleta de colores, tipo de trazo/entintado, composición de plano, atmósfera emocional y partículas según el género y estilo seleccionados.
            3. Si hay memoria de turnos anteriores, mantén la coherencia de personajes y atuendos.
            4. Responde ÚNICAMENTE con el prompt optimizado, sin introducciones ni saludos. Puede estar en inglés o español enriquecido con términos de animación japonesa.
        """.trimIndent()

        val historyContext = if (threadHistory.isNotEmpty()) {
            "Memoria previa del hilo de creación:\n" + threadHistory.takeLast(4).joinToString("\n- ") + "\n\nNueva petición actual: "
        } else ""

        val userContent = historyContext + prompt

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", userContent))
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstructionText))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val contentObj = firstCandidate?.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")?.trim()
                    if (!text.isNullOrBlank()) return@withContext text
                }
            }
        } catch (e: Exception) {
            Log.w("GeminiService", "Failed to enhance prompt with Gemini: ${e.message}")
        }

        fallbackEnhancedPrompt(prompt, genreId, styleId)
    }

    /**
     * Speech / Audio intelligent intent repair.
     * Understands muffled, misheard, or garbled voice input in anime context immediately.
     */
    suspend fun repairSpeechPrompt(
        rawTranscript: String,
        genreId: String,
        styleId: String,
        threadHistory: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || rawTranscript.isBlank()) {
            return@withContext rawTranscript
        }

        val genre = AnimeStyleCatalog.genres.find { it.id == genreId }
        val style = AnimeStyleCatalog.styles.find { it.id == styleId }

        val systemPrompt = """
            Eres un asistente especializado en comprensión de voz y lenguaje natural para creación de anime y manga.
            El usuario acaba de hablar por el micrófono, pero el reconocimiento de voz del dispositivo pudo haber capturado palabras confusas, mal pronunciadas, ruidos o términos fonéticamente parecidos (por ejemplo: 'gato ninja en lluvia de leon' -> 'gato ninja en lluvia de neón', 'espanto de fuego' -> 'espada de fuego', 'saiyayin' -> 'super saiyan').
            
            Contexto actual:
            - Género: ${genre?.name ?: "Anime"} (${genre?.japaneseName ?: ""})
            - Estilo: ${style?.name ?: "Moderno"}
            ${if (threadHistory.isNotEmpty()) "- Contexto del hilo: " + threadHistory.takeLast(3).joinToString(" | ") else ""}
            
            Tu tarea:
            Averigua EXACTAMENTE y a la primera lo que el usuario quiso decir en el contexto del anime, sin obligarle a repetir ni escribir nada.
            Devuelve ÚNICAMENTE la frase corregida y natural que describe lo que quiere crear, sin ninguna explicación.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "Transcripción de voz sin procesar: \"$rawTranscript\""))
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemPrompt))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val contentObj = firstCandidate?.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")?.trim()
                    if (!text.isNullOrBlank()) return@withContext text.removeSurrounding("\"")
                }
            }
        } catch (e: Exception) {
            Log.w("GeminiService", "Speech intent repair failed: ${e.message}")
        }

        rawTranscript
    }

    /**
     * Cuaderno Style Remixer: blends multiple anime styles into a harmonious prompt.
     */
    suspend fun remixAnimeStyles(
        basePrompt: String,
        primaryStyleId: String,
        secondaryStyleId: String,
        customNotes: String = ""
    ): String = withContext(Dispatchers.IO) {
        val s1 = AnimeStyleCatalog.styles.find { it.id == primaryStyleId } ?: AnimeStyleCatalog.styles[0]
        val s2 = AnimeStyleCatalog.styles.find { it.id == secondaryStyleId } ?: AnimeStyleCatalog.styles[1]

        if (apiKey.isBlank()) {
            return@withContext "$basePrompt, fusing ${s1.name} and ${s2.name}, ${s1.promptKeywords}, ${s2.promptKeywords}"
        }

        val promptContent = """
            Combina armónicamente dos estilos de anime en una sola visión artística:
            Estilo Primario: ${s1.name} (${s1.promptKeywords})
            Estilo Secundario: ${s2.name} (${s2.promptKeywords})
            Idea base del usuario: $basePrompt
            ${if (customNotes.isNotBlank()) "Notas de ayuda/fuentes visuales del cuaderno: $customNotes" else ""}
            
            Genera un prompt unificado que fusione la esencia de ambos estilos sin contradecirse.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", promptContent))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val text = root.optJSONArray("candidates")?.optJSONObject(0)
                        ?.optJSONObject("content")?.optJSONArray("parts")
                        ?.optJSONObject(0)?.optString("text")?.trim()
                    if (!text.isNullOrBlank()) return@withContext text
                }
            }
        } catch (_: Exception) {}

        "$basePrompt, fusing ${s1.name} and ${s2.name}, ${s1.promptKeywords}, ${s2.promptKeywords}"
    }

    /**
     * Image generation using gemini-nano-banana-2.1 or gemini-3-pro-image-preview
     */
    suspend fun generateImage(
        prompt: String,
        aspectRatio: String = "1:1",
        imageSize: String = "1K"
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("No API key configured"))
        }

        val model = if (imageSize == "4K" || imageSize == "2K") {
            "gemini-3-pro-image-preview"
        } else {
            "gemini-nano-banana-2.1"
        }

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("imageConfig", JSONObject().apply {
                    put("aspectRatio", aspectRatio)
                    put("imageSize", imageSize)
                })
                put("responseModalities", JSONArray().apply {
                    put("IMAGE")
                    put("TEXT")
                })
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val candidates = root.optJSONArray("candidates")
                    val parts = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.optJSONObject(i)
                            val inlineData = part?.optJSONObject("inlineData")
                            if (inlineData != null) {
                                val mimeType = inlineData.optString("mimeType", "image/png")
                                val data = inlineData.optString("data")
                                if (data.isNotBlank()) {
                                    return@withContext Result.success("data:$mimeType;base64,$data")
                                }
                            }
                        }
                    }
                }
                Result.failure(Exception("Image generation API error: ${response.code} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Video generation with Veo 3.1
     */
    suspend fun generateVideo(
        prompt: String,
        aspectRatio: String = "16:9"
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("No API key configured"))
        }

        val jsonBody = JSONObject().apply {
            put("prompt", prompt)
            put("config", JSONObject().apply {
                put("numberOfVideos", 1)
                put("resolution", "1080p")
                put("aspectRatio", if (aspectRatio == "9:16") "9:16" else "16:9")
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/veo-3.1-fast-generate-preview:generateVideos?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val operationName = root.optString("name")
                    return@withContext Result.success(operationName)
                }
                Result.failure(Exception("Veo API error: ${response.code} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Synthesizes and trains a ComfyUI-style Consistent Character Anchor with prompt embeddings,
     * turnaround reference descriptions, and multi-scene verification parameters.
     */
    suspend fun trainCharacterConsistencyPrompt(
        characterName: String,
        animeGenre: String,
        primaryStyle: String,
        hairDescription: String,
        eyeDescription: String,
        outfitDescription: String,
        distinctiveMarks: String
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext "character anchor: ([$characterName], identical face structure, consistent $hairDescription, signature $eyeDescription, wearing $outfitDescription, $distinctiveMarks:1.25), anime style lock: $primaryStyle, IP-Adapter FaceID weight 0.85"
        }

        val promptText = """
            Actúa como el motor de nodos de consistencia de personajes de ComfyUI (IP-Adapter FaceID + Reference-Only + ControlNet OpenPose).
            Crea un Prompt Anchor técnico altamente optimizado para congelar la identidad facial y corporal de este personaje de anime a través de múltiples escenas sin perder consistencia:
            - Nombre: $characterName
            - Género/Demografía anime: $animeGenre
            - Estilo base: $primaryStyle
            - Cabello: $hairDescription
            - Ojos: $eyeDescription
            - Traje distintivo: $outfitDescription
            - Rasgos distintivos: $distinctiveMarks

            Genera el prompt anchor con pesos de ponderación ComfyUI (ej: (trait:1.2), <char_anchor:0.9>), tokens inmutables y preservación de rasgos anatómicos. Responde solo con el prompt anchor técnico.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", promptText))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val root = JSONObject(body)
                    val text = root.optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")?.trim()
                    if (!text.isNullOrBlank()) return@withContext text
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Error in trainCharacterConsistencyPrompt", e)
        }

        "([$characterName], identical facial structure, consistent $hairDescription, $eyeDescription, $outfitDescription, $distinctiveMarks:1.25), (IP-Adapter face lock:0.85), (ComfyUI consistency anchor:1.15)"
    }

    private fun fallbackEnhancedPrompt(prompt: String, genreId: String, styleId: String): String {
        val genre = AnimeStyleCatalog.genres.find { it.id == genreId }
        val style = AnimeStyleCatalog.styles.find { it.id == styleId }
        val genreKeywords = genre?.promptModifiers ?: "anime aesthetic"
        val styleKeywords = style?.promptKeywords ?: "high quality illustration"
        return "$prompt, $genreKeywords, $styleKeywords, masterpiece, ultra detailed, 8k resolution, authentic Japanese anime production quality"
    }
}
