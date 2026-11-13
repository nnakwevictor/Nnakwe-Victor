package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * PROJECT X — GLOBAL PROPERTY GROUP
 * Formal Premium Architectural Color Palette
 *
 * Core Tonal Structure:
 * - Primary: Deep Navy (Formal, institutional, authoritative)
 * - Secondary: Charcoal (Architectural slate & basalt stone)
 * - Accents: Champagne Gold (Refined metallic opulence, high-net-worth tier)
 */

// ==========================================
// 1. PRIMARY: Deep Navy
// ==========================================
val ProjectXNavyDeep = Color(0xFF070D18)     // Obsidian midnight navy (header background, hero container)
val ProjectXNavy = Color(0xFF0B1426)         // Primary brand deep navy (buttons, prominent typography)
val ProjectXNavyLight = Color(0xFF142038)    // Elevated navy cards, containers, tab indicators
val ProjectXNavySubtle = Color(0xFF1E2E4E)   // Navy borders, secondary borders, interactive states

// ==========================================
// 2. SECONDARY: Charcoal
// ==========================================
val ProjectXCharcoalDark = Color(0xFF16191F)   // Deep charcoal basalt (footer surfaces, dark pills)
val ProjectXCharcoal = Color(0xFF23272F)       // Executive charcoal (secondary buttons, badges, chips)
val ProjectXCharcoalLight = Color(0xFF383E4A)  // Slate charcoal (card outlines, secondary text elements)
val ProjectXCharcoalMuted = Color(0xFF555D6C)  // Charcoal muted text & icons

// ==========================================
// 3. ACCENTS: Champagne Gold
// ==========================================
val ProjectXChampagneGold = Color(0xFFD4AF37)           // Metallic champagne gold (key badges, active icons, price accents)
val ProjectXChampagneGoldLight = Color(0xFFE8D39E)      // Luminous champagne highlight
val ProjectXChampagneGoldDark = Color(0xFFA6852A)       // Burnished antique gold (hover, pressed, border accents)
val ProjectXChampagneGoldMuted = Color(0xFFF9F5EC)      // Soft champagne tint (pill background, luxury badge surface)
val ProjectXChampagneGoldContainer = Color(0xFF2C2414)  // Deep bronze-gold container for dark mode

// ==========================================
// 4. NEUTRALS & ARCHITECTURAL SURFACES
// ==========================================
val ProjectXSand = Color(0xFFFBFBF9)           // Warm editorial alabaster
val ProjectXSurface = Color(0xFFFFFFFF)        // Crisp pure white surface
val ProjectXBackground = Color(0xFFF8F8F6)     // Institutional soft-white background
val ProjectXSurfaceVariant = Color(0xFFF2F3F5) // Charcoal-tinted off-white for subtle section breaks
val ProjectXBorder = Color(0xFFE2E4E8)         // Architectural hairline border
val ProjectXGreyLight = Color(0xFFF3F4F6)      // Neutral input surface
val ProjectXTextPrimary = Color(0xFF111827)    // Dark charcoal primary typography (high contrast)
val ProjectXTextSecondary = Color(0xFF4B5563)  // Subdued body text
val ProjectXTextMuted = Color(0xFF9CA3AF)      // Captions & placeholders

// Dark Mode Surfaces
val ProjectXDarkSurface = Color(0xFF0D131F)
val ProjectXDarkBackground = Color(0xFF070D18)
val ProjectXDarkBorder = Color(0xFF1C273A)

// Status Indicators
val StatusSuccess = Color(0xFF15803D)
val StatusWarning = Color(0xFFB45309)
val StatusError = Color(0xFFB91C1C)
val StatusInfo = Color(0xFF0369A1)

// ==========================================
// BACKWARD-COMPATIBILITY ALIASES
// (Ensures all existing components immediately inherit the upgraded palette)
// ==========================================
val HavenNavyDark = ProjectXNavyDeep
val HavenNavy = ProjectXNavy
val HavenNavyLight = ProjectXNavyLight
val HavenNavySubtle = ProjectXNavySubtle

val HavenGold = ProjectXChampagneGold
val HavenGoldLight = ProjectXChampagneGoldLight
val HavenGoldDark = ProjectXChampagneGoldDark
val HavenGoldMuted = ProjectXChampagneGoldMuted
val HavenGoldContainer = ProjectXChampagneGoldContainer

val HavenSand = ProjectXSand
val HavenSurface = ProjectXSurface
val HavenBackground = ProjectXBackground
val HavenSurfaceVariant = ProjectXSurfaceVariant

val HavenGreyBorder = ProjectXBorder
val HavenGreyLight = ProjectXGreyLight
val HavenGreyText = ProjectXTextSecondary
val HavenGreyMuted = ProjectXTextMuted
val HavenGreyDark = ProjectXCharcoal

val HavenDarkSurface = ProjectXDarkSurface
val HavenDarkBackground = ProjectXDarkBackground
