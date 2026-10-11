package com.example.ui.theme.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CustomAnimeThemeState(
    val name: String = "Por defecto",
    val primaryColor: Color = Color(0xFFFF2E63),
    val secondaryColor: Color = Color(0xFF00F0FF),
    val cardBackgroundColor: Color = Color(0xFF151828),
    val cardBorderColor: Color = Color(0xFFFF2E63),
    val cardBorderWidth: Dp = 1.5.dp,
    val cardCornerRadius: Dp = 16.dp,
    val glowColor: Color = Color(0x66FF2E63),
    val fontScale: Float = 1.0f,
    val filterEffect: String = "none",
    val rawCss: String = """
        /* AnimeGen Studio Custom Theme */
        --primary-color: #FF2E63;
        --secondary-color: #00F0FF;
        --card-bg: #151828;
        --card-border-color: #FF2E63;
        --card-border-width: 1.5px;
        --border-radius: 16px;
        --box-shadow-glow: #FF2E6366;
        --filter-effect: none;
    """.trimIndent()
)

object CssEngine {

    val PRESET_THEMES = listOf(
        CustomAnimeThemeState(
            name = "Neo-Tokyo Cyberpunk",
            primaryColor = Color(0xFF00F0FF),
            secondaryColor = Color(0xFFFF007F),
            cardBackgroundColor = Color(0xFF0A0E1A),
            cardBorderColor = Color(0xFF00F0FF),
            cardBorderWidth = 2.dp,
            cardCornerRadius = 12.dp,
            glowColor = Color(0x8800F0FF),
            fontScale = 1.0f,
            filterEffect = "glow",
            rawCss = """
                /* Neo-Tokyo Cyberpunk */
                --primary-color: #00F0FF;
                --secondary-color: #FF007F;
                --card-bg: #0A0E1A;
                --card-border-color: #00F0FF;
                --card-border-width: 2px;
                --border-radius: 12px;
                --box-shadow-glow: #00F0FF88;
                --filter-effect: glow;
            """.trimIndent()
        ),
        CustomAnimeThemeState(
            name = "90s Retro Cel CRT",
            primaryColor = Color(0xFFFFD166),
            secondaryColor = Color(0xFFEF476F),
            cardBackgroundColor = Color(0xFF1B1A24),
            cardBorderColor = Color(0xFFFFD166),
            cardBorderWidth = 1.5.dp,
            cardCornerRadius = 8.dp,
            glowColor = Color(0x55FFD166),
            fontScale = 0.95f,
            filterEffect = "scanlines",
            rawCss = """
                /* 90s Retro Cel CRT */
                --primary-color: #FFD166;
                --secondary-color: #EF476F;
                --card-bg: #1B1A24;
                --card-border-color: #FFD166;
                --card-border-width: 1.5px;
                --border-radius: 8px;
                --box-shadow-glow: #FFD16655;
                --filter-effect: scanlines;
            """.trimIndent()
        ),
        CustomAnimeThemeState(
            name = "Shonen Ki Crimson",
            primaryColor = Color(0xFFFF2E63),
            secondaryColor = Color(0xFFFF9E00),
            cardBackgroundColor = Color(0xFF140D18),
            cardBorderColor = Color(0xFFFF2E63),
            cardBorderWidth = 2.dp,
            cardCornerRadius = 20.dp,
            glowColor = Color(0x77FF2E63),
            fontScale = 1.05f,
            filterEffect = "aura",
            rawCss = """
                /* Shonen Ki Crimson */
                --primary-color: #FF2E63;
                --secondary-color: #FF9E00;
                --card-bg: #140D18;
                --card-border-color: #FF2E63;
                --card-border-width: 2px;
                --border-radius: 20px;
                --box-shadow-glow: #FF2E6377;
                --filter-effect: aura;
            """.trimIndent()
        ),
        CustomAnimeThemeState(
            name = "Ghibli Pastoral Gouache",
            primaryColor = Color(0xFF06D6A0),
            secondaryColor = Color(0xFF118AB2),
            cardBackgroundColor = Color(0xFF111D18),
            cardBorderColor = Color(0xFF06D6A0),
            cardBorderWidth = 1.dp,
            cardCornerRadius = 24.dp,
            glowColor = Color(0x4406D6A0),
            fontScale = 1.0f,
            filterEffect = "vignette",
            rawCss = """
                /* Ghibli Pastoral Gouache */
                --primary-color: #06D6A0;
                --secondary-color: #118AB2;
                --card-bg: #111D18;
                --card-border-color: #06D6A0;
                --card-border-width: 1px;
                --border-radius: 24px;
                --box-shadow-glow: #06D6A044;
                --filter-effect: vignette;
            """.trimIndent()
        ),
        CustomAnimeThemeState(
            name = "Manga Monochrome Halftone",
            primaryColor = Color(0xFFF8F9FA),
            secondaryColor = Color(0xFFADB5BD),
            cardBackgroundColor = Color(0xFF121212),
            cardBorderColor = Color(0xFFF8F9FA),
            cardBorderWidth = 2.dp,
            cardCornerRadius = 4.dp,
            glowColor = Color(0x33FFFFFF),
            fontScale = 1.0f,
            filterEffect = "halftone",
            rawCss = """
                /* Manga Monochrome Halftone */
                --primary-color: #F8F9FA;
                --secondary-color: #ADB5BD;
                --card-bg: #121212;
                --card-border-color: #F8F9FA;
                --card-border-width: 2px;
                --border-radius: 4px;
                --box-shadow-glow: #FFFFFF33;
                --filter-effect: halftone;
            """.trimIndent()
        )
    )

