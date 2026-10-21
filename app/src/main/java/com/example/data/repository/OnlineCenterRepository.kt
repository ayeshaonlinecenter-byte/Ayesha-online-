package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.model.OrderStatus
import com.example.model.ServiceCategory
import com.example.model.ServiceItem
import com.example.model.ServiceOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class OnlineCenterRepository(private val context: Context) {
  private val auth = FirebaseAuth.getInstance()
  private val db = FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))

  private val OPERATOR_EMAIL = "ayeshaonlinecenter@gmail.com"

  suspend fun ensureDefaultServicesSeeded() {
    try {
      val snapshot = db.collection("services").limit(1).get().await()
      if (snapshot.isEmpty) {
        val defaultServices = getDefaultServicesList()
        val batch = db.batch()
        defaultServices.forEach { service ->
          val docRef = db.collection("services").document(service.id)
          batch.set(docRef, service.toFirestoreMap())
        }
        batch.commit().await()
      }
    } catch (e: Exception) {
      android.util.Log.w("OnlineCenterRepo", "Could not seed default services to Firestore: ${e.message}")
    }
  }

  fun getAllServices(): Flow<List<ServiceItem>> = flow {
    // Always emit default services immediately so UI is never empty
    val defaults = getDefaultServicesList()
    emit(defaults)
    try {
      db.collection("services")
        .snapshots()
        .collect { snapshot ->
          val remoteList = snapshot.documents.mapNotNull { it.toServiceItem() }
          if (remoteList.isNotEmpty()) {
            emit(remoteList)
          } else {
            emit(defaults)
          }
        }
    } catch (e: Exception) {
      android.util.Log.e("OnlineCenterRepo", "Error listening to services, falling back to defaults", e)
      emit(defaults)
    }
  }

  fun getAllOrders(guestPhone: String? = null): Flow<List<ServiceOrder>> = flow {
    try {
      val user = auth.currentUser
      val isOp = user?.email?.equals(OPERATOR_EMAIL, ignoreCase = true) == true

      val query = when {
        isOp -> db.collection("orders").orderBy("orderTimestamp", Query.Direction.DESCENDING)
        !guestPhone.isNullOrBlank() -> db.collection("orders")
          .whereEqualTo("customerPhone", guestPhone.trim())
          .orderBy("orderTimestamp", Query.Direction.DESCENDING)
        user != null -> db.collection("orders")
          .whereEqualTo("userId", user.uid)
          .orderBy("orderTimestamp", Query.Direction.DESCENDING)
        else -> {
          emit(emptyList())
          return@flow
        }
      }

      emitAll(query.snapshots().map { snapshot ->
        snapshot.documents.mapNotNull { it.toServiceOrder() }
      })
    } catch (e: Exception) {
      android.util.Log.e("OnlineCenterRepo", "Error fetching orders", e)
      emit(emptyList())
    }
  }

  suspend fun getOrderBySlipNumber(slipNumber: String): ServiceOrder? {
    return try {
      val snapshot = db.collection("orders")
        .whereEqualTo("slipNumber", slipNumber)
        .limit(1)
        .get()
        .await()
      snapshot.documents.firstOrNull()?.toServiceOrder()
    } catch (e: Exception) {
      android.util.Log.e("OnlineCenterRepo", "Error getting order by slip: $slipNumber", e)
      null
    }
  }

  suspend fun createPaidOrder(
    service: ServiceItem,
    customerName: String,
    customerPhone: String,
    fatherOrSpouseName: String,
    address: String,
    attachedDocuments: List<String>,
    additionalDetails: String,
    paymentMethod: String,
    transactionRef: String
  ): ServiceOrder {
    val user = auth.currentUser
    val userId = user?.uid ?: throw IllegalStateException("User must be signed in.")
    val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val randomSuffix = Random.nextInt(1000, 9999)
    val slipNumber = "AOK-$dateStr-$randomSuffix"

    val order = ServiceOrder(
      slipNumber = slipNumber,
      serviceId = service.id,
      serviceNameHindi = service.nameHindi,
      serviceNameEnglish = service.nameEnglish,
      category = service.category,
      customerName = customerName.trim(),
      customerPhone = customerPhone.trim(),
      fatherOrSpouseName = fatherOrSpouseName.trim(),
      address = address.trim(),
      attachedDocuments = attachedDocuments,
      additionalDetails = additionalDetails.trim(),
      applicationFeePaid = service.applicationFee,
      paymentStatus = if (service.isFree) "FREE_OFFER" else "PAID",
      paymentMethod = if (service.isFree) "100%_FREE" else paymentMethod,
      transactionRef = if (transactionRef.isBlank()) "TXN${System.currentTimeMillis()}" else transactionRef.trim(),
      orderTimestamp = System.currentTimeMillis(),
      status = if (service.isFree) OrderStatus.PAYMENT_VERIFIED else OrderStatus.PENDING_PAYMENT,
      operatorNotes = if (service.isFree)
        "100% फ्री ऑफर आवेदन स्वीकार। संचालक द्वारा सत्यापन कर पावती तैयार की जाएगी।"
      else
        "भुगतान लंबित (Pending)। संचालक द्वारा पेमेंट सत्यापन के बाद स्लिप जनरेट की जाएगी।",
      govtAckNumber = ""
    )

    try {
      val orderMap = hashMapOf(
        "userId" to userId,
        "customerEmail" to (user?.email ?: "guest"),
        "slipNumber" to order.slipNumber,
        "serviceId" to order.serviceId,
        "serviceNameHindi" to order.serviceNameHindi,
        "serviceNameEnglish" to order.serviceNameEnglish,
        "category" to order.category.name,
        "customerName" to order.customerName,
        "customerPhone" to order.customerPhone,
        "fatherOrSpouseName" to order.fatherOrSpouseName,
        "address" to order.address,
        "attachedDocuments" to order.attachedDocuments,
        "additionalDetails" to order.additionalDetails,
        "applicationFeePaid" to order.applicationFeePaid,
        "paymentStatus" to order.paymentStatus,
        "paymentMethod" to order.paymentMethod,
        "transactionRef" to order.transactionRef,
        "orderTimestamp" to order.orderTimestamp,
        "status" to order.status.name,
        "operatorNotes" to order.operatorNotes,
        "govtAckNumber" to order.govtAckNumber,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )

      val docRef = db.collection("orders").document()
      docRef.set(orderMap).await()
    } catch (e: Exception) {
      android.util.Log.e("OnlineCenterRepo", "Error saving order to Firestore: ${e.message}", e)
    }

    return order
  }

  suspend fun updateOrderStatus(
    orderSlipNumber: String,
    newStatus: OrderStatus,
    operatorNotes: String,
    govtAckNumber: String,
    finalDocumentUrl: String = ""
  ) {
    val snapshot = db.collection("orders")
      .whereEqualTo("slipNumber", orderSlipNumber)
      .limit(1)
      .get()
      .await()
    
    val doc = snapshot.documents.firstOrNull() ?: return
    val updateMap = mutableMapOf<String, Any>(
        "status" to newStatus.name,
        "operatorNotes" to operatorNotes,
        "govtAckNumber" to govtAckNumber,
        "updatedAt" to FieldValue.serverTimestamp()
    )
    if (finalDocumentUrl.isNotBlank()) {
        updateMap["finalDocumentUrl"] = finalDocumentUrl
    }
    
    doc.reference.update(updateMap).await()
  }

  suspend fun deleteOrder(orderSlipNumber: String) {
    try {
      val snapshot = db.collection("orders")
        .whereEqualTo("slipNumber", orderSlipNumber)
        .limit(1)
        .get()
        .await()
      
      snapshot.documents.firstOrNull()?.reference?.delete()?.await()
    } catch (e: Exception) {
      android.util.Log.e("OnlineCenterRepo", "Error deleting order: $orderSlipNumber", e)
    }
  }

  suspend fun addCustomService(item: ServiceItem) {
    db.collection("services").document(item.id).set(item.toFirestoreMap()).await()
  }

  private fun ServiceItem.toFirestoreMap() = hashMapOf(
    "id" to id,
    "nameHindi" to nameHindi,
    "nameEnglish" to nameEnglish,
    "category" to category.name,
    "originalFee" to originalFee,
    "discountPercent" to discountPercent,
    "isFree" to isFree,
    "freeConditionText" to freeConditionText,
    "estimatedTime" to estimatedTime,
    "iconType" to iconType,
    "requiredDocs" to requiredDocs,
    "description" to description,
    "isPopular" to isPopular
  )

  private fun getDefaultServicesList(): List<ServiceItem> {
    return listOf(
      ServiceItem(
        id = "pan_card_fix",
        nameHindi = "पैन कार्ड (नया / सुधार)",
        nameEnglish = "PAN Card (New / Correction - Fix)",
        category = ServiceCategory.PAN_CARD,
        originalFee = 200,
        discountPercent = 10,
        estimatedTime = "3 - 5 कार्य दिवस",
        iconType = "credit_card",
        requiredDocs = listOf("आधार कार्ड PDF / फोटो", "पासपोर्ट साइज फोटो", "हस्ताक्षर (Signature PDF)"),
        description = "NSDL / UTIITSL नया पैन कार्ड आवेदन अथवा सुधार कार्य। फिक्स रेट ₹200 (ऑनलाइन 10% छूट: ₹180)।",
        isPopular = true
      ),
      ServiceItem(
        id = "voter_id_apply",
        nameHindi = "वोटर आईडी (नया फॉर्म आवेदन)",
        nameEnglish = "Voter ID Card (Form 6 Apply)",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "15 - 20 दिन",
        iconType = "how_to_vote",
        requiredDocs = listOf("आधार कार्ड PDF", "आयु प्रमाण पत्र", "निवास प्रमाण", "पासपोर्ट फोटो"),
        description = "भारत निर्वाचन आयोग (ECI) पोर्टल पर नया मतदाता पहचान पत्र फॉर्म 6 ऑनलाइन आवेदन।"
      ),
      ServiceItem(
        id = "voter_id_download",
        nameHindi = "वोटर आईडी डाउनलोड व प्रिंट",
        nameEnglish = "Voter ID (e-EPIC) Download & Print",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 50,
        discountPercent = 10,
        estimatedTime = "हाथों-हाथ (10 मिनट)",
        iconType = "print",
        requiredDocs = listOf("EPIC नंबर या रेफरेंस नंबर", "रजिस्टर्ड मोबाइल नंबर"),
        description = "डिजिटल ई-वोटर कार्ड त्वरित डाउनलोड व हाई-क्वालिटी प्रिंट।"
      ),
      ServiceItem(
        id = "ayushman_health_card",
        nameHindi = "आयुष्मान कार्ड / हेल्थ कार्ड (ABHA ID)",
        nameEnglish = "Ayushman Bharat / ABHA Health Card",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 50,
        discountPercent = 100,
        isFree = true,
        freeConditionText = "100% बिल्कुल फ्री (FREE)",
        estimatedTime = "हाथों-हाथ (24 घंटे)",
        iconType = "health_and_safety",
        requiredDocs = listOf("आधार कार्ड PDF", "राशन कार्ड या समग्र आईडी", "मोबाइल OTP"),
        description = "प्रधानमंत्री जन आरोग्य योजना (PMJAY) ₹5 लाख मुफ्त अस्पताल इलाज कार्ड व डिजिटल हेल्थ आईडी।",
        isPopular = true
      ),
      ServiceItem(
        id = "eshram_card_kyc",
        nameHindi = "ई-श्रम कार्ड ई-केवाईसी (E-Shram KYC)",
        nameEnglish = "E-Shram Card KYC & Update",
        category = ServiceCategory.BANKING_PF,
        originalFee = 30,
        discountPercent = 100,
        isFree = true,
        freeConditionText = "चैनल सब्सक्राइब करने पर फ्री",
        estimatedTime = "हाथों-हाथ",
        iconType = "badge",
        requiredDocs = listOf("ई-श्रम UAN नंबर / कार्ड फोटो", "आधार कार्ड PDF", "चैनल सब्सक्रिप्शन प्रूफ"),
        description = "श्रम मंत्रालय पोर्टल पर ई-श्रम प्रोफाइल व बैंक खाता ई-केवाईसी (चैनल सब्सक्राइब करने पर 100% फ्री)।",
        isPopular = true
      ),
      ServiceItem(
        id = "eshram_card_new",
        nameHindi = "नया ई-श्रम कार्ड रजिस्ट्रेशन",
        nameEnglish = "New E-Shram Card Registration",
        category = ServiceCategory.BANKING_PF,
        originalFee = 50,
        discountPercent = 100,
        isFree = true,
        freeConditionText = "चैनल सब्सक्राइब करने पर फ्री",
        estimatedTime = "हाथों-हाथ",
        iconType = "badge",
        requiredDocs = listOf("आधार कार्ड PDF", "बैंक पासबुक PDF", "आधार लिंक्ड मोबाइल", "चैनल सब्सक्रिप्शन प्रूफ"),
        description = "असंगठित कामगारों हेतु नया ई-श्रम कार्ड व 12-अंक UAN नंबर (चैनल सब्सक्राइब करने पर बिल्कुल फ्री)।",
        isPopular = true
      ),
      ServiceItem(
        id = "samagra_id_kyc",
        nameHindi = "समग्र आईडी ई-केवाईसी (Samagra e-KYC)",
        nameEnglish = "Samagra ID e-KYC & Aadhaar Link",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 30,
        discountPercent = 10,
        estimatedTime = "हाथों-हाथ",
        iconType = "assignment_ind",
        requiredDocs = listOf("समग्र 9-अंकीय सदस्य आईडी", "आधार कार्ड PDF"),
        description = "समग्र पोर्टल पर आधार ई-केवाईसी व जन्मतिथि/नाम सुधार लिंकिंग।"
      ),
      ServiceItem(
        id = "sambal_card_2",
        nameHindi = "संबल कार्ड 2.0 ऑनलाइन रजिस्ट्रेशन",
        nameEnglish = "Sambal Card 2.0 Registration",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "7 - 10 दिन",
        iconType = "how_to_vote",
        requiredDocs = listOf("समग्र आईडी PDF", "आधार कार्ड PDF", "बैंक पासबुक PDF", "पासपोर्ट फोटो"),
        description = "मुख्यमंत्री जन कल्याण संबल योजना 2.0 नया पंजीयन ऑनलाइन फॉर्म आवेदन।",
        isPopular = true
      ),
      ServiceItem(
        id = "udyam_registration",
        nameHindi = "उद्यम रजिस्ट्रेशन (MSME / उद्योग आधार)",
        nameEnglish = "Udyam MSME Registration Certificate",
        category = ServiceCategory.BUSINESS_TAX,
        originalFee = 250,
        discountPercent = 10,
        estimatedTime = "24 - 48 घंटे",
        iconType = "work",
        requiredDocs = listOf("आधार कार्ड PDF", "PAN कार्ड PDF", "व्यवसाय का नाम व पता", "बैंक पासबुक"),
        description = "एमएसएमई मंत्रालय से सरकारी उद्योग रजिस्ट्रेशन सर्टिफिकेट व बैंक लोन पात्रता लाभ।"
      ),
      ServiceItem(
        id = "gumasta_license",
        nameHindi = "गुमाश्ता पंजीयन (केवल ऑनलाइन आवेदन)",
        nameEnglish = "Gumasta / Shop Act License (Application Only)",
        category = ServiceCategory.BUSINESS_TAX,
        originalFee = 500,
        discountPercent = 10,
        estimatedTime = "3 - 5 कार्य दिवस",
        iconType = "storefront",
        requiredDocs = listOf("दुकानदार का आधार व पैन PDF", "दुकान का किरायानामा/बिजली बिल", "दुकान का बोर्ड फोटो"),
        description = "दुकान एवं स्थापना अधिनियम के अंतर्गत गुमाश्ता लाइसेंस हेतु ऑनलाइन आवेदन (फीस ₹500)।"
      ),
      ServiceItem(
        id = "itr_filing",
        nameHindi = "इनकम टैक्स रिटर्न (ITR File)",
        nameEnglish = "Income Tax Return (ITR Filing)",
        category = ServiceCategory.BUSINESS_TAX,
        originalFee = 1000,
        discountPercent = 10,
        estimatedTime = "24 - 48 घंटे",
        iconType = "payments",
        requiredDocs = listOf("PAN कार्ड PDF", "आधार कार्ड PDF", "बैंक 1 साल का स्टेटमेंट", "फॉर्म 16 / आय विवरण"),
        description = "टैक्स रिफंड, लोन अप्रूवल व वीजा हेतु अधिकृत सीए/एक्सपर्ट द्वारा ITR फाइलिंग।"
      ),
      ServiceItem(
        id = "online_job_form",
        nameHindi = "सरकारी नौकरी / कोई भी ऑनलाइन फॉर्म आवेदन",
        nameEnglish = "Online Job / Any Govt Form Apply",
        category = ServiceCategory.SARKARI_EXAM,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "1 - 2 घंटे",
        iconType = "work",
        requiredDocs = listOf("शैक्षणिक मार्कशीट PDF", "पासपोर्ट फोटो", "हस्ताक्षर PDF", "जाति/निवास प्रमाण"),
        description = "SSC, रेलवे, पुलिस, व्यापम या किसी भी सरकारी भर्ती का फॉर्म आवेदन (₹100 + पोर्टल/परीक्षा फीस)।",
        isPopular = true
      ),
      ServiceItem(
        id = "admit_card_download",
        nameHindi = "एडमिट कार्ड डाउनलोड व प्रिंट",
        nameEnglish = "Admit Card Download & Print",
        category = ServiceCategory.SARKARI_EXAM,
        originalFee = 30,
        discountPercent = 10,
        estimatedTime = "हाथों-हाथ (10 मिनट)",
        iconType = "confirmation_number",
        requiredDocs = listOf("रोल नंबर / रजिस्ट्रेशन नंबर", "जन्मतिथि / पासवर्ड"),
        description = "किसी भी परीक्षा का प्रवेश पत्र त्वरित डाउनलोड व लेजर प्रिंट।"
      ),
      ServiceItem(
        id = "electricity_bill_payment",
        nameHindi = "बिजली बिल भुगतान (₹10 प्रति ₹1000 पर)",
        nameEnglish = "Electricity Bill Payment (₹10 per ₹1000)",
        category = ServiceCategory.BILLS_RECHARGE,
        originalFee = 10,
        discountPercent = 10,
        estimatedTime = "हाथों-हाथ (तुरंत रसीद)",
        iconType = "bolt",
        requiredDocs = listOf("उपभोक्ता खाता संख्या (IVRS / Account No)", "मोबाइल नंबर"),
        description = "बिजली बिल भुगतान व तुरंत पक्की सरकारी रसीद (सेवा शुल्क ₹10 प्रति हजार)।"
      ),
      ServiceItem(
        id = "train_ticket_check",
        nameHindi = "ट्रेन टिकट सीट उपलब्धता जांच व विवरण",
        nameEnglish = "Train Ticket Seat Availability Check",
        category = ServiceCategory.BILLS_RECHARGE,
        originalFee = 30,
        discountPercent = 10,
        estimatedTime = "हाथों-हाथ (5 मिनट)",
        iconType = "confirmation_number",
        requiredDocs = listOf("यात्रा स्टेशन व गंतव्य", "यात्रा की तारीख", "यात्री का नाम व आयु"),
        description = "IRCTC कन्फर्म सीट, वेटिंग लिस्ट स्टेटस व तत्काल उपलब्धता जांच सेवा।"
      ),
      ServiceItem(
        id = "aadhaar_address_update",
        nameHindi = "आधार कार्ड पता सुधार (Aadhaar Address Update)",
        nameEnglish = "Aadhaar Address Update Online",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 150,
        discountPercent = 10,
        estimatedTime = "24 - 48 घंटे",
        iconType = "badge",
        requiredDocs = listOf("आधार कार्ड PDF", "नया पता प्रमाण PDF (निवास/बिजली बिल/राशन)", "OTP मोबाइल"),
        description = "UIDAI पोर्टल पर आधार कार्ड में नया पता ऑनलाइन अपडेट व रसीद।",
        isPopular = true
      ),
      ServiceItem(
        id = "aadhaar_pvc_card",
        nameHindi = "ऑरिजनल PVC प्लास्टिक आधार कार्ड",
        nameEnglish = "Original PVC Aadhaar Card Order",
        category = ServiceCategory.AADHAAR_ID,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "7 - 10 दिन (डाक द्वारा)",
        iconType = "badge",
        requiredDocs = listOf("आधार कार्ड PDF या 12-अंक नंबर", "मोबाइल नंबर"),
        description = "UIDAI से वॉटरप्रूफ, सिक्योर QR कोड वाला ऑरिजनल स्पीड पोस्ट प्लास्टिक आधार कार्ड।"
      ),
      ServiceItem(
        id = "cert_domicile",
        nameHindi = "निवास प्रमाण पत्र (Domicile Certificate)",
        nameEnglish = "Domicile / Residence Certificate",
        category = ServiceCategory.CERTIFICATES,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "7 - 10 कार्य दिवस",
        iconType = "home_work",
        requiredDocs = listOf("आधार कार्ड PDF", "समग्र आईडी / राशन कार्ड PDF", "स्वघोषणा पत्र", "फोटो"),
        description = "ई-डिस्ट्रिक्ट / लोक सेवा केंद्र द्वारा तहसील स्तर से जारी डिजिटल निवास प्रमाण पत्र।",
        isPopular = true
      ),
      ServiceItem(
        id = "cert_income",
        nameHindi = "आय प्रमाण पत्र (Income Certificate)",
        nameEnglish = "Income Certificate Application",
        category = ServiceCategory.CERTIFICATES,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "7 - 10 कार्य दिवस",
        iconType = "payments",
        requiredDocs = listOf("आधार कार्ड PDF", "समग्र आईडी PDF", "स्वप्रमाणित आय घोषणा पत्र", "फोटो"),
        description = "छात्रवृत्ति, एडमिशन व सरकारी योजनाओं हेतु अधिकृत डिजिटल आय प्रमाण पत्र।"
      ),
      ServiceItem(
        id = "cert_caste",
        nameHindi = "जाति प्रमाण पत्र आवेदन (Caste Certificate)",
        nameEnglish = "Caste Certificate (SC / ST / OBC)",
        category = ServiceCategory.CERTIFICATES,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "7 - 12 कार्य दिवस",
        iconType = "assignment_ind",
        requiredDocs = listOf("आधार कार्ड PDF", "परिवार के सदस्य का जाति रिकॉर्ड", "समग्र आईडी PDF", "फोटो"),
        description = "आरक्षण व सरकारी योजनाओं के लिए डिजिटल हस्ताक्षरित अधिकृत जाति प्रमाण पत्र।"
      ),
      ServiceItem(
        id = "cibil_score_check",
        nameHindi = "सिबिल स्कोर जांच व फुल क्रेडिट रिपोर्ट",
        nameEnglish = "CIBIL Score Check & Detailed Report",
        category = ServiceCategory.LOAN_CIBIL,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "तुरंत (5 मिनट)",
        iconType = "credit_card",
        requiredDocs = listOf("PAN कार्ड PDF", "आधार कार्ड PDF", "लिंक्ड मोबाइल नंबर"),
        description = "लोन व क्रेडिट कार्ड पात्रता हेतु विस्तृत सिबिल व क्रेडिट ब्यूरो स्कोर रिपोर्ट तुरंत प्राप्त करें।",
        isPopular = true
      ),
      ServiceItem(
        id = "loan_file_process",
        nameHindi = "लोन फाइल आवेदन व प्रोसेसिंग (फाइल अनुसार)",
        nameEnglish = "Loan File Apply & Processing (Case-wise)",
        category = ServiceCategory.LOAN_CIBIL,
        originalFee = 100,
        discountPercent = 10,
        estimatedTime = "केस अनुसार",
        iconType = "payments",
        requiredDocs = listOf("आधार व PAN कार्ड PDF", "बैंक 6 माह स्टेटमेंट PDF", "आय प्रमाण / सैलरी स्लिप / ITR"),
        description = "पर्सनल लोन, बिजनेस लोन या वाहन लोन फाइल लॉगिन व प्रोसेसिंग (केस-वाइज परामर्श | टोकन ₹90, बाकी फाइल अनुसार)।",
        isPopular = true
      )
    )
  }
}

