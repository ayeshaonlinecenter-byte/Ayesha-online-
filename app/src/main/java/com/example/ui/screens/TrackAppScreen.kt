package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SuccessGreenDark
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackAppScreen(viewModel: OnlineCenterViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("आवेदन ट्रैक करें", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PortalNavy, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("आपके आवेदन:", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PortalNavy)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(allOrders) { order ->
                     Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text("रसीद: ${order.slipNumber}", fontWeight = FontWeight.Bold, color = PortalNavy)
                             Text("सेवा: ${order.serviceNameHindi}", fontWeight = FontWeight.Bold)
                             Text("स्थिति: ${order.status.titleHindi}", fontWeight = FontWeight.Black, color = if (order.status == OrderStatus.COMPLETED) SuccessGreenDark else Color.Gray)
                             
                             if (order.status == OrderStatus.COMPLETED && order.finalDocumentUrl.isNotBlank()) {
                                 Spacer(modifier = Modifier.height(8.dp))
                                 Button(
                                     onClick = {
                                         val intent = Intent(Intent.ACTION_VIEW, Uri.parse(order.finalDocumentUrl))
                                         context.startActivity(intent)
                                     },
                                     colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark)
                                 ) {
                                     Icon(Icons.Default.Download, contentDescription = null)
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Text("पीडीएफ डाउनलोड करें")
                                 }
                             }
                             
                             Spacer(modifier = Modifier.height(8.dp))
                             OutlinedButton(onClick = { viewModel.viewSlip(order) }) {
                                 Text("रसीद का विवरण देखें")
                             }
                         }
                     }
                }
            }
        }
    }
}
