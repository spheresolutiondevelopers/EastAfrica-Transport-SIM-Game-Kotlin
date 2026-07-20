package com.transportsim.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.transportsim.app.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val RajdhaniFont = GoogleFont("Rajdhani")
val OrbitronFont = GoogleFont("Orbitron")
val ShareTechMonoFont = GoogleFont("Share Tech Mono")

val RajdhaniFamily = FontFamily(
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Bold)
)

val OrbitronFamily = FontFamily(
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Black)
)

val ShareTechMonoFamily = FontFamily(
    Font(googleFont = ShareTechMonoFont, fontProvider = provider, weight = FontWeight.Normal)
)

val Typography = Typography(
    // Headlines use Orbitron
    displayLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 2.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 1.sp
    ),
    
    // Titles and Body use Rajdhani
    titleLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    
    // Labels and Monospace sections use Share Tech Mono
    labelLarge = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontSize = 8.sp,
        letterSpacing = 0.5.sp
    )
)
