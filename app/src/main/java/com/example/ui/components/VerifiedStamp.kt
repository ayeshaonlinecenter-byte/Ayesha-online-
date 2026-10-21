package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StampGreen

@Composable
fun VerifiedStamp(
  modifier: Modifier = Modifier,
  textTop: String = "AYESHA ONLINE KENDRA",
  textMain: String = "PAID & VERIFIED",
  textBottom: String = "CSC DIGITAL SEVA",
  stampColor: Color = StampGreen,
  rotationDegrees: Float = -12f
) {
  Box(
    modifier = modifier
      .rotate(rotationDegrees)
      .size(118.dp)
      .border(2.5.dp, stampColor.copy(alpha = 0.85f), CircleShape)
      .padding(4.dp)
      .border(1.dp, stampColor.copy(alpha = 0.65f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(6.dp)
    ) {
      Text(
        text = textTop,
        fontSize = 7.sp,
        fontWeight = FontWeight.Bold,
        color = stampColor.copy(alpha = 0.85f),
        textAlign = TextAlign.Center,
        letterSpacing = 0.5.sp
      )

      Box(
        modifier = Modifier
          .padding(vertical = 2.dp)
          .border(1.dp, stampColor.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text(
          text = textMain,
          fontSize = 9.sp,
          fontWeight = FontWeight.Black,
          color = stampColor,
          textAlign = TextAlign.Center,
          letterSpacing = 0.5.sp
        )
      }

      Text(
        text = textBottom,
        fontSize = 6.5.sp,
        fontWeight = FontWeight.SemiBold,
        color = stampColor.copy(alpha = 0.85f),
        textAlign = TextAlign.Center,
        letterSpacing = 0.5.sp
      )
    }
  }
}
