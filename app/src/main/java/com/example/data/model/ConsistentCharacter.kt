package com.example.data.model

data class TurnaroundAngle(
    val viewName: String, // "Vista Frontal", "Ángulo 3/4", "Perfil Lateral", "Pose de Acción", "Expresión Emotiva"
    val description: String,
    val previewUrl: String,
    val angleDegrees: String = "0°"
)

data class ConsistencyVerificationScene(
    val sceneTitle: String,
    val prompt: String,
    val environment: String,
    val previewUrl: String
)

data class TrackingGenerationEntry(
    val id: String = "",
    val turnNumber: Int = 1,
    val rawPrompt: String = "",
    val modifiedTrackingPrompt: String = "",
    val negativePrompt: String = "",
    val previewUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ConsistentCharacter(
    val id: String = "",
    val name: String = "",
    val identityToken: String = "<char_consistent:identity_anchor:1.0>",
    val animeGenre: String = "shonen",
    val primaryStyle: String = "modern_digital",
    val hairDescription: String = "Cabello plateado en puntas rebeldes con mechas azul zafiro",
    val eyeDescription: String = "Ojos carmesí afilados con brillo estelar",
    val signatureOutfit: String = "Chaqueta haori negra moderna con runas doradas y bufanda roja",
    val distinctiveFeatures: String = "Cicatriz sutil sobre la ceja izquierda y pendiente de jade",
    val ipAdapterWeight: Float = 0.85f,
    val faceDetailerDenoise: Float = 0.40f,
    val controlNetGuidance: Float = 0.75f,
    val styleLockStrength: Float = 0.90f,
    val trainingStatus: String = "trained", // "idle", "training", "trained"
    val trainingSteps: Int = 1500,
    val turnaroundViews: List<TurnaroundAngle> = emptyList(),
    val testScenes: List<ConsistencyVerificationScene> = emptyList(),
    val consistencyScore: Int = 98, // e.g. 98% de fidelidad facial y de vestuario

    // Reference Image & Character-Tracking Module Fields
    val referenceImageUrl: String? = null,
    val referenceImageUri: String? = null,
    val isTrackingActive: Boolean = true,
    val trackingMode: String = "AUTO_ADAPTIVE", // "AUTO_ADAPTIVE", "STRICT_LOCK", "CREATIVE_FUSION"
    val customTrackingPrefix: String = "",
    val fixedSeed: Long? = 4289124L,
    val trackingGenerations: List<TrackingGenerationEntry> = emptyList()
)
