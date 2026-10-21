package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.model.ServiceOrder
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorDashboardScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allOrders by viewModel.allOrders.collectAsState()

  var selectedOrderForUpdate by remember { mutableStateOf<ServiceOrder?>(null) }
  var docToView by remember { mutableStateOf<Pair<String, ServiceOrder>?>(null) }

  // Metrics calculation
  val totalEarnings = allOrders.sumOf { it.applicationFeePaid }
  val totalOrdersCount = allOrders.size
  val inProgressCount = allOrders.count { it.status == OrderStatus.IN_PROGRESS || it.status == OrderStatus.PAYMENT_VERIFIED }
  val completedCount = allOrders.count { it.status == OrderStatus.COMPLETED }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "संचालक पैनल (Operator Portal)",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "घर बैठे काम स्वीकार करें व संपर्क करें",
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
        actions = {
          // New Orders Badge
          val newOrders = allOrders.count { it.status == com.example.model.OrderStatus.PENDING_PAYMENT }
          if (newOrders > 0) {
            Box(
                modifier = Modifier.padding(end = 8.dp).background(Color.Red, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "New: $newOrders",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
          }
          IconButton(
            onClick = { viewModel.openShareDialog() },
            modifier = Modifier.testTag("operator_share_app_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share App",
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
      // Welcome Owner Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = PortalNavy),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .background(SaffronGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "आयशा ऑनलाइन सेवा केंद्र (Operator Hub)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
              Text(
                text = "ग्राहकों के आवेदन व संलग्न PDF दस्तावेज यहाँ प्राप्त होते हैं।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F)
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // 4 Metric Grid Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Total Earnings
          MetricCard(
            title = "कुल आवेदन आय",
            value = "₹$totalEarnings",
            icon = Icons.Default.CurrencyRupee,
            bgColor = SuccessGreenLight,
            textColor = SuccessGreenDark,
            modifier = Modifier.weight(1f)
          )

          // Total Orders
          MetricCard(
            title = "कुल प्राप्त आवेदन",
            value = "$totalOrdersCount",
            icon = Icons.Default.Receipt,
            bgColor = PortalBlueLight,
            textColor = PortalNavy,
            modifier = Modifier.weight(1f)
          )
        }
      }

      item { Spacer(modifier = Modifier.height(10.dp)) }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // In Progress
          MetricCard(
            title = "प्रगति पर (In Progress)",
            value = "$inProgressCount",
            icon = Icons.Default.PendingActions,
            bgColor = SaffronLight,
            textColor = Color(0xFFC66900),
            modifier = Modifier.weight(1f)
          )

          // Completed
          MetricCard(
            title = "पूर्ण कार्य (Completed)",
            value = "$completedCount",
            icon = Icons.Default.TaskAlt,
            bgColor = Color(0xFFEFF6FF),
            textColor = Color(0xFF1D4ED8),
            modifier = Modifier.weight(1f)
          )
        }
      }

      item { Spacer(modifier = Modifier.height(18.dp)) }

      // Header for Orders List
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "प्राप्त ऑनलाइन आवेदन (Customer Work Queue):",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "सभी (${allOrders.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
          )
        }
      }

      item { Spacer(modifier = Modifier.height(10.dp)) }

      if (allOrders.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8))
            )
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "अभी तक कोई नया आवेदन प्राप्त नहीं हुआ है।",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "जैसे ही कोई ग्राहक फॉर्म भरेगा और पेमेंट करेगा, उसका काम और PDF दस्तावेज तुरंत यहाँ दिखाई देंगे।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(allOrders) { order ->
          OperatorOrderCardWithDocs(
            order = order,
            onCallClick = { viewModel.contactCustomer(context, order.customerPhone, viaWhatsApp = false) },
            onWhatsAppClick = { viewModel.contactCustomer(context, order.customerPhone, viaWhatsApp = true) },
            onUpdateStatusClick = { selectedOrderForUpdate = order },
            onViewSlipClick = { viewModel.viewSlip(order) },
            onDeleteClick = { viewModel.deleteOrder(order.slipNumber) },
            onDocClick = { docEntry -> docToView = Pair(docEntry, order) }
          )
        }
      }
    }
  }

  // Update Status Dialog
  if (selectedOrderForUpdate != null) {
    val order = selectedOrderForUpdate!!
    var newStatus by remember { mutableStateOf(order.status) }
    var notes by remember { mutableStateOf(order.operatorNotes) }
    var govtRef by remember { mutableStateOf(order.govtAckNumber) }
    var docUrl by remember { mutableStateOf(order.finalDocumentUrl) }

    AlertDialog(
      onDismissRequest = { selectedOrderForUpdate = null },
      containerColor = Color.White,
      titleContentColor = Color.Black,
      textContentColor = Color.Black,
      title = {
        Text("कार्य स्थिति अपडेट करें (Update Status)", fontWeight = FontWeight.Black, color = Color.Black)
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 450.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = "स्लिप: ${order.slipNumber}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
          )
          Text(
            text = "${order.serviceNameHindi} - ${order.customerName}",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text("नई स्थिति चुनें (Select Status):", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
          Spacer(modifier = Modifier.height(6.dp))

          OrderStatus.values().forEach { status ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { newStatus = status }
                .padding(vertical = 3.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(
                    width = if (newStatus == status) 2.dp else 1.dp,
                    color = if (newStatus == status) Color.Black else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              androidx.compose.material3.RadioButton(
                selected = newStatus == status,
                onClick = { newStatus = status },
                colors = androidx.compose.material3.RadioButtonDefaults.colors(
                  selectedColor = Color.Black,
                  unselectedColor = Color.DarkGray
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = status.titleHindi,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = govtRef,
            onValueChange = { govtRef = it },
            label = { Text("सरकारी आवेदन/पावती संख्या (Govt Ref No)", fontWeight = FontWeight.Bold, color = Color.Black) },
            placeholder = { Text("उदा. NSDL-481920491", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedPlaceholderColor = Color.DarkGray,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = docUrl,
            onValueChange = { docUrl = it },
            label = { Text("सरकारी दस्तावेज लिंक (लिंक यहाँ से डाउनलोड करें)", fontWeight = FontWeight.Bold, color = Color.Black) },
            placeholder = { Text("https:// या PDF लिंक यहाँ दर्ज करें", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedPlaceholderColor = Color.DarkGray,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("ग्राहक हेतु संदेश / रिमार्क (Notes)", fontWeight = FontWeight.Bold, color = Color.Black) },
            placeholder = { Text("उदा. आपका फॉर्म सबमिट हो गया है", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedPlaceholderColor = Color.DarkGray,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateOrderStatus(order.slipNumber, newStatus, notes, govtRef, docUrl)
            selectedOrderForUpdate = null
            Toast.makeText(context, "स्थिति अपडेट हो गई!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PortalNavy)
        ) {
          Text("सेव करें (Save)", fontWeight = FontWeight.Bold, color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedOrderForUpdate = null }) {
          Text("रद्द करें", fontWeight = FontWeight.Bold, color = Color.Black)
        }
      }
    )
  }

  // Document Viewer Dialog for Operator
  if (docToView != null) {
    val (docEntry, order) = docToView!!
    val parts = docEntry.split("|")
    val docName = parts.firstOrNull() ?: docEntry
    val docUriStr = if (parts.size > 1) parts[1] else ""

    val isImage = docName.endsWith(".jpg", ignoreCase = true) ||
                  docName.endsWith(".jpeg", ignoreCase = true) ||
                  docName.endsWith(".png", ignoreCase = true) ||
                  docName.endsWith(".webp", ignoreCase = true) ||
                  docUriStr.contains("image", ignoreCase = true)

    AlertDialog(
      onDismissRequest = { docToView = null },
      containerColor = Color.White,
      titleContentColor = Color.Black,
      textContentColor = Color.Black,
      title = {
        Text("📄 ग्राहक दस्तावेज व्यूअर", fontWeight = FontWeight.Black, color = Color.Black)
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text("फाइल का नाम: $docName", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          Spacer(modifier = Modifier.height(4.dp))
          Text("ग्राहक: ${order.customerName}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          Text("मोबाइल: +91 ${order.customerPhone}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          Text("सेवा: ${order.serviceNameHindi}", fontSize = 12.sp, color = Color(0xFF334155))

          Spacer(modifier = Modifier.height(10.dp))

          // IN-APP DIRECT IMAGE PREVIEW (Fixes customer documents not showing on screen!)
          if (docUriStr.isNotBlank() && isImage) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              coil.compose.AsyncImage(
                model = Uri.parse(docUriStr),
                contentDescription = docName,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          if (docUriStr.isNotBlank()) {
            Button(
              onClick = {
                try {
                  val rawUri = Uri.parse(docUriStr)
                  val intent = Intent(Intent.ACTION_VIEW).apply {
                    if (rawUri.scheme == "file") {
                      val file = java.io.File(rawUri.path ?: "")
                      val contentUri = androidx.core.content.FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                      )
                      setDataAndType(contentUri, context.contentResolver.getType(contentUri) ?: if (isImage) "image/*" else "application/pdf")
                      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                      setDataAndType(rawUri, if (isImage) "image/*" else "application/pdf")
                      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                  }
                  context.startActivity(Intent.createChooser(intent, "दस्तावेज़ खोलें"))
                } catch (e: Exception) {
                  Toast.makeText(context, "फ़ाइल खोलने में असमर्थ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("👁️ गैलरी / पीडीएफ व्यूअर में खोलें", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          Button(
            onClick = {
              viewModel.contactCustomer(context, order.customerPhone, viaWhatsApp = true)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("💬 व्हाट्सएप पर साफ़ फोटो/PDF मंगाएं", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { docToView = null }) {
          Text("बंद करें", fontWeight = FontWeight.Bold, color = Color.Black)
        }
      }
    )
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  bgColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = bgColor),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = textColor)
        Text(text = title, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
      }
    }
  }
}

@Composable
fun OperatorOrderCardWithDocs(
  order: ServiceOrder,
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  onUpdateStatusClick: () -> Unit,
  onViewSlipClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onDocClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = order.slipNumber,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = PortalNavy
          )
          Text(
            text = "ग्राहक: ${order.customerName}",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        Box(
          modifier = Modifier
            .background(SuccessGreenLight, RoundedCornerShape(4.dp))
            .border(1.dp, SuccessGreenDark, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "₹${order.applicationFeePaid} PAID",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            color = SuccessGreenDark
          )
        }
        
        IconButton(onClick = onDeleteClick) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "कार्य: ${order.serviceNameHindi}",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
      )

      // Attached PDF/JPG Documents uploaded by customer
      if (order.attachedDocuments.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "📄 संलग्न दस्तावेज (क्लिक करके फ़ाइल/फोटो खोलें):",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black,
          color = PortalNavy
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          order.attachedDocuments.forEach { docEntry ->
            val parts = docEntry.split("|")
            val docName = parts.firstOrNull() ?: docEntry
            val docUriStr = if (parts.size > 1) parts[1] else ""

            Surface(
              color = Color(0xFFEFF6FF),
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onDocClick(docEntry)
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = docName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                  )
                }
                Box(
                  modifier = Modifier
                    .background(Color(0xFF1D4ED8), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("खोलें / देखें", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }
      }

      if (order.additionalDetails.isNotBlank()) {
        Text(
          text = "निर्देश: ${order.additionalDetails}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF475569)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Current Status Pill
      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(4.dp)
      ) {
        Text(
          text = "स्थिति: ${order.status.titleHindi}",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black,
          color = PortalNavy,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Color(0xFFE2E8F0))
      Spacer(modifier = Modifier.height(8.dp))

      // Actions for Operator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Call Button
          OutlinedButton(
            onClick = onCallClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp), tint = PortalNavy)
            Spacer(modifier = Modifier.width(4.dp))
            Text("कॉल", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PortalNavy)
          }

          // WhatsApp Button
          Button(
            onClick = onWhatsAppClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("व्हाट्सएप", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Update Status
          Button(
            onClick = onUpdateStatusClick,
            colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("स्थिति बदलें", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
          }

          // View Slip
          OutlinedButton(
            onClick = onViewSlipClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("स्लिप", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PortalNavy)
          }

          // Delete Order
          IconButton(
            onClick = onDeleteClick
          ) {
             Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
          }
        }
      }
    }
  }
}
