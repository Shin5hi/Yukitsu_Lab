# 🌸 Yukitsu_Lab (雪津スタジオ)
### Suite Integral de Creación de Animación Japonesa con IA

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Database-Cloud%20Firestore-orange.svg)](https://firebase.google.com)
[![Gemini](https://img.shields.io/badge/AI-Gemini%203.5%20%7C%20Veo%203.1-4285F4.svg)](https://ai.google.dev)

**Yukitsu_Lab** es una aplicación Android nativa diseñada para creadores, ilustradores y aficionados al anime. Combina la potencia de la inteligencia artificial generativa con el conocimiento profundo de las demografías, técnicas y épocas de la animación japonesa.

---

## 🚀 Características Principales

### 1. 🎭 Consistencia de Personaje (ComfyUI Engine)
- **Pipeline de Nodos Integrado:** Bloquea la fisionomía facial, tipo de cabello y vestuario característico a través de múltiples escenas.
- **Configuración de Hiperparámetros:** Ponderación IP-Adapter FaceID, Face Detailer Denoise, ControlNet OpenPose y Bloqueo de Estilo Anime.
- **Hoja de Giro (Turnaround Dataset):** Visualización en 5 ángulos (Vista Frontal 0°, Ángulo 3/4 45°, Perfil 90°, Pose de Acción Dinámica y Expresión Emocional).
- **Pruebas de Verificación Multi-Escena:** Comprobación del personaje en diferentes entornos (lluvia nocturna, santuario tradicional, combate épico).
- **Inyección Automática:** Carga el token de anclaje de identidad (`<char_anchor:...>`) directamente en el prompt del estudio.

### 2. 🎨 Catálogo Completo de Estilos y Demografías
- **Demografías:** Shōnen, Seinen, Kodomo, Shōjo, Josei, Mecha / Sci-Fi, Isekai / Fantasía.
- **Estilos Visuales:** Clásico Cel 90s, Digital Moderno (Ufotable/Mappa), Acuarela Ghibli, Manga Ink entintado blanco y negro, Cyberpunk Synthwave, Trigger High-Octane, Kyoto Ethereal y Chibi / Kodomo kawaii.
- **Plantillas Predefinidas:** Ajustes optimizados de iluminación, composición y resolución (1K, 2K, 4K) con relaciones de aspecto variadas (1:1, 16:9, 9:16, 21:9, etc.).

### 3. 🧪 Laboratorio LoRA & Cuadernos con Memoria
- **Entrenamiento de LoRAs:** Mezcla y ponderación de pesos entre diferentes estilos visuales anime.
- **Cuadernos de Bocetos:** Almacenamiento organizado por carpetas temáticas y etiquetas en Cloud Firestore.
- **Memoria Multihilo (Thread Memory):** La IA recuerda las descripciones y decisiones de turnos anteriores dentro del mismo hilo para mantener coherencia narrativa.

### 4. 🎙️ Micrófono con Memoria de Contexto (Gemini AI)
- Detección de dictado por voz y corrección inteligente de palabras malinterpretadas basándose en el contexto del anime y la cultura otaku sin necesidad de repetir.

### 5. 🎬 Storyboard y Cortos de Animación por Frames (1 - 2 min)
- Generación de videos de hasta 30 segundos (Veo 3.1).
- Secuenciador de escenas con planos de cámara cinematográficos, diálogos originales en japonés kanji/hiragana, transcripción en romaji y traducción.

### 6. 🖌️ Live CSS Studio
- Personalizador en tiempo real de la interfaz y componentes de la app mediante declaraciones de estilo CSS en vivo.
- Presets incluidos: *Cyberpunk Neo-Tokyo*, *Ghibli Nostalgia Gouache*, *Shonen Battle Crimson* y *Manga Noir Monochrome*.

### 7. 🌐 Galería Comunitaria y Remix
- Publicación de imágenes, videos y GIFs en la galería global.
- Sistema de votación (Likes) y función de **Remix de Prompt** directo al estudio.

---

## 🛠️ Arquitectura Técnica

- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose con Material Design 3
- **Patrón de Diseño:** Model-View-ViewModel (MVVM) con StateFlow y Corrutinas
- **Persistencia en la Nube:** Firebase Cloud Firestore con reglas de seguridad granulares
- **Autenticación:** Firebase Auth + Jetpack Credential Manager
- **Carga de Imágenes:** Coil Compose
- **Motor de IA:** Google Gemini REST API (v1beta)

---

## ⚙️ Configuración y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/TU_USUARIO/yukitsu-studio.git
   cd yukitsu-studio
   ```
2. **Configuración de Variables de Entorno:**
   Copia el archivo `.env.example` como `.env` y configura tu clave de Gemini:
   ```env
   GEMINI_API_KEY=tu_clave_de_gemini_api
   ```
3. **Compilar e Instalar:**
   Abre el proyecto en **Android Studio** (Koala o superior) y sincroniza los archivos Gradle.
   Ejecuta la app en un dispositivo Android o emulador con soporte de API 26+.

---

## 📄 Licencia

Distribuido bajo la Licencia MIT. Consulta el archivo `LICENSE` para más información.
