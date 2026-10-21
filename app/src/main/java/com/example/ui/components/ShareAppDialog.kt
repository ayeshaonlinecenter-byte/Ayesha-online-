package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.OnlineCenterViewModel

@Composable
fun ShareAppDialog(
  viewModel: OnlineCenterViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("share_app_dialog"),
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
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFE0E7FF), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = PortalNavy,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ऐप शेयर करें (Share App)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PortalNavy
              )
              Text(
                text = "दोस्तों व परिजनों को भेजें",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // App Features Summary Box
        Surface(
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "🏛️ आयशा ऑनलाइन सेवा केंद्र ऐप",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "घर बैठे सभी 22 सरकारी व ऑनलाइन सेवाएं, 10% ऑनलाइन डिस्काउंट, फ्री आयुष्मान व ई-श्रम, और तुरंत डिजिटल पावती रसीद प्राप्त करें।",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF334155),
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // QR Code for direct in-person scanning
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          QrCodeView(
            content = viewModel.APP_SHARE_URL,
            size = 140.dp,
            primaryColor = PortalNavy
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "कैमरे से स्कैन करके सीधे ऐप खोलें",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Share Buttons
        // 1. WhatsApp Direct Share Button
        Button(
          onClick = {
            viewModel.shareApp(context, viaWhatsApp = true)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF25D366),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("whatsapp_share_app_btn")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "व्हाट्सएप पर शेयर करें (WhatsApp)",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. All Apps Share Sheet Button
        Button(
          onClick = {
            viewModel.shareApp(context, viaWhatsApp = false)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = PortalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("system_share_app_btn")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "अन्य सभी ऐप्स पर शेयर करें (Share)",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Copy Link Surface
        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.copyAppLink(context)
              onDismiss()
            }
            .testTag("copy_app_link_surface")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = PortalNavy,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = viewModel.APP_SHARE_URL,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F52BA),
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Text(
              text = "कॉपी करें",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy,
              modifier = Modifier.padding(start = 6.dp)
            )
          }
        }
      }
    }
  }
}
