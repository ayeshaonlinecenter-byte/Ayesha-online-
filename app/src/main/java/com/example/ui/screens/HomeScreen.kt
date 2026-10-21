package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceCategory
import com.example.model.ServiceItem
import com.example.ui.theme.PortalBlue
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val isOperator by viewModel.isOperator.collectAsState()
  val activity = LocalContext.current as Activity
  val context = LocalContext.current
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val filteredServices by viewModel.filteredServices.collectAsState()
  val allOrders by viewModel.allOrders.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9)),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // Top Hero Branding Header (Solid High-Contrast Navy Blue)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(PortalNavy, Color(0xFF0F3A7A))
            )
          )
          .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
      ) {
        Column {
          // Top Status Bar: Operator Profile vs Guest Visitor Mode
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isOperator) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = androidx.compose.material.icons.Icons.Default.Verified,
                  contentDescription = null,
                  tint = SaffronGold,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "संचालक: ${currentUser?.email ?: "ayeshaonlinecenter@gmail.com"}",
                  fontSize = 11.sp,
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Share App Button
                Surface(
                  color = SaffronGold,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .clickable { viewModel.openShareDialog() }
                    .testTag("top_share_app_btn")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Share,
                      contentDescription = "Share",
                      tint = Color.Black,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "शेयर ऐप",
                      fontSize = 11.sp,
                      color = Color.Black,
                      fontWeight = FontWeight.Black
                    )
                  }
                }

                Surface(
                  color = Color.White.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { viewModel.signOut(context) }
                ) {
                  Text(
                    text = "लॉगआउट",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
            } else if (currentUser != null) {
              // Customer Logged-In Mode
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = null,
                  tint = Color(0xFF4ADE80),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "लॉगइन: ${currentUser?.email}",
                  fontSize = 11.5.sp,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                  color = SaffronGold,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { viewModel.openShareDialog() }
                ) {
                  Text(
                    text = "शेयर",
                    fontSize = 11.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Surface(
                  color = Color.White.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { viewModel.signOut(context) }
                ) {
                  Text(
                    text = "लॉगआउट",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            } else {
              // Guest Mode Indicator with Gmail Login Button
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF4ADE80), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "अतिथि मोड",
                  fontSize = 11.sp,
                  color = Color(0xFFE2E8F0),
                  fontWeight = FontWeight.Bold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Customer Gmail Sign-In Button
                Surface(
                  color = SaffronGold,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .clickable { viewModel.onGoogleSignInClicked(activity) }
                    .testTag("customer_google_login_btn")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "🔑 जीमेल लॉगिन",
                      fontSize = 11.sp,
                      color = Color.Black,
                      fontWeight = FontWeight.Black
                    )
                  }
                }

                // Operator Login Entry
                Surface(
                  color = Color.White.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(12.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                  modifier = Modifier
                    .clickable { viewModel.openOperatorLogin() }
                    .testTag("admin_login_entry_btn")
                ) {
                  Text(
                    text = "संचालक",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          // Brand Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f, fill = false)
            ) {
              Image(
                painter = painterResource(id = R.drawable.ayesha_logo),
                contentDescription = "Ayesha Online Logo",
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(2.dp, SaffronGold, RoundedCornerShape(12.dp))
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "AYESHA ONLINE CENTER",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "डिजिटल सेवा केंद्र | ई-डिस्ट्रिक्ट व ऑनलाइन वर्क",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFFD54F)
                )
              }
            }

            // Quick Operator Portal Button - ONLY FOR LOGGED-IN OPERATOR
            if (isOperator) {
              Button(
                onClick = { viewModel.navigateTo(AppScreen.OPERATOR_DASHBOARD) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = SaffronGold,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("operator_mode_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = "Operator",
                  modifier = Modifier.size(16.dp),
                  tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "संचालक",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Special 10% Flat Online Discount & Free Services Offer Banner
          Surface(
            color = Color(0xFFFFFBEB),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD97706)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocalOffer,
                  contentDescription = null,
                  tint = Color(0xFFD97706),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "🎁 स्पेशल ऑनलाइन बुकिंग ऑफर (10% Flat OFF)",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF78350F)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "• ऑनलाइन पोर्टल से बुक करने पर सभी सेवाओं पर सीधा 10% की छूट!",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "• आयुष्मान कार्ड बिल्कुल फ्री | ई-श्रम नया व KYC चैनल सब्सक्राइब पर 100% फ्री!",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreenDark
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Action Pill Bar (Book Service, My Slips)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { viewModel.navigateTo(AppScreen.SERVICES) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = PortalNavy
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("all_services_btn")
            ) {
              Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp), tint = PortalNavy)
              Spacer(modifier = Modifier.width(6.dp))
              Text("सभी 22 सेवाएं", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PortalNavy)
            }

            Button(
              onClick = { viewModel.navigateTo(AppScreen.MY_SLIPS) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF07214E),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                .testTag("my_slips_btn")
            ) {
              Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("मेरी पर्चियां", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            
            Button(
              onClick = { viewModel.navigateTo(AppScreen.TRACK_APP) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC66900),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("track_app_btn")
            ) {
              Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("ट्रैक करें", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
          }
        }
      }
    }

    // Search Box
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = {
            Text(
              "रेट लिस्ट खोजें (पैन कार्ड, सिबिल, लोन, जाति, ITR...)",
              color = Color(0xFF475569),
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PortalNavy, modifier = Modifier.size(24.dp))
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_services_input"),
          shape = RoundedCornerShape(12.dp),
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
    }

    // Dedicated Share App Promotion Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF25D366))
        )
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ऐप शेयर करें (Share App)",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A)
                )
                Text(
                  text = "अपने दोस्तों व परिवार को भेजें, घर बैठे सेवा पाएं",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF64748B)
                )
              }
            }

            Surface(
              color = Color(0xFFDCFCE7),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "1-क्लिक शेयर",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF16A34A),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // WhatsApp Share Button
            Button(
              onClick = { viewModel.shareApp(context, viaWhatsApp = true) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier
                .weight(1.3f)
                .height(40.dp)
                .testTag("home_whatsapp_share_btn")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("व्हाट्सएप पर शेयर", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            // More Options / Copy Link / QR
            OutlinedButton(
              onClick = { viewModel.openShareDialog() },
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, PortalNavy),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .testTag("home_more_share_btn")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = PortalNavy, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("QR व अन्य ऐप", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PortalNavy)
            }
          }
        }
      }
    }

    // Categories Horizontal Row
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "सेवा श्रेणियां (Select Category):",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(end = 16.dp)
        ) {
          items(ServiceCategory.values()) { category ->
            val isSelected = selectedCategory == category
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setCategory(category) },
              label = {
                Text(
                  text = category.titleHindi,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                  fontSize = 12.5.sp,
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
      }
    }

    item { Spacer(modifier = Modifier.height(14.dp)) }

    // Popular Services Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isNotBlank() || selectedCategory != ServiceCategory.ALL)
            "उपलब्ध सेवाएं (${filteredServices.size})"
          else
            "अधिकृत रेट लिस्ट व ऑनलाइन डिस्काउंट",
          fontSize = 15.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )

        Text(
          text = "सब देखें",
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = PortalBlue,
          modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.SERVICES) }
        )
      }
    }

    item { Spacer(modifier = Modifier.height(8.dp)) }

    // Services Items List
    if (filteredServices.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8)))
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = Color(0xFF475569),
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "कोई सेवा नहीं मिली",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "कृपया दूसरा शब्द खोजें या कस्टम कार्य चुनें।",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF334155)
            )
          }
        }
      }
    } else {
      items(filteredServices) { service ->
        ServiceCardWithRateAndDiscount(
          service = service,
          onApplyClick = { viewModel.requestApplyService(activity, service) }
        )
      }
    }

    // Safety & Rules Trust Banner
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8))
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Rules",
              tint = PortalNavy,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "मुख्य नियम व शर्तें (Terms & Conditions)",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = PortalNavy
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "1. ई-श्रम कार्ड एवं केवाईसी 100% फ्री करवाने के लिए काउंटर पर या ऑनलाइन चैनल सब्सक्रिप्शन का प्रूफ दिखाना अनिवार्य है।",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
          Text(
            text = "2. 10% स्पेशल ऑनलाइन डिस्काउंट केवल इस ऑनलाइन पोर्टल के माध्यम से बुक की गई सशुल्क सेवाओं पर ही लागू होगा।",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
        }
      }
    }
  }
}