    fun parseCss(css: String, fallback: CustomAnimeThemeState = PRESET_THEMES.first()): CustomAnimeThemeState {
        var primary = fallback.primaryColor
        var secondary = fallback.secondaryColor
        var cardBg = fallback.cardBackgroundColor
        var cardBorder = fallback.cardBorderColor
        var borderWidth = fallback.cardBorderWidth
        var cornerRadius = fallback.cardCornerRadius
        var glow = fallback.glowColor
        var filter = fallback.filterEffect

        val lines = css.lines()
        for (line in lines) {
            val trimmed = line.trim().removeSuffix(";")
            val parts = trimmed.split(":")
            if (parts.size >= 2) {
                val prop = parts[0].trim().lowercase()
                val value = parts[1].trim()

                when {
                    prop.contains("primary") -> parseColor(value)?.let { primary = it }
                    prop.contains("secondary") -> parseColor(value)?.let { secondary = it }
                    prop.contains("card-bg") || prop.contains("background") -> parseColor(value)?.let { cardBg = it }
                    prop.contains("border-color") -> parseColor(value)?.let { cardBorder = it }
                    prop.contains("border-width") -> parseDimension(value)?.let { borderWidth = it }
                    prop.contains("radius") -> parseDimension(value)?.let { cornerRadius = it }
                    prop.contains("glow") || prop.contains("shadow") -> parseColor(value)?.let { glow = it }
                    prop.contains("filter") -> filter = value.lowercase().trim()
                }
            }
        }

        return fallback.copy(
            primaryColor = primary,
            secondaryColor = secondary,
            cardBackgroundColor = cardBg,
            cardBorderColor = cardBorder,
            cardBorderWidth = borderWidth,
            cardCornerRadius = cornerRadius,
            glowColor = glow,
            filterEffect = filter,
            rawCss = css
        )
    }

    private fun parseColor(hex: String): Color? {
        val clean = hex.removePrefix("#").trim()
        return try {
            when (clean.length) {
                6 -> {
                    val r = clean.substring(0, 2).toInt(16)
                    val g = clean.substring(2, 4).toInt(16)
                    val b = clean.substring(4, 6).toInt(16)
                    Color(r, g, b, 255)
                }
                8 -> {
                    val r = clean.substring(0, 2).toInt(16)
                    val g = clean.substring(2, 4).toInt(16)
                    val b = clean.substring(4, 6).toInt(16)
                    val a = clean.substring(6, 8).toInt(16)
                    Color(r, g, b, a)
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parseDimension(dim: String): Dp? {
        val clean = dim.lowercase().replace("px", "").replace("dp", "").trim()
        val num = clean.toFloatOrNull() ?: return null
        return num.dp
    }
}
