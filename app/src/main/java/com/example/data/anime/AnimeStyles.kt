package com.example.data.anime

data class AnimeGenreInfo(
    val id: String,
    val name: String,
    val japaneseName: String,
    val description: String,
    val promptModifiers: String,
    val accentColor: String,
    val iconName: String
)

data class AnimeStyleInfo(
    val id: String,
    val name: String,
    val japaneseName: String,
    val era: String,
    val description: String,
    val promptKeywords: String,
    val category: String,
    val signatureEffect: String
)

object AnimeStyleCatalog {

    val genres = listOf(
        AnimeGenreInfo(
            id = "shonen",
            name = "Shōnen",
            japaneseName = "少年",
            description = "Acción trepidante, auras de energía, líneas de velocidad y batallas épicas",
            promptModifiers = "shonen anime style, intense dynamic action pose, glowing ki energy aura, bold outlines, dramatic lighting, heroic expression, speed lines",
            accentColor = "#FF2E63",
            iconName = "bolt"
        ),
        AnimeGenreInfo(
            id = "seinen",
            name = "Seinen",
            japaneseName = "青年",
            description = "Psicológico, sombreado detallado, alta tensión, estética noir y texturas crudas",
            promptModifiers = "seinen anime aesthetic, mature dark tone, intricate cross-hatching, high contrast shadows, film noir atmosphere, gritty realism, deep psychological depth",
            accentColor = "#7B2CBF",
            iconName = "psychology"
        ),
        AnimeGenreInfo(
            id = "kodomo",
            name = "Kodomo",
            japaneseName = "子供",
            description = "Amigable, formas redondeadas y tiernas, colores vivos y alegres",
            promptModifiers = "kodomo anime aesthetic, cute chibi proportions, vibrant cheerful pastel colors, rounded soft outlines, playful whimsical atmosphere, joyful expressions",
            accentColor = "#06D6A0",
            iconName = "child_care"
        ),
        AnimeGenreInfo(
            id = "shojo",
            name = "Shōjo",
            japaneseName = "少女",
            description = "Romántico, destellos mágicos, flores flotantes, líneas delicadas y etéreas",
            promptModifiers = "shojo anime style, ethereal sparkling particles, soft pastel lighting, delicate slender linework, floating cherry blossoms, dreamy emotional ambiance, glowing eyes",
            accentColor = "#FF70A6",
            iconName = "favorite"
        ),
        AnimeGenreInfo(
            id = "josei",
            name = "Josei",
            japaneseName = "女性",
            description = "Elegancia madura, acuarela sutil, drama realista y moda refinada",
            promptModifiers = "josei anime art style, sophisticated slice of life, soft watercolor wash, naturalistic proportions, atmospheric urban nostalgia, subtle refined emotions",
            accentColor = "#F9844A",
            iconName = "palette"
        ),
        AnimeGenreInfo(
            id = "mecha",
            name = "Mecha & Sci-Fi",
            japaneseName = "メカ",
            description = "Robots gigantes, tecnología cyberpunk, paneles mecánicos y luces de neón",
            promptModifiers = "mecha anime style, intricate mechanical joints, hard surface armor, glowing cockpit HUD display, neon reflections, cinematic lens flare, high-tech weaponry",
            accentColor = "#00F0FF",
            iconName = "smart_toy"
        )
    )

