package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic Windows Phone Metro Theme Colors
val MetroBlack = Color(0xFF000000)
val MetroDarkGray = Color(0xFF1F1F1F)
val MetroMediumGray = Color(0xFF2D2D2D)
val MetroLightGray = Color(0xFF767676)
val MetroSubtleGray = Color(0xFFAAAAAA)
val MetroWhite = Color(0xFFFFFFFF)
val MetroOffWhite = Color(0xFFF3F3F3)

// Windows Phone 7 & 8 Signature Accent Palette
enum class MetroAccent(val displayName: String, val color: Color) {
    COBALT("Cobalt", Color(0xFF0078D7)),
    CYAN("Cyan", Color(0xFF1BA1E2)),
    TEAL("Teal", Color(0xFF00ABA9)),
    EMERALD("Emerald", Color(0xFF008A00)),
    LIME("Lime", Color(0xFFA4C400)),
    MANGO("Mango", Color(0xFFF09609)),
    ORANGE("Orange", Color(0xFFFA6800)),
    CRIMSON("Crimson", Color(0xFFE51400)),
    MAGENTA("Magenta", Color(0xFFD80073)),
    PURPLE("Purple", Color(0xFF6A00FF)),
    STEEL("Steel", Color(0xFF647687)),
    TAUPE("Taupe", Color(0xFF76608A))
}
