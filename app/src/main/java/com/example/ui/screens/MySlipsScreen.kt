package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceOrder
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.OnlineCenterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySlipsScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val filteredOrders by viewModel.filteredOrders.collectAsState()
  val slipSearchQuery by viewModel.slipSearchQuery.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "मेरी पावती रसीदें (My Slips)",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "कुल ${filteredOrders.size} स्लिप उपलब्ध हैं",
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
          IconButton(
            onClick = { viewModel.openShareDialog() },
            modifier = Modifier.testTag("slips_share_app_btn")
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
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(Color(0xFFF1F5F9))
    ) {
      val currentUser by viewModel.currentUser.collectAsState()
      val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity

      if (currentUser == null && activity != null) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PortalNavy)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("🔑 जीमेल अकाउंट से लॉगिन करें", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PortalNavy)
              Text("अपनी सभी पावती रसीदों का रियल-टाइम अपडेट पाने के लिए लॉगिन करें।", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            androidx.compose.material3.Button(
              onClick = { viewModel.onGoogleSignInClicked(activity) },
              colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PortalNavy),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("लॉगिन", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
          }
        }
      }

      // Search Bar for slips
      Box(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = slipSearchQuery,
          onValueChange = { viewModel.setSlipSearchQuery(it) },
          placeholder = {
            Text(
              "रसीद संख्या या मोबाइल नंबर से खोजें...",
              color = Color(0xFF475569),
              fontWeight = FontWeight.Medium
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PortalNavy)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("slip_search_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedBorderColor = PortalNavy,
            unfocusedBorderColor = Color(0xFF94A3B8)
          ),
          singleLine = true
        )
      }

      if (filteredOrders.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Receipt,
              contentDescription = null,
              tint = Color(0xFF475569),
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "अभी कोई पावती स्लिप नहीं है",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "जैसे ही आप आवेदन व पावती पेमेंट करेंगे, आपकी आधिकारिक स्लिप यहाँ उपलब्ध होगी।",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF334155),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 24.dp)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredOrders) { order ->
            SlipListItemCard(
              order = order,
              onClick = { viewModel.viewSlip(order) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun SlipListItemCard(
  order: ServiceOrder,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.orderTimestamp))

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("slip_item_${order.slipNumber}"),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    shape = RoundedCornerShape(12.dp),
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
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black,
            color = PortalNavy,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = dateStr,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
          )
        }

        Box(
          modifier = Modifier
            .background(SuccessGreenLight, RoundedCornerShape(4.dp))
            .border(1.dp, SuccessGreenDark, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreenDark,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "PAID: ₹${order.applicationFeePaid}",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Black,
              color = SuccessGreenDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Divider(color = Color(0xFFE2E8F0))
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = order.serviceNameHindi,
        fontSize = 14.5.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF0F172A),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      if (order.attachedDocuments.isNotEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
          Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF0F52BA), modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "दस्तावेज: ${order.attachedDocuments.joinToString(", ")}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F52BA),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ग्राहक: ${order.customerName} (+91 ${order.customerPhone})",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "स्लिप देखें",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            color = PortalNavy
          )
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = PortalNavy,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }
  }
}
