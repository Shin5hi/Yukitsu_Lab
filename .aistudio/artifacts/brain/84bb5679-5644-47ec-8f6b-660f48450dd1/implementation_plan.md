# AnimeGen Studio (アニメスタジオ) — AI Anime Creation Suite

An AI-powered creative studio designed for generating images, videos (Veo 3), and animated GIFs in authentic Japanese animation styles. Filter by demographics (Shonen, Seinen, Kodomo, Shojo, Josei) and aesthetic eras (90s Cel, Modern Digital, Manga Ink, Ghibli-esque), with intuitive editing overlays (speed lines, manga screen tones, anime subtitles, energy auras) and a live CSS/style customizer for interface themes and anime showcase cards, backed by Firebase Auth and Firestore.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following directions were confirmed during interactive clarification and will guide the implementation:

- **Primary Creative Workflow**: Direct anime prompt studio with instant genre/demographic filters (Shonen, Seinen, Kodomo, Shojo, Josei) and aesthetic style presets (90s Retro Cel, Modern Digital Ufotable, Manga Halftone Ink, Ghibli Watercolor, Cyberpunk Mecha).
- **Custom CSS & Styling Scope**: Live CSS and style editor allowing users to customize both the app interface theme (colors, borders, glowing shadows, card styles) and preview/export custom styled anime cards using responsive CSS-driven styling.
- **Anime Style Breadth**: Comprehensive selection covering classic 90s hand-painted cel animation, modern cinematic digital animation, and traditional black & white manga screen tones.
- **Firebase & Cloud Persistence**: Integrated with Google Sign-In (`CredentialManager`) and Cloud Firestore for saving creations, collections, custom CSS presets, and generation history.

---

## 1. Overview & Core Concept

- **What It Does**: Users enter simple text descriptions (e.g., *"un gato ninja bajo la lluvia de neón"* or *"guerrera con espada de llamas"*), select an anime genre/demographic (Shonen, Seinen, Kodomo, Shojo, Josei) and animation aesthetic (90s Cel, Modern Digital, Manga Ink, Ghibli, Cyberpunk), and generate high-fidelity anime images, Veo animated videos, or looping GIFs. An interactive editing suite lets users apply manga speed lines, energy VFX, subtitles, and halftone tones, while a CSS customizer allows full personal styling of the app interface and anime showcase cards.
- **Target Audience / Persona**: Anime enthusiasts, digital artists, mangakas, and content creators looking for authentic Japanese animation aesthetics without complex prompt engineering.
- **Key Value**: Democratizes authentic anime art generation with built-in Japanese artistic knowledge, genre categorization, one-touch prompt optimization via Gemini intelligence, intuitive anime-specific post-processing, and a personalized CSS theme engine.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Authentication Gate**:
   - Welcome screen with modern anime-inspired branding ("AnimeGen Studio / アニメスタジオ").
   - Frictionless Google Sign-In button powered by Jetpack Credential Manager.
2. **Creation Studio (Main Tab)**:
   - **Simple Text Input**: Natural language prompt input with "Otaku AI Enhancer" (Gemini expands simple phrases into director-level anime prompts).
   - **Genre & Demographic Filter Bar**: Segmented chips for `Shonen` (Action/Aura), `Seinen` (Gritty/Noir/Complex), `Kodomo` (Cute/Chibi/Playful), `Shojo` (Pastel/Sparkle), `Josei` (Grown-up/Atmospheric), and `Cyberpunk / Mecha`.
   - **Aesthetic Eras & Styles**: Horizontal visual carousel (`90s Retro Cel`, `Modern Digital Composited`, `Manga Ink & Screen-tone`, `Hand-painted Ghibli`, `Makoto Shinkai Skies`).
   - **Generation Mode Selector**: `Image` (aspect ratios 1:1, 9:16, 16:9, 4:3, 2:3, 21:9 with 1K/2K/4K resolution settings), `Video` (Veo 3.1 generation in 16:9 or 9:16), and `Animated GIF` (looping animated frames).
   - **Generate Action**: One-tap generation with anime-themed loading progress indicators and cancel safety.
