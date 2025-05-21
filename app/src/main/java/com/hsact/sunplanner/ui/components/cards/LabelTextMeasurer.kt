package com.hsact.sunplanner.ui.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun totalTextWidth(texts: List<String>, textStyle: TextStyle): Double {
    val textMeasurer = rememberTextMeasurer()

    val joinedText = texts.joinToString(separator = " ")

    val layoutResult = textMeasurer.measure(
        text = AnnotatedString(joinedText),
        style = textStyle
    )
    return layoutResult.getLineRight(0).toDouble() * 1.2
}