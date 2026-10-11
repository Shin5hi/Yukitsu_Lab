package com.example.data.model

data class AnimeStoryboardScene(
    val sceneNumber: Int,
    val title: String,
    val promptDescription: String,
    val durationSeconds: Int = 5,
    val japaneseDialogue: String = "",
    val romajiSubtitle: String = "",
    val spanishTranslation: String = "",
    val cameraShot: String = "Plano Medio", // Plano General, Primer Plano, Acción Dinámica, Plano Detalle
    val imageUrl: String = ""
)

data class AnimeShortFilm(
    val id: String = "",
    val title: String = "Corto Anime Yukitsu",
    val genre: String = "shonen",
    val style: String = "modern_digital",
    val totalDurationSeconds: Int = 30,
    val scenes: List<AnimeStoryboardScene> = listOf(
        AnimeStoryboardScene(
            sceneNumber = 1,
            title = "Apertura / Establecimiento",
            promptDescription = "Un templo místico envuelto en niebla al amanecer en lo alto de una montaña",
            durationSeconds = 6,
            japaneseDialogue = "「静寂が破られる時が来た…」",
            romajiSubtitle = "Shijima ga yaburareru toki ga kita...",
            spanishTranslation = "Ha llegado la hora en que el silencio se quebrante...",
            cameraShot = "Plano General Panorámico",
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800&auto=format&fit=crop"
        ),
        AnimeStoryboardScene(
            sceneNumber = 2,
            title = "Encuentro de Personajes",
            promptDescription = "Primer plano de un guerrero desenvainando una katana con mirada decidida",
            durationSeconds = 8,
            japaneseDialogue = "「貴様の覚悟を、ここで見せてもらおう！」",
            romajiSubtitle = "Kisama no kakugo o, koko de misete moraou!",
            spanishTranslation = "¡Demuéstrame aquí tu determinación!",
            cameraShot = "Primer Plano / Close-up",
            imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop"
        ),
        AnimeStoryboardScene(
            sceneNumber = 3,
            title = "Clímax de Acción",
            promptDescription = "Choque explosivo de dos energías elemental azul y carmesí con destellos de partículas",
            durationSeconds = 10,
            japaneseDialogue = "「これが私の、全力の刃だーーっ！」",
            romajiSubtitle = "Kore ga watashi no, zenryoku no yaiba daaa!",
            spanishTranslation = "¡Esta es mi espada con todo mi poder!",
            cameraShot = "Ángulo Contrapicado Dinámico",
            imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop"
        ),
        AnimeStoryboardScene(
            sceneNumber = 4,
            title = "Cierre Emotivo",
            promptDescription = "Pétalos de cerezo cayendo mientras el guerrero enfunda su espada al atardecer dorado",
            durationSeconds = 6,
            japaneseDialogue = "「戦いは終わり、風が吹き抜ける。」",
            romajiSubtitle = "Tatakai wa owari, kaze ga fukinukeru.",
            spanishTranslation = "La batalla ha terminado y el viento sigue soplando.",
            cameraShot = "Plano Detalle / Silueta",
            imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop"
        )
    )
)