fun DocumentSnapshot.toServiceItem(): ServiceItem? {
  return try {
    ServiceItem(
      id = getString("id") ?: id,
      nameHindi = getString("nameHindi") ?: "",
      nameEnglish = getString("nameEnglish") ?: "",
      category = try { ServiceCategory.valueOf(getString("category") ?: "ALL") } catch (_: Exception) { ServiceCategory.ALL },
      originalFee = getLong("originalFee")?.toInt() ?: 0,
      discountPercent = getLong("discountPercent")?.toInt() ?: 10,
      isFree = getBoolean("isFree") ?: false,
      freeConditionText = getString("freeConditionText") ?: "",
      estimatedTime = getString("estimatedTime") ?: "",
      iconType = getString("iconType") ?: "",
      requiredDocs = (get("requiredDocs") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
      description = getString("description") ?: "",
      isPopular = getBoolean("isPopular") ?: false
    )
  } catch (e: Exception) {
    android.util.Log.e("OnlineCenterRepo", "Error deserializing service $id", e)
    null
  }
}

fun DocumentSnapshot.toServiceOrder(): ServiceOrder? {
  return try {
    ServiceOrder(
      slipNumber = getString("slipNumber") ?: "",
      serviceId = getString("serviceId") ?: "",
      serviceNameHindi = getString("serviceNameHindi") ?: "",
      serviceNameEnglish = getString("serviceNameEnglish") ?: "",
      category = try { ServiceCategory.valueOf(getString("category") ?: "CUSTOM_WORK") } catch (_: Exception) { ServiceCategory.CUSTOM_WORK },
      customerName = getString("customerName") ?: "",
      customerPhone = getString("customerPhone") ?: "",
      fatherOrSpouseName = getString("fatherOrSpouseName") ?: "",
      address = getString("address") ?: "",
      attachedDocuments = (get("attachedDocuments") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
      additionalDetails = getString("additionalDetails") ?: "",
      applicationFeePaid = getLong("applicationFeePaid")?.toInt() ?: 0,
      paymentStatus = getString("paymentStatus") ?: "",
      paymentMethod = getString("paymentMethod") ?: "",
      transactionRef = getString("transactionRef") ?: "",
      orderTimestamp = getLong("orderTimestamp") ?: 0L,
      status = try { OrderStatus.valueOf(getString("status") ?: "PENDING_PAYMENT") } catch (_: Exception) { OrderStatus.PENDING_PAYMENT },
      operatorNotes = getString("operatorNotes") ?: "",
      govtAckNumber = getString("govtAckNumber") ?: "",
      finalDocumentUrl = getString("finalDocumentUrl") ?: ""
    )
  } catch (e: Exception) {
    android.util.Log.e("OnlineCenterRepo", "Error deserializing order $id", e)
    null
  }
}