3. **Anime FX & Editing Studio**:
   - **Manga Screen-tones & Halftones**: Authentic dot patterns and cross-hatching overlays.
   - **Action Speed Lines**: Radial and linear focus lines for dramatic impact.
   - **Aura & VFX Overlays**: Super Saiyan / Ki flames, lightning sparks, cherry blossom petals (sakura), and glowing lens flares.
   - **Anime Subtitle Generator**: Retro yellow/white font with black border and Japanese translation bar (e.g., 「お前はもう死んでいる」).
   - **Crop & Aspect Ratio Re-framing**.
4. **Gallery & Cloud Collection**:
   - Real-time Firestore synchronized grid of generated artworks and videos.
   - Filter by genre, favorite flag, and format (Image, Video, GIF).
   - Detail view with prompt info, style tags, download, and share actions.
5. **Live CSS & Interface Customizer**:
   - Live interactive CSS code editor and preset customizer.
   - Real-time preview of customized Anime Card elements (custom borders, neon glow box-shadow, scanlines, glassmorphism blur, custom font scale).
   - Apply custom CSS theme to the application UI dynamically (primary accent, glowing highlights, card elevation, border radius).
   - Export CSS snippet alongside the artwork for web and social embeds.
6. **AI Anime Vision & Style Analyzer**:
   - Upload any image to analyze its anime demographic, color harmony, line-weight, and auto-generate similar prompts.

### Visual Identity & Theme

- **Aesthetic Direction**: Neo-Tokyo Dark Luxury Anime Studio — sleek obsidian backdrops, electric neon accents (Cyberpunk Cyan, Shonen Crimson, Neon Violet), razor-sharp card contours, and high-legibility typography.
- **Color Palette & Design Tokens**:
  - `Background`: Deep Obsidian (`#0C0D14`) and Surface Charcoal (`#151722`)
  - `Primary / Shonen Crimson`: `#FF2E63` (Dynamic energy and action)
  - `Secondary / Cyber Cyan`: `#00F0FF` (Modern digital, sci-fi)
  - `Tertiary / Sakura Gold`: `#FFD166` (Classic anime highlights)
  - `Surface Variant / Card`: `#1D2030` with subtle border stroke `#2A2E45`
  - `Text Primary`: Crisp White (`#F8F9FA`), `Text Secondary`: Muted Silver (`#9AA0A6`)
- **Typography & Motion**:
  - Expressive bold display headers with Japanese sub-labels.
  - Smooth spring transitions (`spring(dampingRatio = 0.8f)`), subtle shimmer on generating states, and tactile M3 ripples.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct REST API for Gemini & Veo with Coroutine Pipeline**
  - *Chosen Approach*: Direct Retrofit REST integration with `gemini-3.5-flash` / `gemini-3.1-pro-preview` for prompt intelligence & analysis, `gemini-2.5-flash-image` / `gemini-nano-banana-2.1` / `gemini-3-pro-image-preview` for images, and `veo-3.1-fast-generate-preview` for video generation.
  - *Why*: Allows fine-grained control over prompt styling parameters, aspect ratios, image size parameters (1K, 2K, 4K), and Veo polling status.
  - *Alternatives Considered*: Firebase AI logic only; however, Veo video generation and granular aspect ratio controls are cleanly supported via the specialized Gemini/Veo endpoints.
- **Decision 2: Live CSS Engine with Compose Bridge**
  - *Chosen Approach*: A bidirectional CSS parser/renderer that parses CSS declarations (`color`, `background`, `border-radius`, `box-shadow`, `border`, `font-family`, `opacity`, `filter`, `backdrop-filter`) and dynamically maps them to Jetpack Compose `Modifier` attributes, plus a live CSS code editor tab with syntax highlighting and exportable HTML/CSS code.
  - *Why*: Gives users full creative control over both the app's visual style and the presentation of their anime artwork cards.
- **Decision 3: Firebase Google Sign-In & Firestore Persistence**
  - *Chosen Approach*: Follow strict zero-trust Firestore architecture with Jetpack `CredentialManager` (`GetSignInWithGoogleOption`), `firebase-blueprint.json`, hardened `firestore.rules`, and verified local testing before rules deployment.
  - *Why*: Meets the mandatory user authorization and cloud data requirements without unauthenticated or mock fallbacks.

---

## 4. Technical Architecture & Data Strategy

### Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                          AnimeGen Studio UI                            │
│                                                                        │
│  ┌────────────────────┐  ┌─────────────────────┐  ┌─────────────────┐  │
│  │  Creation Studio   │  │  Manga/FX Editor    │  │ CSS Customizer  │  │
│  │ (Genres & Prompts) │  │ (Speedlines & Sub)  │  │ (Live App Theme)│  │
│  └─────────┬──────────┘  └──────────┬──────────┘  └────────┬────────┘  │
│            │                        │                      │           │
│  ┌─────────▼────────────────────────▼──────────────────────▼────────┐  │
│  │                 MainViewModel & StudioState                      │  │
│  └─────────┬────────────────────────┬──────────────────────┬────────┘  │
└────────────┼────────────────────────┼──────────────────────┼───────────┘
             │                        │                      │
             ▼                        ▼                      ▼
┌────────────────────────┐  ┌──────────────────┐  ┌─────────────────────┐
│  Gemini & Veo Service  │  │ CSS Style Parser │  │ Firestore Repository│
│  - Text-to-Image       │  │ - Color & Shadows│  │ - Users & Creations │
│  - Prompt Enhancer     │  │ - Borders & Radii│  │ - Saved Themes      │
│  - Veo 3 Video Gen     │  │ - Compose Tokens │  │ - Multi-User Rules  │
│  - Vision Analyzer     │  │ - HTML/CSS Export│  │                     │
└────────────────────────┘  └──────────────────┘  └──────────┬──────────┘
                                                             │
                                                  ┌──────────▼──────────┐
                                                  │ Firebase Backend    │
                                                  │ - Auth (Google ID)  │
                                                  │ - Cloud Firestore   │
                                                  └─────────────────────┘
```

### Data Models & Firestore Collections

- **`/users/{userId}/creations/{creationId}`**:
  - `id: String` (Document ID)
  - `userId: String` (Google Auth UID)
  - `prompt: String` (User prompt)
  - `enhancedPrompt: String` (Expanded anime director prompt)
  - `genre: String` (e.g. `shonen`, `seinen`, `kodomo`, `shojo`, `josei`)
  - `style: String` (e.g. `retro_90s`, `modern_digital`, `manga_ink`, `ghibli`)
  - `mediaType: String` (`image`, `video`, `gif`)
  - `mediaUrl: String` (Data URI / URL)
  - `aspectRatio: String` (`1:1`, `16:9`, `9:16`, `4:3`, etc.)
  - `editorSettings: Map<String, Any>` (Applied speedlines, subtitles, auras)
  - `customCss: String` (User-defined CSS styling string)
  - `isFavorite: Boolean`
  - `createdAt: Timestamp`
  - `updatedAt: Timestamp`
- **`/users/{userId}/themes/{themeId}`**:
  - `id: String`
  - `userId: String`
  - `name: String` (e.g. "Cyberpunk Neon", "90s Retro CRT", "Shonen Gold")
  - `cssCode: String`
  - `isCurrentActive: Boolean`
  - `createdAt: Timestamp`
  - `updatedAt: Timestamp`

### Key Interactive Flows & State Lifecycle

1. **Prompt Entry & Enhancement**:
   - User types "samurai cyberpunk".
   - Taps "Enhance with Gemini" -> `gemini-3.5-flash` infuses the prompt with chosen genre keywords (e.g., for Seinen: "Gritty dark alleys, high contrast lighting, intricate cybernetic Katana, rain-slicked pavement, neo-noir anime aesthetics").
2. **Generation Dispatch**:
   - `Image`: Calls `gemini-nano-banana-2.1` or `gemini-3-pro-image-preview` with selected aspect ratio and resolution.
   - `Video`: Calls `veo-3.1-fast-generate-preview` with prompt and aspect ratio (`16:9` or `9:16`).
   - `GIF`: Generates animated sequence frames with looping Compose animation canvas.
3. **Manga/Anime Post-Editing**:
   - Users overlay live speedlines, dynamic action flares, and subtitles with bilingual text.
   - Instant live preview updates in Compose `Canvas` and `Box` composition.
4. **Live CSS Engine**:
   - Interactive CSS code input parsing custom properties (`border-radius`, `box-shadow`, `border`, `background`, `accent-color`).
   - Dynamically updates the Anime Art Card presentation and overall app theme color tokens.
5. **Save & Cloud Sync**:
   - Pushes creation document to Firestore under `/users/{uid}/creations` with server timestamps.
