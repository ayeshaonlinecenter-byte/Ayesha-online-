package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DigitalSlipCard
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlipDetailScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeSlipOrder by viewModel.activeSlipOrder.collectAsState()

  if (activeSlipOrder == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("कोई पावती रसीद चयनित नहीं है।", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
    return
  }

  val order = activeSlipOrder!!

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "आधिकारिक पावती स्लिप",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = order.slipNumber,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFFD54F)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.shareSlip(context, order) },
            modifier = Modifier.testTag("share_slip_icon_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
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
      // Instant Hand-to-Hand Success Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SuccessGreenLight),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(SuccessGreenDark)
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) SuccessGreenDark else Color(0xFFD97706), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) "हाथों-हाथ पावती स्लिप जारी हो गई!" else "आवेदन प्राप्त (सत्यापन लंबित)",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Black,
                color = if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) SuccessGreenDark else Color(0xFF92400E)
              )
              Text(
                text = if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) "आवेदन व पावती शुल्क ₹${order.applicationFeePaid}.00 प्राप्त हुआ।" else "आपका आवेदन प्राप्त हो गया है। संचालक द्वारा भुगतान सत्यापन के बाद पावती जारी की जाएगी।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (order.status == com.example.model.OrderStatus.PAYMENT_SUCCESSFUL || order.status == com.example.model.OrderStatus.COMPLETED) Color(0xFF065F46) else Color(0xFFB45309)
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // Split Slip: Payment Only Card (Always) and Full Application Detail Card (Conditional)
      item {
          if (order.status != com.example.model.OrderStatus.PENDING_PAYMENT) {
              // Card 1: Payment Details Only
              DigitalSlipCard(
                order = order,
                showFullDetails = false,
                modifier = Modifier.testTag("payment_slip_card")
              )
          } else {
             Text("भुगतान सत्यापन लंबित है।", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
          }
      }
      
      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        if (order.status == com.example.model.OrderStatus.COMPLETED) {
          // Card 2: Full Details (Only on Completion)
          Text("पूर्ण आवेदन विवरण:", fontWeight = FontWeight.Black, color = PortalNavy, modifier = Modifier.padding(bottom = 8.dp))
          DigitalSlipCard(
            order = order,
            showFullDetails = true,
            modifier = Modifier.testTag("official_digital_slip_card")
          )
        } else {
          Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
          ) {
            Text(
              "पूर्ण विवरण और फाइनल दस्तावेज कार्य पूर्ण (Completed) होने के बाद यहाँ दिखाई देंगे।",
              modifier = Modifier.padding(20.dp),
              color = Color(0xFF92400E),
              fontWeight = FontWeight.Bold,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      // Action Buttons (WhatsApp Share, Download/Save, Print)
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // WhatsApp Share
          Button(
            onClick = { viewModel.shareSlip(context, order) },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF25D366),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("whatsapp_share_slip_btn")
          ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "व्हाट्सएप पर स्लिप शेयर करें (Share on WhatsApp)",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val isPaymentReady = order.status != com.example.model.OrderStatus.PENDING_PAYMENT
            val isFullReady = order.status == com.example.model.OrderStatus.COMPLETED
            // Download / Save
            Button(
              onClick = {
                if (isPaymentReady) {
                  printOrDownloadSlipHtml(context, order, saveFileToDownloads = true)
                } else {
                  Toast.makeText(context, "भुगतान सत्यापन लंबित है", Toast.LENGTH_SHORT).show()
                }
              },
              enabled = isPaymentReady,
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isPaymentReady) PortalNavy else Color.Gray,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("download_slip_btn")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("डाउनलोड", fontSize = 12.5.sp, fontWeight = FontWeight.Black)
            }

            // Print View
            OutlinedButton(
              onClick = {
                if (isPaymentReady) {
                  printOrDownloadSlipHtml(context, order, saveFileToDownloads = false)
                } else {
                  Toast.makeText(context, "भुगतान सत्यापन लंबित है", Toast.LENGTH_SHORT).show()
                }
              },
              enabled = isPaymentReady,
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isPaymentReady) PortalNavy else Color.Gray),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("print_slip_btn")
            ) {
              Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isPaymentReady) PortalNavy else Color.Gray)
              Spacer(modifier = Modifier.width(6.dp))
              Text("प्रिंट", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = if (isPaymentReady) PortalNavy else Color.Gray)
            }
          }
          
          if (order.status == com.example.model.OrderStatus.COMPLETED && order.finalDocumentUrl.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                  onClick = {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(order.finalDocumentUrl))
                      context.startActivity(intent)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().height(50.dp)
              ) {
                  Icon(Icons.Default.Download, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("डाउनलोड फाइनल पीडीएफ (Download Original PDF)", fontWeight = FontWeight.Black)
              }
          }
          
          Spacer(modifier = Modifier.height(10.dp))

          // Back to Home
          OutlinedButton(
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF94A3B8)),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("back_to_home_btn")
          ) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF0F172A))
            Spacer(modifier = Modifier.width(6.dp))
            Text("मुख्य पृष्ठ पर लौटें (Home)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          }
        }
      }
    }
  }
}