    val styles = listOf(
        AnimeStyleInfo(
            id = "retro_90s",
            name = "90s Cel Clásico",
            japaneseName = "セル画 90年代",
            era = "1990s",
            description = "Animación tradicional pintada a mano en acetato con grano analógico y estética CRT",
            promptKeywords = "1990s vintage anime cel animation, hand-painted cel shading, analog CRT film grain, saturated nostalgic colors, retro aesthetic like Cowboy Bebop and Sailor Moon",
            category = "Retro",
            signatureEffect = "CRT Scanlines"
        ),
        AnimeStyleInfo(
            id = "modern_digital",
            name = "Moderno Ufotable",
            japaneseName = "デジタル最新",
            era = "2020s",
            description = "Composición digital cinematográfica, chispas volumétricas e iluminación hiper-dinámica",
            promptKeywords = "modern high-budget digital anime, ufotable style, cinematic composition, dynamic particle sparks, glowing magical blade effects, 4k digital compositing, dramatic focal blur",
            category = "Moderno",
            signatureEffect = "Elemental VFX"
        ),
        AnimeStyleInfo(
            id = "ghibli",
            name = "Estudio Ghibli",
            japaneseName = "ジブリ風",
            era = "Atemporal",
            description = "Fondos de gouache pintados a mano, nubes esponjosas y naturaleza bucólica y mágica",
            promptKeywords = "Studio Ghibli aesthetic, Hayao Miyazaki art style, lush hand-painted gouache background, fluffy cumulus clouds, peaceful pastoral nature, soft warm natural sunlight, nostalgic wonder",
            category = "Artístico",
            signatureEffect = "Pintura Gouache"
        ),
        AnimeStyleInfo(
            id = "makoto_shinkai",
            name = "Cielos de Shinkai",
            japaneseName = "新海誠風",
            era = "Cinematográfico",
            description = "Refracción de luz hiper-detallada, crepúsculos estrellados y reflejos en la lluvia",
            promptKeywords = "Makoto Shinkai style, breathtaking twilight sky, sparkling meteor shower, ultra-detailed rain reflections, hyper-realistic lens flare, emotive atmospheric lighting, Your Name aesthetic",
            category = "Cinematográfico",
            signatureEffect = "Luz Crepuscular"
        ),
        AnimeStyleInfo(
            id = "manga_ink",
            name = "Manga Entintado & Tramas",
            japaneseName = "漫画 白黒",
            era = "Manga",
            description = "Blanco y negro con tramas mecánicas (screentones), líneas cinéticas y onomatopeyas",
            promptKeywords = "black and white manga page art, authentic screentone dot patterns, heavy dynamic ink hatching, bold speed lines, Japanese sound effect kanji onomatopoeia, stark monochrome contrast",
            category = "Manga",
            signatureEffect = "Tramas Halftone"
        ),
        AnimeStyleInfo(
            id = "cyberpunk",
            name = "Cyberpunk Trigger",
            japaneseName = "サイバーパンク",
            era = "Neo-Tokyo",
            description = "Colores neón ultra-saturados, poses cinéticas distorsionadas y aberración cromática",
            promptKeywords = "Studio Trigger Cyberpunk Edgerunners style, hyper-saturated neon magenta and cyan, chromatic aberration, kinetic exaggerated perspective, explosive glowing laser smoke",
            category = "Acción",
            signatureEffect = "Glow Neón"
        ),
        AnimeStyleInfo(
            id = "chibi",
            name = "Chibi / Super Deformed",
            japaneseName = "ちびキャラ",
            era = "Kawaii",
            description = "Proporciones miniatura 2:1, ojos gigantes expresivos y ternura cómica",
            promptKeywords = "chibi anime character, super-deformed 2-head proportion, giant expressive sparkling eyes, cute kawaii blush, comedic sweatdrop, adorable miniature style",
            category = "Kawaii",
            signatureEffect = "Brillos Kawaii"
        ),
        AnimeStyleInfo(
            id = "watercolor",
            name = "Acuarela & Sumi-e",
            japaneseName = "水彩・墨絵",
            era = "Tradicional",
            description = "Pinceladas tradicionales japonesas, tinta fluida y lavados suaves de acuarela",
            promptKeywords = "traditional Japanese sumi-e ink wash painting combined with delicate anime character, fluid brushstrokes, raw paper texture, poetic minimalist aesthetic",
            category = "Tradicional",
            signatureEffect = "Pincel Sumi-e"
        )
    )
}
