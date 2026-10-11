package com.example.data.ai

import com.example.data.model.ConsistentCharacter
import org.json.JSONObject

/**
 * Generates standard ComfyUI API Prompt and Node Workflow JSON structures
 * bridging Phase 1 (Yukitsu Studio App) to Phase 2 (ComfyUI advanced node rendering).
 */
object ComfyUiWorkflowGenerator {

    /**
     * Generates a complete ComfyUI API workflow dictionary JSON that can be sent directly
     * to `POST /prompt` or saved as `workflow.json` to load in ComfyUI.
     */
    fun generateComfyUiWorkflowJson(
        character: ConsistentCharacter,
        scenePrompt: String = "en una escena cinematográfica de anime con iluminación épica"
    ): String {
        val root = JSONObject()
        val promptObj = JSONObject()

        val seed = character.fixedSeed ?: (100000000L..999999999L).random()
        val refImg = character.referenceImageUrl ?: character.referenceImageUri ?: "character_reference_image.png"

        // Node 1: CheckpointLoaderSimple
        val node1 = JSONObject().apply {
            put("class_type", "CheckpointLoaderSimple")
            put("inputs", JSONObject().apply {
                put("ckpt_name", if (character.animeGenre == "shonen") "animagineXL_v31.safetensors" else "anything-v5.0.safetensors")
            })
            put("_meta", JSONObject().apply { put("title", "1. Cargar Checkpoint Anime Base") })
        }
        promptObj.put("1", node1)

        // Node 2: LoadImage (Character Reference Image from Yukitsu Studio Phase 1)
        val node2 = JSONObject().apply {
            put("class_type", "LoadImage")
            put("inputs", JSONObject().apply {
                put("image", "yukitsu_ref_${character.name.lowercase().replace(" ", "_")}.png")
                put("upload", "image")
                put("reference_source_url", refImg)
            })
            put("_meta", JSONObject().apply { put("title", "2. Imagen de Referencia (Fase 1 App)") })
        }
        promptObj.put("2", node2)

        // Node 3: IPAdapterModelLoader
        val node3 = JSONObject().apply {
            put("class_type", "IPAdapterModelLoader")
            put("inputs", JSONObject().apply {
                put("ipadapter_file", "ip-adapter-plus_sdxl_vit-h.safetensors")
            })
            put("_meta", JSONObject().apply { put("title", "3. Cargar Modelo IP-Adapter") })
        }
        promptObj.put("3", node3)

        // Node 4: CLIPVisionLoader
        val node4 = JSONObject().apply {
            put("class_type", "CLIPVisionLoader")
            put("inputs", JSONObject().apply {
                put("clip_name", "clip-vit-h-14-laion2B-s32B-b79K.safetensors")
            })
            put("_meta", JSONObject().apply { put("title", "4. Cargar CLIP Vision") })
        }
        promptObj.put("4", node4)

        // Node 5: IPAdapterApply (Anchoring character facial consistency)
        val node5 = JSONObject().apply {
            put("class_type", "IPAdapterApply")
            put("inputs", JSONObject().apply {
                put("weight", character.ipAdapterWeight.toDouble())
                put("weight_type", "standard")
                put("noise", 0.0)
                put("ipadapter", listOf("3", 0))
                put("clip_vision", listOf("4", 0))
                put("image", listOf("2", 0))
                put("model", listOf("1", 0))
            })
            put("_meta", JSONObject().apply { put("title", "5. Aplicar IP-Adapter (Peso: ${"%.2f".format(character.ipAdapterWeight)})") })
        }
        promptObj.put("5", node5)

        // Node 6: CLIPTextEncode (Positive Prompt with Character Anchors)
        val fullPositivePrompt = buildString {
            append("${character.identityToken} ")
            append("1person, ${character.name}, ")
            append("${character.hairDescription}, ")
            append("${character.eyeDescription}, ")
            append("${character.signatureOutfit}, ")
            append("${character.distinctiveFeatures}, ")
            append("$scenePrompt, ")
            append("masterpiece anime key visual, studio quality, highly detailed facial geometry")
        }
        val node6 = JSONObject().apply {
            put("class_type", "CLIPTextEncode")
            put("inputs", JSONObject().apply {
                put("text", fullPositivePrompt)
                put("clip", listOf("1", 1))
            })
            put("_meta", JSONObject().apply { put("title", "6. Prompt Positivo (Anclajes Fase 1)") })
        }
        promptObj.put("6", node6)

        // Node 7: CLIPTextEncode (Negative Anti-Drift Prompt)
        val negativePrompt = "altered face, face drift, missing hair streaks, wrong eye color, inconsistent attire, ugly face, distorted pupils, bad anatomy, deformed hands, low quality, artifact, blur"
        val node7 = JSONObject().apply {
            put("class_type", "CLIPTextEncode")
            put("inputs", JSONObject().apply {
                put("text", negativePrompt)
                put("clip", listOf("1", 1))
            })
            put("_meta", JSONObject().apply { put("title", "7. Prompt Negativo Anti-Deriva Facial") })
        }
        promptObj.put("7", node7)

        // Node 8: EmptyLatentImage
        val node8 = JSONObject().apply {
            put("class_type", "EmptyLatentImage")
            put("inputs", JSONObject().apply {
                put("width", 832)
                put("height", 1216)
                put("batch_size", 1)
            })
            put("_meta", JSONObject().apply { put("title", "8. Latent Vacío (Resolución HD)") })
        }
        promptObj.put("8", node8)

        // Node 9: KSampler (Main Sampling)
        val node9 = JSONObject().apply {
            put("class_type", "KSampler")
            put("inputs", JSONObject().apply {
                put("seed", seed)
                put("steps", 28)
                put("cfg", 7.0)
                put("sampler_name", "euler_ancestral")
                put("scheduler", "karras")
                put("denoise", 1.0)
                put("model", listOf("5", 0)) // Model from IPAdapter
                put("positive", listOf("6", 0))
                put("negative", listOf("7", 0))
                put("latent_image", listOf("8", 0))
            })
            put("_meta", JSONObject().apply { put("title", "9. KSampler (Seed: $seed)") })
        }
        promptObj.put("9", node9)

        // Node 10: VAEDecode
        val node10 = JSONObject().apply {
            put("class_type", "VAEDecode")
            put("inputs", JSONObject().apply {
                put("samples", listOf("9", 0))
                put("vae", listOf("1", 2))
            })
            put("_meta", JSONObject().apply { put("title", "10. Decodificador VAE") })
        }
        promptObj.put("10", node10)

        // Node 11: FaceDetailer (Fase 2 Fine-Tuning Facial)
        val node11 = JSONObject().apply {
            put("class_type", "FaceDetailer")
            put("inputs", JSONObject().apply {
                put("image", listOf("10", 0))
                put("model", listOf("5", 0))
                put("clip", listOf("1", 1))
                put("vae", listOf("1", 2))
                put("guide_size", 512)
                put("max_size", 768)
                put("seed", seed)
                put("steps", 20)
                put("cfg", 6.5)
                put("sampler_name", "euler")
                put("scheduler", "normal")
                put("denoise", character.faceDetailerDenoise.toDouble())
                put("positive", listOf("6", 0))
                put("negative", listOf("7", 0))
            })
            put("_meta", JSONObject().apply { put("title", "11. FaceDetailer (Denoise: ${"%.2f".format(character.faceDetailerDenoise)})") })
        }
        promptObj.put("11", node11)

        // Node 12: SaveImage
        val node12 = JSONObject().apply {
            put("class_type", "SaveImage")
            put("inputs", JSONObject().apply {
                put("filename_prefix", "Yukitsu_Phase2_${character.name.lowercase().replace(" ", "_")}")
                put("images", listOf("11", 0))
            })
            put("_meta", JSONObject().apply { put("title", "12. Guardar Arte Final Fase 2") })
        }
        promptObj.put("12", node12)

        root.put("prompt", promptObj)
        root.put("client_id", "yukitsu_studio_android")
        root.put("extra_data", JSONObject().apply {
            put("character_name", character.name)
            put("phase", "Fase 2: ComfyUI Node Pipeline Execution")
            put("generated_by", "Yukitsu Studio Android Suite")
        })

        return root.toString(2)
    }
}