/**
 * Service Card showing:
 * 1. Title & Category in bold high-contrast
 * 2. EXACT checklist of required documents right below it
 * 3. 100% Free Badge OR Strike-through Original Rate + 10% Discounted Rate
 * 4. "दस्तावेज दें व आवेदन करें" CTA
 */
@Composable
fun ServiceCardWithRateAndDiscount(
  service: ServiceItem,
  onApplyClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onApplyClick() },
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    shape = RoundedCornerShape(12.dp),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (service.isFree) SuccessGreenDark else Color(0xFFCBD5E1)
      )
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Service Icon
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (service.isFree) SuccessGreenLight else PortalBlueLight)
            .border(
              1.dp,
              if (service.isFree) SuccessGreenDark else PortalNavy.copy(alpha = 0.2f),
              RoundedCornerShape(10.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          val icon = getServiceIcon(service.category, service.iconType)
          Icon(
            imageVector = icon,
            contentDescription = service.nameEnglish,
            tint = if (service.isFree) SuccessGreenDark else PortalNavy,
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Title & Timing
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = service.nameHindi,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )
            if (service.isFree) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(SuccessGreenDark, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "100% FREE",
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }
            } else if (service.isPopular) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(SaffronGold, RoundedCornerShape(4.dp))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "पॉपुलर",
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }
            }
          }

          Text(
            text = "समय: ${service.estimatedTime}",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F52BA)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Color(0xFFE2E8F0))
      Spacer(modifier = Modifier.height(8.dp))

      // REQUIRED DOCUMENTS LIST - DISPLAYED RIGHT UNDERNEATH AS REQUESTED
      Text(
        text = "📄 आवश्यक दस्तावेज (Required Documents):",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF0F172A)
      )
      Spacer(modifier = Modifier.height(4.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
          .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        service.requiredDocs.forEach { doc ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreenDark,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = doc,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Rate Breakdown & Action Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          if (service.isFree) {
            Box(
              modifier = Modifier
                .background(SuccessGreenLight, RoundedCornerShape(4.dp))
                .border(1.dp, SuccessGreenDark, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "शुल्क: ₹0 (बिल्कुल फ्री)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = SuccessGreenDark
              )
            }
            if (service.freeConditionText.isNotBlank()) {
              Text(
                text = service.freeConditionText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309)
              )
            }
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "₹${service.originalFee}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                textDecoration = TextDecoration.LineThrough
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(SuccessGreenLight, RoundedCornerShape(4.dp))
                  .border(1.dp, SuccessGreenDark, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "₹${service.applicationFee} (10% छूट)",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Black,
                  color = SuccessGreenDark
                )
              }
            }
            Text(
              text = "ऑनलाइन बुकिंग फ्लैट 10% डिस्काउंट",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F52BA)
            )
          }
        }

        Button(
          onClick = onApplyClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (service.isFree) SuccessGreenDark else PortalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("apply_service_${service.id}")
        ) {
          Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (service.isFree) "फ्री अप्लाई करें" else "आवेदन करें",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}

fun getServiceIcon(category: ServiceCategory, iconType: String): ImageVector {
  return when (category) {
    ServiceCategory.PAN_CARD -> Icons.Default.CreditCard
    ServiceCategory.AADHAAR_ID -> when (iconType) {
      "how_to_vote" -> Icons.Default.HowToVote
      "badge" -> Icons.Default.Badge
      "health_and_safety" -> Icons.Default.HealthAndSafety
      else -> Icons.Default.Verified
    }
    ServiceCategory.CERTIFICATES -> Icons.Default.Description
    ServiceCategory.SARKARI_EXAM -> Icons.Default.Work
    ServiceCategory.LOAN_CIBIL -> Icons.Default.Payments
    ServiceCategory.BUSINESS_TAX -> Icons.Default.Storefront
    ServiceCategory.BILLS_RECHARGE -> Icons.Default.Bolt
    ServiceCategory.BANKING_PF -> Icons.Default.Badge
    ServiceCategory.CUSTOM_WORK -> Icons.Default.Assignment
    else -> Icons.Default.Description
  }
}
