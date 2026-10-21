package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.viewmodel.OnlineCenterViewModel

fun Context.findActivity(): Activity? {
  var ctx = this
  while (ctx is android.content.ContextWrapper) {
    if (ctx is Activity) return ctx
    ctx = ctx.baseContext
  }
  return null
}

@Composable
fun OperatorLoginDialog(
  viewModel: OnlineCenterViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val activity = context.findActivity()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .testTag("operator_login_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFFEF3C7), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "संचालक प्रवेश",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "🔒 केवल अधिकृत संचालक हेतु",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "यह सेक्शन केवल आयशा ऑनलाइन सेवा केंद्र के संचालक (Admin) के लिए है। केवल ayeshaonlinecenter@gmail.com से ही संचालक डैशबोर्ड खुलेगा। यदि आप ग्राहक हैं, तो आप सीधे होम पेज का उपयोग कर सकते हैं।",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "💡 ग्राहक बिना लॉगिन किए सीधे होम पेज से सेवाएं ब्राउज व फॉर्म भर सकते हैं।",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F52BA)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (activity != null) {
              viewModel.onGoogleSignInClicked(activity)
              onDismiss()
            } else {
              Toast.makeText(context, "Activity उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = PortalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("operator_google_signin_button")
        ) {
          Icon(Icons.Default.Storefront, contentDescription = null, tint = SaffronGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Google से संचालक लॉगिन करें",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onDismiss) {
          Text("रद्द करें (अतिथि मोड में रहें)", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
