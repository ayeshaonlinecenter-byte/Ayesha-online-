package com.example.ui.screens

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceCategory
import com.example.ui.theme.PortalNavy
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCatalogScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val activity = LocalContext.current as Activity
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val filteredServices by viewModel.filteredServices.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "सभी ऑनलाइन सेवाएं व दस्तावेज",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "ओनली फॉर आवेदन पेमेंट | कुल ${filteredServices.size} सेवाएं",
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
            modifier = Modifier.testTag("catalog_share_app_btn")
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
      // Search Bar
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = {
            Text(
              "सेवा खोजें (पैन, आधार, प्रमाण पत्र, राशन, बिजली...)",
              color = Color(0xFF475569),
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PortalNavy)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("catalog_search_input"),
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

      // Categories horizontal filter chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
      ) {
        items(ServiceCategory.values()) { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setCategory(category) },
            label = {
              Text(
                text = category.titleHindi,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF0F172A)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = PortalNavy,
              selectedLabelColor = Color.White,
              containerColor = Color.White,
              labelColor = Color(0xFF0F172A)
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = Color(0xFF94A3B8),
              selectedBorderColor = PortalNavy
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // List of Services with detailed documents
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
      ) {
        items(filteredServices) { service ->
          ServiceCardWithRateAndDiscount(
            service = service,
            onApplyClick = { viewModel.requestApplyService(activity, service) }
          )
        }
      }
    }
  }
}
