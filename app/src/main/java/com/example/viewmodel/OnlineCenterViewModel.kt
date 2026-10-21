package com.example.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.repository.OnlineCenterRepository
import com.example.model.OrderStatus
import com.example.model.ServiceCategory
import com.example.model.ServiceItem
import com.example.model.ServiceOrder
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class AppScreen {
  HOME,
  SERVICES,
  SERVICE_FORM,
  PAYMENT_GATE,
  SLIP_DETAIL,
  MY_SLIPS,
  OPERATOR_DASHBOARD,
  TRACK_APP
}

data class ServiceFormInput(
  val customerName: String = "",
  val customerPhone: String = "",
  val fatherOrSpouseName: String = "",
  val address: String = "",
  val attachedDocuments: List<String> = emptyList(), // PDF/Image files attached by user
  val additionalDetails: String = "",
  val documentsReady: Boolean = true
)

class OnlineCenterViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = OnlineCenterRepository(application)
  private val auth = FirebaseAuth.getInstance()
  private val credentialManager = CredentialManager.create(application)

  val OPERATOR_EMAIL = "ayeshaonlinecenter@gmail.com"

  // Auth & Operator State
  private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
  val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

  val isOperator: StateFlow<Boolean> = _currentUser.map { user ->
    user?.email?.equals(OPERATOR_EMAIL, ignoreCase = true) == true
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), auth.currentUser?.email?.equals(OPERATOR_EMAIL, ignoreCase = true) == true)

  private val _showOperatorLoginDialog = MutableStateFlow(false)
  val showOperatorLoginDialog: StateFlow<Boolean> = _showOperatorLoginDialog.asStateFlow()

  // App Navigation (Default to HOME for Guest Access)
  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _screenBackStack = mutableListOf<AppScreen>()

  // Category and Search Filter

  private val _selectedCategory = MutableStateFlow(ServiceCategory.ALL)
  val selectedCategory: StateFlow<ServiceCategory> = _selectedCategory.asStateFlow()


  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Selected Service & Order Form
  private val _selectedService = MutableStateFlow<ServiceItem?>(null)
  val selectedService: StateFlow<ServiceItem?> = _selectedService.asStateFlow()

  private val _formInput = MutableStateFlow(ServiceFormInput())
  val formInput: StateFlow<ServiceFormInput> = _formInput.asStateFlow()

  private val _formError = MutableStateFlow<String?>(null)
  val formError: StateFlow<String?> = _formError.asStateFlow()

  // Payment State
  private val _paymentMethod = MutableStateFlow("UPI_QR")
  val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

  private val _transactionRef = MutableStateFlow("")
  val transactionRef: StateFlow<String> = _transactionRef.asStateFlow()

  private val _paymentConfirmedChecked = MutableStateFlow(false)
  val paymentConfirmedChecked: StateFlow<Boolean> = _paymentConfirmedChecked.asStateFlow()

  private val _isProcessingPayment = MutableStateFlow(false)
  val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

  private val _activeSlipOrder = MutableStateFlow<ServiceOrder?>(null)
  val activeSlipOrder: StateFlow<ServiceOrder?> = _activeSlipOrder.asStateFlow()

  // Local session orders so guests instantly see freshly created slips
  private val _sessionOrders = MutableStateFlow<List<ServiceOrder>>(emptyList())

  // Services Catalog
  val allServices: StateFlow<List<ServiceItem>> = repository.getAllServices()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredServices: StateFlow<List<ServiceItem>> = combine(
    allServices,
    _selectedCategory,
    _searchQuery
  ) { list, category, query ->
    list.filter { item ->
      val matchesCategory = (category == ServiceCategory.ALL || item.category == category)
      val matchesQuery = query.isBlank() ||
          item.nameHindi.contains(query, ignoreCase = true) ||
          item.nameEnglish.contains(query, ignoreCase = true) ||
          item.description.contains(query, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Orders Flow: Operator sees all; User sees own orders
  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val allOrders: StateFlow<List<ServiceOrder>> = _currentUser.flatMapLatest { user ->
    val isOp = user?.email?.equals(OPERATOR_EMAIL, ignoreCase = true) == true
    if (isOp) {
      repository.getAllOrders()
    } else if (user != null) {
      repository.getAllOrders(user.uid)
    } else {
      flowOf(emptyList())
    }
  }.combine(_sessionOrders) { remoteOrders, sessionOrders ->
    // Combine and deduplicate by slipNumber
    val combinedMap = linkedMapOf<String, ServiceOrder>()
    sessionOrders.forEach { combinedMap[it.slipNumber] = it }
    remoteOrders.forEach { combinedMap[it.slipNumber] = it }
    combinedMap.values.toList().sortedByDescending { it.orderTimestamp }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // My Slips search query
  private val _slipSearchQuery = MutableStateFlow("")
  val slipSearchQuery: StateFlow<String> = _slipSearchQuery.asStateFlow()

  val filteredOrders: StateFlow<List<ServiceOrder>> = combine(
    allOrders,
    _slipSearchQuery
  ) { orders, query ->
    if (query.isBlank()) orders else {
      orders.filter { order ->
        order.slipNumber.contains(query, ignoreCase = true) ||
            order.customerPhone.contains(query, ignoreCase = true) ||
            order.customerName.contains(query, ignoreCase = true) ||
            order.serviceNameHindi.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    auth.addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      _currentUser.value = user
    }

    viewModelScope.launch {
      repository.ensureDefaultServicesSeeded()
    }

    // Monitor real-time status changes and post local notification
    val lastStatusMap = mutableMapOf<String, OrderStatus>()
    viewModelScope.launch {
      allOrders.collect { orders ->
        orders.forEach { order ->
          val prevStatus = lastStatusMap[order.slipNumber]
          if (prevStatus != null && prevStatus != order.status) {
            sendStatusNotification(
              context = getApplication(),
              title = "आयशा ऑनलाइन सेंटर: आवेदन अपडेट!",
              message = "स्लिप ${order.slipNumber} की स्थिति: ${order.status.titleHindi}। रिमार्क: ${order.operatorNotes.ifBlank { "सत्यापित" }}"
            )
          }
          lastStatusMap[order.slipNumber] = order.status
        }
      }
    }
  }

  private fun sendStatusNotification(context: Context, title: String, message: String) {
    try {
      val channelId = "ayesha_order_status_channel"
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = android.app.NotificationChannel(
          channelId,
          "आवेदन स्थिति अपडेट",
          android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = "संचालक द्वारा स्थिति अपडेट होने पर नोटिफिकेशन"
        }
        notificationManager.createNotificationChannel(channel)
      }

      val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ayesha_logo)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

      notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    } catch (e: Exception) {
      Log.e("Notification", "Error posting notification", e)
    }
  }

  // ==========================================
  // MOBILE OTP VERIFICATION LOGIC (GUEST MODE)
  // ==========================================

  fun requestApplyService(activity: Activity, service: ServiceItem) {
    if (auth.currentUser != null) {
      selectServiceForBooking(service)
    } else {
      onGoogleSignInClicked(activity)
    }
  }


  // ==========================================
  // OPERATOR AUTHENTICATION (ADMIN ONLY)
  // ==========================================

  fun openOperatorLogin() {
    _showOperatorLoginDialog.value = true
  }

  fun closeOperatorLogin() {
    _showOperatorLoginDialog.value = false
  }

  fun attemptAutoSignIn(context: Context) {
    if (auth.currentUser != null) return

    val clientId = try {
      context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
      return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(true)
      .setServerClientId(clientId)
      .setAutoSelectEnabled(true)
      .build()

    val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    viewModelScope.launch {
      try {
        val result = credentialManager.getCredential(context, request)
        handleSignInResult(result.credential, context)
      } catch (e: Exception) {
        Log.d("Auth", "Auto sign-in silent check: ${e.message}")
      }
    }
  }

  fun onGoogleSignInClicked(activity: Activity) {
    val clientId = try {
      activity.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
      Toast.makeText(activity, "Google Client ID प्राप्त नहीं हुआ", Toast.LENGTH_SHORT).show()
      return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    viewModelScope.launch {
      try {
        val result = credentialManager.getCredential(activity, request)
        handleSignInResult(result.credential, activity)
      } catch (e: GetCredentialCancellationException) {
        Log.w("Auth", "Sign-in cancelled")
      } catch (e: Exception) {
        Log.e("Auth", "Sign-in failed", e)
        Toast.makeText(activity, "लॉगिन विफल: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private suspend fun handleSignInResult(credential: androidx.credentials.Credential, context: Context) {
    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
      val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
      val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
      val authResult = auth.signInWithCredential(authCredential).await()
      val signedInEmail = authResult.user?.email

      if (signedInEmail?.equals(OPERATOR_EMAIL, ignoreCase = true) == true) {
        Toast.makeText(context, "स्वागत है! संचालक डैशबोर्ड अनलॉक हो गया।", Toast.LENGTH_LONG).show()
        navigateTo(AppScreen.OPERATOR_DASHBOARD)
      } else {
        Toast.makeText(context, "स्वागत है! आप लॉगिन हो गए हैं।", Toast.LENGTH_SHORT).show()
        _currentScreen.value = AppScreen.HOME
      }
    }
  }

  fun signOut(context: Context) {
    auth.signOut()
    viewModelScope.launch {
      try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
      } catch (e: Exception) {
        Log.e("Auth", "Clear state failed", e)
      }
    }
    Toast.makeText(context, "लॉगआउट हो गया। अतिथि मोड सक्रिय है।", Toast.LENGTH_SHORT).show()
    _currentScreen.value = AppScreen.HOME
  }

  // ==========================================
  // NAVIGATION
  // ==========================================

  fun navigateTo(screen: AppScreen) {
    // Protect operator dashboard: only allow if isOperator == true
    if (screen == AppScreen.OPERATOR_DASHBOARD && !isOperator.value) {
      openOperatorLogin()
      return
    }
    if (_currentScreen.value != screen) {
      _screenBackStack.add(_currentScreen.value)
      _currentScreen.value = screen
    }
  }

  fun handleBack(): Boolean {
    if (_screenBackStack.isNotEmpty()) {
      _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.lastIndex)
      return true
    }
    if (_currentScreen.value != AppScreen.HOME) {
      _currentScreen.value = AppScreen.HOME
      return true
    }
    return false
  }

  fun setCategory(category: ServiceCategory) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSlipSearchQuery(query: String) {
    _slipSearchQuery.value = query
  }

  fun selectServiceForBooking(service: ServiceItem) {
    _selectedService.value = service
    _formError.value = null
    _paymentConfirmedChecked.value = false
    _transactionRef.value = ""
    _formInput.value = ServiceFormInput(
      customerPhone = ""
    )
    navigateTo(AppScreen.SERVICE_FORM)
  }

  fun updateFormInput(update: (ServiceFormInput) -> ServiceFormInput) {
    _formInput.value = update(_formInput.value)
    _formError.value = null
  }

  fun addAttachedDocument(docName: String) {
    val currentDocs = _formInput.value.attachedDocuments
    if (!currentDocs.contains(docName)) {
      _formInput.value = _formInput.value.copy(attachedDocuments = currentDocs + docName)
    }
  }

  fun removeAttachedDocument(docName: String) {
    val currentDocs = _formInput.value.attachedDocuments
    _formInput.value = _formInput.value.copy(attachedDocuments = currentDocs.filter { it != docName })
  }

  fun proceedToPayment(): Boolean {
    val input = _formInput.value
    if (input.customerName.trim().length < 2) {
      _formError.value = "कृपया ग्राहक का पूरा नाम दर्ज करें।"
      return false
    }
    if (input.customerPhone.trim().length < 10) {
      _formError.value = "कृपया 10 अंकों का वैध मोबाइल नंबर दर्ज करें।"
      return false
    }
    _formError.value = null
    navigateTo(AppScreen.PAYMENT_GATE)
    return true
  }

  fun setPaymentMethod(method: String) {
    _paymentMethod.value = method
  }

  fun setTransactionRef(ref: String) {
    _transactionRef.value = ref
  }

  fun setPaymentConfirmedChecked(checked: Boolean) {
    _paymentConfirmedChecked.value = checked
  }

  // Complete Payment and Generate Slip Hand-to-Hand
  fun confirmPaymentAndGenerateSlip(onSuccess: (ServiceOrder) -> Unit) {
    val service = _selectedService.value ?: return
    val input = _formInput.value

    if (!_paymentConfirmedChecked.value) {
      _formError.value = "कृपया पहले भुगतान पूरा करें और टिक मार्क पर क्लिक करें।"
      return
    }

    _isProcessingPayment.value = true

    viewModelScope.launch {
      try {
        val order = repository.createPaidOrder(
          service = service,
          customerName = input.customerName,
          customerPhone = input.customerPhone,
          fatherOrSpouseName = input.fatherOrSpouseName,
          address = input.address,
          attachedDocuments = input.attachedDocuments,
          additionalDetails = input.additionalDetails,
          paymentMethod = _paymentMethod.value,
          transactionRef = _transactionRef.value.ifBlank { "UPI${System.currentTimeMillis()}" }
        )

        // Store into session orders so user sees it in My Slips immediately
        _sessionOrders.value = listOf(order) + _sessionOrders.value

        _activeSlipOrder.value = order
        _isProcessingPayment.value = false

        // Clear form inputs
        _formInput.value = ServiceFormInput()
        _paymentConfirmedChecked.value = false
        _transactionRef.value = ""

        // Navigate to Instant Slip Detail
        _screenBackStack.clear()
        _screenBackStack.add(AppScreen.HOME)
        _currentScreen.value = AppScreen.SLIP_DETAIL

        onSuccess(order)
      } catch (e: Exception) {
        _isProcessingPayment.value = false
        _formError.value = "आवेदन सुरक्षित करने में त्रुटि: ${e.message}"
      }
    }
  }

  fun viewSlip(order: ServiceOrder) {
    _activeSlipOrder.value = order
    navigateTo(AppScreen.SLIP_DETAIL)
  }

  fun updateOrderStatus(orderSlipNumber: String, newStatus: OrderStatus, notes: String, govtRef: String, finalDocumentUrl: String = "") {
    viewModelScope.launch {
      repository.updateOrderStatus(orderSlipNumber, newStatus, notes, govtRef, finalDocumentUrl)
    }
  }

  fun deleteOrder(orderSlipNumber: String) {
    if (isOperator.value) {
      viewModelScope.launch {
        repository.deleteOrder(orderSlipNumber)
      }
    }
  }

  // Share Slip via WhatsApp / Android Share Sheet
  fun shareSlip(context: Context, order: ServiceOrder) {
    val docsText = if (order.attachedDocuments.isNotEmpty()) {
      order.attachedDocuments.joinToString(", ")
    } else {
      "व्हाट्सएप पर उपलब्ध कराया जाएगा"
    }

    val message = """
      📄 *AYESHA ONLINE SEVA KENDRA*
      *आधिकारिक ई-सेवा पावती रसीद (Acknowledgment Slip)*
      
      *रसीद संख्या (Slip No):* ${order.slipNumber}
      *ग्राहक का नाम:* ${order.customerName}
      *मोबाइल नंबर:* ${order.customerPhone}
      *सेवा का नाम:* ${order.serviceNameHindi}
      *संलग्न दस्तावेज:* $docsText
      
      💰 *भुगतान स्थिति:*
      • *आवेदन व पावती शुल्क:* ₹${order.applicationFeePaid} (PAID - सफल)
      • *बाकी मुख्य पेमेंट:* पावती मिलने के बाद संचालक को संपर्क पर देय
      • *माध्यम:* ${order.paymentMethod} (Ref: ${order.transactionRef})
      • *वर्तमान स्थिति:* ${order.status.titleHindi}
      
      🏢 *आयशा ऑनलाइन सेंटर एवं साइबर कैफे*
      CSC ID: 7496836261 | ईमेल: ayeshaonlinecenter@gmail.com
      घर बैठे सभी सरकारी व ऑनलाइन काम करवाएं!
    """.trimIndent()

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Ayesha Online Center - Slip: ${order.slipNumber}")
      putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(shareIntent, "पावती स्लिप शेयर करें"))
  }

  fun contactCustomer(context: Context, phone: String, viaWhatsApp: Boolean = false) {
    try {
      if (viaWhatsApp) {
        val cleanPhone = phone.filter { it.isDigit() }
        val targetNumber = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val intent = Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse("https://api.whatsapp.com/send?phone=$targetNumber&text=नमस्ते! आयशा ऑनलाइन सेंटर से आपके आवेदन के संदर्भ में संपर्क किया जा रहा है।")
        }
        context.startActivity(intent)
      } else {
        val intent = Intent(Intent.ACTION_DIAL).apply {
          data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
      }
    } catch (e: Exception) {
      Toast.makeText(context, "संपर्क करने में असमर्थ", Toast.LENGTH_SHORT).show()
    }
  }

  // ==========================================
  // SHARE APP FEATURE
  // ==========================================
  val APP_SHARE_URL = "https://ais-pre-amof5m6ftifqwal6dp2wuy-749683626127.asia-northeast1.run.app"

  private val _showShareDialog = MutableStateFlow(false)
  val showShareDialog: StateFlow<Boolean> = _showShareDialog.asStateFlow()

  fun openShareDialog() {
    _showShareDialog.value = true
  }

  fun closeShareDialog() {
    _showShareDialog.value = false
  }

  fun getAppShareMessage(): String {
    return """
      🏛️ *आयशा ऑनलाइन सेंटर (Ayesha Online Center)* 🏛️
      *डिजिटल सेवा केंद्र | ई-डिस्ट्रिक्ट एवं ऑनलाइन सेवाएं*
      
      अब घर बैठे मोबाइल से करवाएं सभी सरकारी व ऑनलाइन काम!
      
      ✨ *मुख्य सेवाएं एवं विशेषताएं:*
      ✅ पैन कार्ड (नया/सुधार) - फ्लैट 10% ऑनलाइन छूट
      ✅ आयुष्मान भारत कार्ड (₹5 लाख फ्री इलाज) - 100% बिल्कुल फ्री
      ✅ ई-श्रम कार्ड (नया व ई-केवाईसी) - 100% फ्री
      ✅ सरकारी भर्ती/नौकरी फॉर्म, एडमिट कार्ड व रिजल्ट
      ✅ समग्र आईडी, संबल 2.0, जाति, आय व निवास प्रमाण पत्र
      ✅ उद्योग/उद्यम रजिस्ट्रेशन, गुमाश्ता व ITR फाइलिंग
      ✅ बिजली बिल भुगतान, IRCTC ट्रेन टिकट व सिबिल स्कोर जांच
      
      📄 *घर बैठे फॉर्म भरें, डॉक्यूमेंट भेजें और तुरंत डिजिटल पावती रसीद पाएं!*
      
      📲 *ऐप लिंक पर क्लिक करें और इस्तेमाल करें:*
      $APP_SHARE_URL
      
      🏢 *आयशा ऑनलाइन सेवा केंद्र*
      ईमेल: ayeshaonlinecenter@gmail.com
    """.trimIndent()
  }

  fun shareApp(context: Context, viaWhatsApp: Boolean = false) {
    val message = getAppShareMessage()
    try {
      if (viaWhatsApp) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
        }
        context.startActivity(intent)
      } else {
        val intent = Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_SUBJECT, "आयशा ऑनलाइन सेंटर - डिजिटल सेवा ऐप")
          putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, "आयशा ऑनलाइन सेंटर ऐप शेयर करें"))
      }
    } catch (e: Exception) {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "आयशा ऑनलाइन सेंटर - डिजिटल सेवा ऐप")
        putExtra(Intent.EXTRA_TEXT, message)
      }
      context.startActivity(Intent.createChooser(intent, "आयशा ऑनलाइन सेंटर ऐप शेयर करें"))
    }
  }

  fun copyAppLink(context: Context) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Ayesha Online Center App Link", APP_SHARE_URL)
    clipboardManager?.setPrimaryClip(clip)
    Toast.makeText(context, "ऐप लिंक कॉपी हो गया: $APP_SHARE_URL", Toast.LENGTH_SHORT).show()
  }
}
