package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PortalNavy
import com.example.viewmodel.OnlineCenterViewModel

@Composable
fun AuthScreen(viewModel: OnlineCenterViewModel) {
  val context = LocalContext.current
  val activity = context as Activity

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(PortalNavy, Color(0xFF1A237E))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Surface(
        modifier = Modifier
          .size(120.dp)
          .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        tonalElevation = 8.dp
      ) {
        Image(
          painter = painterResource(id = R.drawable.ayesha_logo),
          contentDescription = "Logo",
          modifier = Modifier.padding(16.dp),
          contentScale = ContentScale.Fit
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "Ayesha Online Center",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Text(
        text = "डिजिटल सेवा केंद्र में आपका स्वागत है",
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
      )

      Spacer(modifier = Modifier.height(48.dp))

      Button(
        onClick = { viewModel.onGoogleSignInClicked(activity) },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.White,
          contentColor = PortalNavy
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          // In a real app, you'd add the Google Logo here
          Text(
            text = "Google के साथ साइन-इन करें",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "सुरक्षित लॉगिन के लिए अपने Google अकाउंट का उपयोग करें।",
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
      )
    }
  }
}
