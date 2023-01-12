package com.sayler666.gina.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sayler666.gina.R

val RobotoSlabThin = FontFamily(Font(R.font.roboto_slab_thin))
val RobotoSlabLight = FontFamily(Font(R.font.roboto_slab_light))
val RobotoSlabRegular = FontFamily(Font(R.font.roboto_slab_regular))
val RobotoSlabMedium = FontFamily(Font(R.font.roboto_slab_medium))

val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoSlabThin,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.3.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoSlabLight,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 21.0.sp,
        letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 33.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = RobotoSlabLight,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoSlabLight,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
    ),
)
