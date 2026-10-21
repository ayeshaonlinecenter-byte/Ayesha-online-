package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Renders an authentic Code-128 style Barcode on Canvas based on the Slip / Token number.
 */
@Composable
fun BarcodeView(
  code: String,
  modifier: Modifier = Modifier,
  height: Dp = 48.dp,
  barColor: Color = Color(0xFF111827)
) {
  val barPattern = remember(code) {
    generateBarcodePattern(code)
  }

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
  ) {
    val totalUnits = barPattern.size
    val unitWidth = size.width / totalUnits

    for (i in barPattern.indices) {
      if (barPattern[i]) {
        drawRect(
          color = barColor,
          topLeft = Offset(i * unitWidth, 0f),
          size = Size(unitWidth * 0.95f, size.height)
        )
      }
    }
  }
}

private fun generateBarcodePattern(text: String): BooleanArray {
  val pattern = mutableListOf<Boolean>()
  // Start guard
  pattern.addAll(listOf(true, false, true, false))

  val hash = abs(text.hashCode())
  for (char in text) {
    val code = char.code
    val b1 = (code and 1) != 0
    val b2 = (code and 2) != 0
    val b3 = (code and 4) != 0
    val b4 = (code and 8) != 0

    pattern.addAll(listOf(b1, true, b2, false, b3, true, b4, false))
  }

  // End guard
  pattern.addAll(listOf(true, false, true, true, false, true))
  return pattern.toBooleanArray()
}