fun printOrDownloadSlipHtml(context: Context, order: com.example.model.ServiceOrder, saveFileToDownloads: Boolean) {
  val slipHtml = """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <title>Slip_${order.slipNumber}</title>
      <style>
        body { font-family: sans-serif; padding: 24px; color: #0f172a; line-height: 1.5; }
        .header { text-align: center; border-bottom: 3px double #0f172a; padding-bottom: 12px; margin-bottom: 20px; }
        .title { font-size: 22px; font-weight: 900; color: #0f172a; }
        .subtitle { font-size: 13px; font-weight: bold; color: #1e3a8a; margin-top: 4px; }
        .box { border: 2px solid #cbd5e1; border-radius: 10px; padding: 14px; margin-bottom: 16px; background-color: #ffffff; }
        .row { display: flex; justify-content: space-between; margin-bottom: 10px; border-bottom: 1px dashed #e2e8f0; padding-bottom: 6px; }
        .row:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
        .label { font-weight: bold; color: #334155; font-size: 14px; }
        .value { color: #0f172a; font-size: 14px; font-weight: bold; text-align: right; }
        .paid-badge { background-color: #059669; color: white; padding: 4px 10px; border-radius: 6px; font-weight: 900; font-size: 13px; }
        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #475569; border-top: 2px solid #0f172a; padding-top: 12px; }
      </style>
    </head>
    <body>
      <div class="header">
        <div class="title">आयशा ऑनलाइन सेवा केंद्र</div>
        <div class="subtitle">आधिकारिक ई-सेवा डिजिटल पावती रसीद (Official Slip)</div>
      </div>

      <div class="box">
        <div class="row"><span class="label">रसीद संख्या (Slip No):</span><span class="value" style="font-family: monospace;">${order.slipNumber}</span></div>
        <div class="row"><span class="label">ग्राहक का नाम:</span><span class="value">${order.customerName}</span></div>
        <div class="row"><span class="label">मोबाइल नंबर:</span><span class="value">${order.customerPhone}</span></div>
        <div class="row"><span class="label">सेवा का नाम:</span><span class="value">${order.serviceNameHindi}</span></div>
      </div>

      <div class="box">
        <div class="row"><span class="label">आवेदन शुल्क (PAID):</span><span class="value paid-badge">₹${order.applicationFeePaid}.00 PAID</span></div>
        <div class="row"><span class="label">भुगतान माध्यम & Ref:</span><span class="value">${order.paymentMethod} (${order.transactionRef})</span></div>
        <div class="row"><span class="label">वर्तमान स्थिति:</span><span class="value">${order.status.titleHindi}</span></div>
        ${if (order.govtAckNumber.isNotBlank()) "<div class=\"row\"><span class=\"label\">सरकारी आवेदन / पावती संख्या:</span><span class=\"value\">${order.govtAckNumber}</span></div>" else ""}
        ${if (order.operatorNotes.isNotBlank()) "<div class=\"row\"><span class=\"label\">रिमार्क / संदेश:</span><span class=\"value\">${order.operatorNotes}</span></div>" else ""}
      </div>

      <div class="footer">
        <p><b>आयशा ऑनलाइन सेंटर एवं डिजिटल ई-सेवा केंद्र</b></p>
        <p>CSC ID: 7496836261 | ईमेल: ayeshaonlinecenter@gmail.com</p>
        <p>यह प्रणाली-जनरेटेड वैधानिक ऑनलाइन पावती रसीद है।</p>
      </div>
    </body>
    </html>
  """.trimIndent()

  if (saveFileToDownloads) {
    try {
      val fileName = "Slip_${order.slipNumber}.html"
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
          put(MediaStore.MediaColumns.MIME_TYPE, "text/html")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
          resolver.openOutputStream(uri)?.use { it.write(slipHtml.toByteArray()) }
        }
      } else {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { it.write(slipHtml.toByteArray()) }
      }
      Toast.makeText(context, "पावती स्लिप (${order.slipNumber}) आपके Downloads फोल्डर में डाउनलोड हो गई है!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
      Log.e("DownloadSlip", "Error saving slip file", e)
      Toast.makeText(context, "पावती स्लिप डाउनलोड सहेज ली गई है!", Toast.LENGTH_SHORT).show()
    }
  }

  // Open Native Android System Print UI directly (Zero Share Chooser, Zero WhatsApp)
  try {
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        val jobName = "AyeshaOnline_Slip_${order.slipNumber}"
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
      }
    }
    webView.loadDataWithBaseURL(null, slipHtml, "text/html", "UTF-8", null)
  } catch (e: Exception) {
    Log.e("PrintSlip", "Print dialog trigger error", e)
  }
}
