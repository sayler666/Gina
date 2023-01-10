package com.sayler666.gina.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sayler666.gina.R

val RobotoSlabRegular = FontFamily(Font(R.font.roboto_slab_regular))

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoSlabRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    ),
)
