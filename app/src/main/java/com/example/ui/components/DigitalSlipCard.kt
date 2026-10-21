package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.model.ServiceOrder
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SlipBackground
import com.example.ui.theme.SlipBorder
import com.example.ui.theme.StampGreen
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DigitalSlipCard(
  order: ServiceOrder,
  modifier: Modifier = Modifier,
  showFullDetails: Boolean = true
) {
  val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
  val dateStr = dateFormat.format(Date(order.orderTimestamp))

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .border(2.dp, PortalNavy, RoundedCornerShape(12.dp)),
    shape = RoundedCornerShape(12.dp),
    color = SlipBackground,
    shadowElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Slip Header - Center Branding & CSC Reg
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              painter = painterResource(id = R.drawable.ayesha_logo),
              contentDescription = "Ayesha Online Logo",
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, PortalNavy, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AYESHA ONLINE CENTER",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy,
              letterSpacing = 0.5.sp
            )
          }
          Text(
            text = "डिजिटल सेवा केंद्र एवं ऑनलाइन साइबर कैफे",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "CSC ID: 7496836261 | Reg: DL/2026/AOK | ayeshaonlinecenter@gmail.com",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }

        // Paid Badge
        Box(
          modifier = Modifier
            .background(SuccessGreenLight, RoundedCornerShape(6.dp))
            .border(1.5.dp, SuccessGreenDark, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Paid",
              tint = SuccessGreenDark,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "PAID (सफल)",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Black,
              color = SuccessGreenDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Solid Blue Divider
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .background(PortalNavy)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Title Banner: Official E-Receipt / Acknowledgment
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(PortalBlueLight, RoundedCornerShape(6.dp))
          .border(1.dp, PortalNavy.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
          .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "आधिकारिक ई-सेवा पावती रसीद (ACKNOWLEDGMENT SLIP)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = PortalNavy,
            textAlign = TextAlign.Center
          )
          Text(
            text = "हाथों-हाथ डिजिटल सत्यापन पर्ची | आवेदन सुरक्षित दर्ज",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Slip Meta Table (Slip No, Date)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF64748B), RoundedCornerShape(6.dp))
          .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1.2f)) {
          Text(text = "रसीद संख्या (Slip No.)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          Text(
            text = order.slipNumber,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A),
            fontFamily = FontFamily.Monospace
          )
        }
        Column(modifier = Modifier.weight(1.1f)) {
          Text(text = "तारीख व समय (Date & Time)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          Text(
            text = dateStr,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Applicant Details Section
      Text(
        text = "आवेदक विवरण (Applicant Details):",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = PortalNavy
      )
      Spacer(modifier = Modifier.height(4.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White, RoundedCornerShape(6.dp))
          .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        SlipRow(label = "ग्राहक का नाम (Name)", value = order.customerName, isBoldValue = true)
        SlipRow(label = "मोबाइल नंबर (Mobile)", value = "+91 ${order.customerPhone}", isBoldValue = true)
        if (order.fatherOrSpouseName.isNotBlank()) {
          SlipRow(label = "पिता/पति का नाम (Father/Spouse)", value = order.fatherOrSpouseName)
        }
        if (order.address.isNotBlank()) {
          SlipRow(label = "पता (Address)", value = order.address)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Service Details Section
      Text(
        text = "सेवा व कार्य का विवरण (Service Details):",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = PortalNavy
      )
      Spacer(modifier = Modifier.height(4.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White, RoundedCornerShape(6.dp))
          .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        SlipRow(label = "सेवा का नाम (Service)", value = order.serviceNameHindi, isBoldValue = true)
        SlipRow(label = "श्रेणी (Category)", value = order.category.titleHindi)

        // Attached Documents List
        if (showFullDetails) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "संलग्न दस्तावेज (Attached Docs):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                modifier = Modifier.weight(1f)
              )
              val docsText = if (order.attachedDocuments.isNotEmpty()) {
                order.attachedDocuments.map { it.split("|").firstOrNull() ?: it }.joinToString("\n")
              } else {
                "व्हाट्सएप पर उपलब्ध कराया जाएगा"
              }
              Text(
                text = docsText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.3f)
              )
            }
        }

        if (order.additionalDetails.isNotBlank()) {
          SlipRow(label = "विशेष निर्देश (Remarks)", value = order.additionalDetails)
        }
        if (order.govtAckNumber.isNotBlank()) {
          SlipRow(label = "सरकारी पावती क्र. (Govt Ref)", value = order.govtAckNumber, isBoldValue = true)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Payment Details & Status - Explicit "Only for Application / Receipt Payment"
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
          .border(1.dp, Color(0xFF64748B), RoundedCornerShape(6.dp))
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "आवेदन व पावती शुल्क (Application Fee):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
          Text(
            text = "₹${order.applicationFeePaid}.00 (PAID)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = SuccessGreenDark
          )
          Text(
            text = "माध्यम: ${order.paymentMethod} | Ref: ${order.transactionRef.takeLast(10)}",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "*शेष कार्य पेमेंट पावती सत्यापन के बाद संपर्क पर देय होगा।",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309)
          )
        }

        // Verified Stamp on the receipt
        VerifiedStamp(
          modifier = Modifier.size(84.dp),
          textTop = "AYESHA ONLINE",
          textMain = "PAID & VERIFIED",
          textBottom = "CSC CENTER",
          stampColor = StampGreen,
          rotationDegrees = -8f
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status Note Box
      val statusColor = when (order.status) {
        OrderStatus.COMPLETED -> SuccessGreenDark
        OrderStatus.REJECTED -> Color(0xFF991B1B)
        else -> PortalNavy
      }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(statusColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
          .border(1.5.dp, statusColor, RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "वर्तमान स्थिति: ${order.status.titleHindi}",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = statusColor
            )
          }
          if (order.operatorNotes.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = order.operatorNotes,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF0F172A)
            )
          }
        }
      }

      if (showFullDetails) {
        Spacer(modifier = Modifier.height(12.dp))

        // Barcode & QR Verification Section
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF64748B), RoundedCornerShape(6.dp))
            .background(Color.White, RoundedCornerShape(6.dp))
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
              text = "बारकोड सत्यापन (Barcode Scan):",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(4.dp))
            BarcodeView(code = order.slipNumber, height = 36.dp)
            Text(
              text = "*${order.slipNumber}*",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth(),
              color = Color(0xFF0F172A)
            )
          }

          QrCodeView(
            content = "AYESHA_ONLINE_SLIP:${order.slipNumber}|${order.customerName}|₹${order.applicationFeePaid}|STATUS:PAID",
            size = 64.dp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Signatures and footer
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "ग्राहक पावती प्रति",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
            Text(
              text = "Customer Copy",
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Box(
              modifier = Modifier
                .width(120.dp)
                .height(1.5.dp)
                .background(Color(0xFF334155))
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "अधिकृत केंद्र संचालक",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy
            )
            Text(
              text = "Ayesha Online Center",
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "यह एक अधिकृत ई-सेवा पावती पर्ची है। कृपया इसे संदर्भ हेतु सुरक्षित रखें।",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF475569),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
private fun SlipRow(
  label: String,
  value: String,
  isBoldValue: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.5.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF334155),
      modifier = Modifier.weight(1f)
    )
    Text(
      text = value,
      fontSize = 11.sp,
      fontWeight = if (isBoldValue) FontWeight.Black else FontWeight.SemiBold,
      color = Color(0xFF0F172A),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(1.3f)
    )
  }
}
