package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeView
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val service by viewModel.selectedService.collectAsState()
  val formInput by viewModel.formInput.collectAsState()
  val paymentMethod by viewModel.paymentMethod.collectAsState()
  val transactionRef by viewModel.transactionRef.collectAsState()
  val isPaymentConfirmedChecked by viewModel.paymentConfirmedChecked.collectAsState()
  val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
  val formError by viewModel.formError.collectAsState()

  if (service == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("कृपया सेवा चुनें।", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
    return
  }

  val currentService = service!!
  val upiId = "9977998734-7@YBL"
  val amount = currentService.applicationFee
  val upiPayload = "upi://pay?pa=$upiId&pn=Ayesha+Online+Center&am=$amount&cu=INR&tn=${currentService.id}_application_fee"

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "सुरक्षित भुगतान (Secure Payment)",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "ओनली फॉर आवेदन पेमेंट | भुगतान बाद तुरंत स्लिप",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFFD54F)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = { viewModel.handleBack() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PortalNavy)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(Color(0xFFF1F5F9)),
      contentPadding = PaddingValues(16.dp)
    ) {
      // Slip Lock Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626))
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFFEE2E2), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "डिजिटल स्लिप अभी लॉक है (Slip Locked)",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF991B1B)
              )
              Text(
                text = "पेमेंट होने से पहले स्लिप जनरेट नहीं होगी। कृपया पहले आवेदन शुल्क भुगतान करें और नीचे दिए गए टिक मार्क पर क्लिक करें।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF991B1B)
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // Payment Summary Card - High Contrast
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8))
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "आवेदन व फीस विवरण (Order Summary):",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            PaymentDetailRow("सेवा का नाम (Service)", currentService.nameHindi, isBold = true)
            PaymentDetailRow("ग्राहक का नाम (Customer)", formInput.customerName, isBold = true)
            PaymentDetailRow("मोबाइल नंबर (Mobile)", "+91 ${formInput.customerPhone}")
            if (formInput.attachedDocuments.isNotEmpty()) {
              PaymentDetailRow("संलग्न दस्तावेज (Attached)", "${formInput.attachedDocuments.size} फाइलें उपलब्ध")
            }

            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ओनली फॉर आवेदन पेमेंट:",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A)
                )
                Text(
                  text = "(सिर्फ रिसीप्ट व आवेदन दर्ज करने का शुल्क)",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F52BA)
                )
              }
              Text(
                text = "₹$amount.00",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = SuccessGreenDark
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notice that remainder is paid after receipt
            Surface(
              color = Color(0xFFFFFBEB),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "💡 ध्यान दें: बाकी मुख्य कार्य का भुगतान आपको डिजिटल पावती स्लिप मिलने के बाद ही करना है। केंद्र संचालक आपसे सीधे संपर्क करके बाकी प्रक्रिया पूरी करेंगे।",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F),
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      // Dynamic UPI QR Code Section
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8))
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.QrCode2,
                  contentDescription = null,
                  tint = PortalNavy,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "QR कोड स्कैन करके ₹$amount का भुगतान करें",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A)
                )
              }

              Box(
                modifier = Modifier
                  .background(SuccessGreenLight, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "0% शुल्क",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = SuccessGreenDark
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The Canvas-drawn Real QR Code
            QrCodeView(
              content = upiPayload,
              size = 180.dp,
              primaryColor = PortalNavy
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "किसी भी UPI ऐप (GPay, PhonePe, Paytm, BHIM) से ₹$amount स्कैन करें",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // UPI ID Display and Copy Pill
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .border(1.5.dp, Color(0xFF94A3B8), RoundedCornerShape(8.dp))
                .clickable {
                  clipboardManager.setText(AnnotatedString(upiId))
                  Toast.makeText(context, "UPI ID कॉपी की गई: $upiId", Toast.LENGTH_SHORT).show()
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "UPI ID: $upiId",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = PortalNavy,
                  fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy",
                  tint = PortalNavy,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment App Option Pills
            Text(
              text = "या नीचे दिए गए भुगतान ऐप का चयन करें:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              UpiAppChip(
                name = "PhonePe",
                isSelected = paymentMethod == "PHONEPE",
                color = Color(0xFF5F259F),
                onClick = { viewModel.setPaymentMethod("PHONEPE") }
              )
              UpiAppChip(
                name = "Google Pay",
                isSelected = paymentMethod == "GPAY",
                color = Color(0xFF1A73E8),
                onClick = { viewModel.setPaymentMethod("GPAY") }
              )
              UpiAppChip(
                name = "Paytm",
                isSelected = paymentMethod == "PAYTM",
                color = Color(0xFF002E6E),
                onClick = { viewModel.setPaymentMethod("PAYTM") }
              )
              UpiAppChip(
                name = "दुकान/Cash",
                isSelected = paymentMethod == "CASH",
                color = Color(0xFF2E7D32),
                onClick = { viewModel.setPaymentMethod("CASH") }
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // UTR / Transaction Reference Input
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "UPI ट्रांजैक्शन आईडी / UTR नंबर (वैकल्पिक):",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = transactionRef,
              onValueChange = { viewModel.setTransactionRef(it) },
              placeholder = {
                Text(
                  "उदा. 429182749102 (12-अंक UTR नंबर)",
                  color = Color(0xFF64748B),
                  fontWeight = FontWeight.Medium
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("transaction_ref_input"),
              shape = RoundedCornerShape(8.dp),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A),
                focusedBorderColor = PortalNavy,
                unfocusedBorderColor = Color(0xFF94A3B8)
              )
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      // ==============================================================
      // CRITICAL REQUIREMENT ENFORCEMENT:
      // "उसमें ऐसा टिक मार्क आए कि पेमेंट पहले कीजिए तो आपकी स्लिप हाथों-हाथ जनरेट हो जाएगी।"
      // ==============================================================
      item {
        Surface(
          color = if (isPaymentConfirmedChecked) SuccessGreenLight else SaffronLight,
          border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isPaymentConfirmedChecked) SuccessGreenDark else SaffronGold
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.setPaymentConfirmedChecked(!isPaymentConfirmedChecked) }
            .testTag("payment_confirmation_tick_box")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = isPaymentConfirmedChecked,
              onCheckedChange = { viewModel.setPaymentConfirmedChecked(it) },
              colors = CheckboxDefaults.colors(
                checkedColor = SuccessGreenDark,
                uncheckedColor = Color(0xFFB45309)
              ),
              modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "✓ पेमेंट पहले कीजिए तो आपकी स्लिप हाथों-हाथ जनरेट हो जाएगी!",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = if (isPaymentConfirmedChecked) SuccessGreenDark else Color(0xFF78350F)
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "मैंने आवेदन व पावती का ₹$amount भुगतान कर दिया है। बाकी मुख्य कार्य पेमेंट पावती मिलने के बाद संचालक को संपर्क पर दूंगा/दूंगी। मेरी डिजिटल स्लिप तुरंत जारी करें।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPaymentConfirmedChecked) Color(0xFF065F46) else Color(0xFF78350F)
              )
            }
          }
        }
      }

      // If user hasn't ticked, show guidance warning
      if (!isPaymentConfirmedChecked) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "कृपया ऊपर दिए गए बॉक्स पर टिक मार्क लगाएं ताकि आपकी स्लिप तुरंत बन सके।",
              fontSize = 11.5.sp,
              color = Color(0xFF92400E),
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      if (formError != null) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = formError!!,
            color = Color(0xFFDC2626),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }
      }

      item { Spacer(modifier = Modifier.height(18.dp)) }

      // Big Pay & Generate Slip Button
      item {
        Button(
          onClick = {
            if (!isPaymentConfirmedChecked) {
              Toast.makeText(
                context,
                "कृपया पहले 'पेमेंट पहले कीजिए' टिक मार्क पर क्लिक करें!",
                Toast.LENGTH_LONG
              ).show()
            } else {
              viewModel.confirmPaymentAndGenerateSlip { order ->
                Toast.makeText(
                  context,
                  "भुगतान सफल! स्लिप जारी: ${order.slipNumber}",
                  Toast.LENGTH_SHORT
                ).show()
              }
            }
          },
          enabled = !isProcessingPayment,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isPaymentConfirmedChecked) SuccessGreenDark else PortalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("confirm_payment_generate_slip_btn")
        ) {
          if (isProcessingPayment) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              "पेमेंट सत्यापित व स्लिप जनरेट हो रही है...",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          } else {
            Icon(
              imageVector = if (isPaymentConfirmedChecked) Icons.Default.CheckCircle else Icons.Default.Payment,
              contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isPaymentConfirmedChecked)
                "भुगतान सत्यापित करें और हाथों-हाथ स्लिप प्राप्त करें"
              else
                "पहले पेमेंट करें (Pay ₹$amount)",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PaymentDetailRow(label: String, value: String, isBold: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF334155)
    )
    Text(
      text = value,
      fontSize = 12.sp,
      fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
      color = Color(0xFF0F172A)
    )
  }
}

@Composable
private fun UpiAppChip(
  name: String,
  isSelected: Boolean,
  color: Color,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) color.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
    border = androidx.compose.foundation.BorderStroke(
      2.dp,
      if (isSelected) color else Color(0xFF94A3B8)
    ),
    modifier = Modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = name,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Black,
        color = if (isSelected) color else Color(0xFF0F172A)
      )
    }
  }
}
