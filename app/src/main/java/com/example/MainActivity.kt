package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.OperatorLoginDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MySlipsScreen
import com.example.ui.screens.OperatorDashboardScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.ServiceCatalogScreen
import com.example.ui.screens.ServiceFormScreen
import com.example.ui.screens.SlipDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OnlineCenterViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
  private val viewModel: OnlineCenterViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: OnlineCenterViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val isOperator by viewModel.isOperator.collectAsState()
  val showOperatorLoginDialog by viewModel.showOperatorLoginDialog.collectAsState()
  val showShareDialog by viewModel.showShareDialog.collectAsState()
  val context = LocalContext.current
  val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
      androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
  ) {}

  LaunchedEffect(Unit) {
    viewModel.attemptAutoSignIn(context)
    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
  }

  // Handle Android back button
  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    viewModel.handleBack()
  }

  // Operator Authentication Dialog (Triggered when operator wants to log in)
  if (showOperatorLoginDialog) {
    OperatorLoginDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeOperatorLogin() }
    )
  }

  // Share App Dialog
  if (showShareDialog) {
    com.example.ui.components.ShareAppDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeShareDialog() }
    )
  }

  val showBottomNav = currentScreen in listOf(
    AppScreen.HOME,
    AppScreen.SERVICES,
    AppScreen.MY_SLIPS,
    AppScreen.OPERATOR_DASHBOARD
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          containerColor = Color.White,
          tonalElevation = 8.dp,
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("main_bottom_nav")
        ) {
          NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            icon = {
              Icon(
                imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Home"
              )
            },
            label = { Text("होम", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = PortalNavy,
              selectedTextColor = PortalNavy,
              indicatorColor = PortalBlueLight
            ),
            modifier = Modifier.testTag("nav_item_home")
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.SERVICES,
            onClick = { viewModel.navigateTo(AppScreen.SERVICES) },
            icon = {
              Icon(
                imageVector = if (currentScreen == AppScreen.SERVICES) Icons.Filled.Description else Icons.Outlined.Description,
                contentDescription = "Services"
              )
            },
            label = { Text("सेवाएं", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = PortalNavy,
              selectedTextColor = PortalNavy,
              indicatorColor = PortalBlueLight
            ),
            modifier = Modifier.testTag("nav_item_services")
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.MY_SLIPS,
            onClick = { viewModel.navigateTo(AppScreen.MY_SLIPS) },
            icon = {
              Icon(
                imageVector = if (currentScreen == AppScreen.MY_SLIPS) Icons.Filled.History else Icons.Outlined.History,
                contentDescription = "Slips"
              )
            },
            label = { Text("मेरी पर्चियां", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = PortalNavy,
              selectedTextColor = PortalNavy,
              indicatorColor = PortalBlueLight
            ),
            modifier = Modifier.testTag("nav_item_slips")
          )

          // Only operator can see and open operator dashboard tab
          if (isOperator) {
            NavigationBarItem(
              selected = currentScreen == AppScreen.OPERATOR_DASHBOARD,
              onClick = { viewModel.navigateTo(AppScreen.OPERATOR_DASHBOARD) },
              icon = {
                Icon(
                  imageVector = if (currentScreen == AppScreen.OPERATOR_DASHBOARD) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                  contentDescription = "Operator"
                )
              },
              label = { Text("संचालक", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PortalNavy,
                selectedTextColor = PortalNavy,
                indicatorColor = PortalBlueLight
              ),
              modifier = Modifier.testTag("nav_item_operator")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ScreenTransition"
      ) { screen ->
        when (screen) {
          AppScreen.HOME -> HomeScreen(viewModel = viewModel)
          AppScreen.SERVICES -> ServiceCatalogScreen(viewModel = viewModel)
          AppScreen.SERVICE_FORM -> ServiceFormScreen(viewModel = viewModel)
          AppScreen.PAYMENT_GATE -> PaymentScreen(viewModel = viewModel)
          AppScreen.SLIP_DETAIL -> SlipDetailScreen(viewModel = viewModel)
          AppScreen.MY_SLIPS -> MySlipsScreen(viewModel = viewModel)
          AppScreen.OPERATOR_DASHBOARD -> {
            if (isOperator) {
              OperatorDashboardScreen(viewModel = viewModel)
            } else {
              HomeScreen(viewModel = viewModel)
            }
          }
          AppScreen.TRACK_APP -> com.example.ui.screens.TrackAppScreen(viewModel = viewModel)
        }
      }
    }
  }
}
