package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Renders a crisp QR Code matrix directly on Jetpack Compose Canvas.
 * Generates official finder patterns in 3 corners and deterministic payload modules.
 */
@Composable
fun QrCodeView(
  content: String,
  modifier: Modifier = Modifier,
  size: Dp = 160.dp,
  primaryColor: Color = Color(0xFF0F172A),
  backgroundColor: Color = Color.White
) {
  val matrixSize = 25
  val matrix = remember(content) {
    generateQrMatrix(content, matrixSize)
  }

  Box(
    modifier = modifier
      .size(size)
      .background(backgroundColor, RoundedCornerShape(8.dp))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size - 16.dp)) {
      val cellSize = this.size.width / matrixSize

      for (row in 0 until matrixSize) {
        for (col in 0 until matrixSize) {
          if (matrix[row][col]) {
            drawRect(
              color = primaryColor,
              topLeft = Offset(col * cellSize, row * cellSize),
              size = Size(cellSize, cellSize)
            )
          }
        }
      }
    }
  }
}

private fun generateQrMatrix(content: String, size: Int): Array<BooleanArray> {
  val matrix = Array(size) { BooleanArray(size) { false } }

  // Draw 3 Corner Finder Patterns (7x7 outer box + 3x3 inner square)
  fun drawFinder(startRow: Int, startCol: Int) {
    for (r in 0 until 7) {
      for (c in 0 until 7) {
        val isOuter = r == 0 || r == 6 || c == 0 || c == 6
        val isInner = r in 2..4 && c in 2..4
        matrix[startRow + r][startCol + c] = isOuter || isInner
      }
    }
  }

  drawFinder(0, 0)
  drawFinder(0, size - 7)
  drawFinder(size - 7, 0)

  // Timing lines
  for (i in 7 until size - 7) {
    matrix[6][i] = (i % 2 == 0)
    matrix[i][6] = (i % 2 == 0)
  }

  // Data modules derived deterministically from content hash & chars
  val hash = abs(content.hashCode())
  var bitIndex = 0
  for (r in 0 until size) {
    for (c in 0 until size) {
      // Skip finder zones
      val inTopLeft = r < 8 && c < 8
      val inTopRight = r < 8 && c >= size - 8
      val inBottomLeft = r >= size - 8 && c < 8
      val inTiming = (r == 6 || c == 6)

      if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
        val charVal = if (content.isNotEmpty()) content[bitIndex % content.length].code else 42
        val pseudoBit = ((hash shr ((bitIndex * 3) % 28)) xor (charVal * (r + 1) * (c + 1))) % 2 == 0
        matrix[r][c] = pseudoBit
        bitIndex++
      }
    }
  }

  return matrix
}
