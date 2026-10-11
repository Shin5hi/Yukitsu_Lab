package com.example.data.ai

import com.example.data.model.ConsistentCharacter

data class ModifiedPromptResult(
    val fullModifiedPrompt: String,
    val characterAnchorTag: String,
    val immutableFeaturesDescription: String,
    val referenceImageDirective: String,
    val sceneAdaptationPrompt: String,
    val negativePrompt: String,
    val turnNumber: Int,
    val seed: Long?,
    val ipAdapterWeight: Float,
    val trackingMode: String
)

/**
 * Character-Tracking Prompt Modifier Module
 * 
 * Intercepts, anchors and rewrites generation prompts across multiple turns
 * to ensure persistent facial, hair, and aesthetic consistency based on a defined reference image.
 */
class CharacterTrackingPromptModifier {

    /**
     * Modifies the incoming generation prompt according to the character's reference image and tracking rules.
     */
    fun modifyPrompt(
        rawPrompt: String,
        character: ConsistentCharacter,
        turnNumber: Int = 1
    ): ModifiedPromptResult {
        val cleanUserPrompt = rawPrompt.trim()
        val anchorTag = character.identityToken.ifBlank { "<char_anchor:${character.id}:1.0>" }

        val refImage = character.referenceImageUrl ?: character.referenceImageUri
        val refDirective = if (!refImage.isNullOrBlank()) {
            "[REF_IMAGE_BOUND: weight=${"%.2f".format(character.ipAdapterWeight)}, source=${refImage.take(40)}...]"
        } else {
            "[REFERENCE_IDENTITY: ${character.name}, weight=${"%.2f".format(character.ipAdapterWeight)}]"
        }

        val immutableFeatures = buildString {
            append("face lock: ${character.eyeDescription}, ${character.distinctiveFeatures}; ")
            append("hair lock: ${character.hairDescription}; ")
            if (character.trackingMode == "STRICT_LOCK") {
                append("outfit lock: ${character.signatureOutfit}; ")
            }
        }.trim()

        val styleLockDirective = "[Style Lock: ${character.primaryStyle}, strength=${"%.2f".format(character.styleLockStrength)}]"

        val modifiedPrompt = when (character.trackingMode) {
            "STRICT_LOCK" -> {
                // Strict IP-Adapter + ControlNet lock: character appearance is absolute
                "$anchorTag $refDirective $styleLockDirective [CHARACTER: ${character.name}, $immutableFeatures] $cleanUserPrompt, masterpiece anime key visual, highly consistent character identity, seed=${character.fixedSeed ?: 4289124L}"
            }
            "CREATIVE_FUSION" -> {
                // Creative fusion: identity is preserved, but clothing and style flex with user prompt
                "$anchorTag [CONSISTENT FACE & HAIR: ${character.hairDescription}, ${character.eyeDescription}] $cleanUserPrompt, anime cinematic rendering, ${character.primaryStyle} style"
            }
            else -> {
                // AUTO_ADAPTIVE: Standard balanced consistency
                "$anchorTag $refDirective [CHARACTER: ${character.name}, ${character.hairDescription}, ${character.eyeDescription}, ${character.distinctiveFeatures}] $cleanUserPrompt, ${character.primaryStyle} anime aesthetic, consistent facial geometry"
            }
        }

        val negativePrompt = generateNegativeAnchorPrompt(character)

        return ModifiedPromptResult(
            fullModifiedPrompt = modifiedPrompt,
            characterAnchorTag = anchorTag,
            immutableFeaturesDescription = immutableFeatures,
            referenceImageDirective = refDirective,
            sceneAdaptationPrompt = cleanUserPrompt,
            negativePrompt = negativePrompt,
            turnNumber = turnNumber,
            seed = character.fixedSeed,
            ipAdapterWeight = character.ipAdapterWeight,
            trackingMode = character.trackingMode
        )
    }

    /**
     * Generates a tailored negative prompt to suppress facial drift and unwanted variations.
     */
    fun generateNegativeAnchorPrompt(character: ConsistentCharacter): String {
        return buildString {
            append("face distortion, inconsistent facial structure, altered eye color, changed hair style, ")
            append("wrong character face, face drift, missing signature features, duplicate faces, ")
            if (character.trackingMode == "STRICT_LOCK") {
                append("mismatched costume, costume hallucination, altered outfit colors, ")
            }
            append("blurry eyes, bad anime anatomy, low quality, artifacts, watermark")
        }
    }
}
