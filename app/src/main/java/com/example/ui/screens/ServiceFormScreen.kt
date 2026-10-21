package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PortalBlueLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.viewmodel.OnlineCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormScreen(
  viewModel: OnlineCenterViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val service by viewModel.selectedService.collectAsState()
  val formInput by viewModel.formInput.collectAsState()
  val formError by viewModel.formError.collectAsState()

  if (service == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("कृपया पहले सेवा चुनें।", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
    return
  }

  val currentService = service!!

  // Document file picker for PDF and Images
  val documentPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    uri?.let {
      try {
        context.contentResolver.takePersistableUriPermission(
          it,
          Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
      } catch (e: Exception) {
        // Ignore if not supported
      }
      val fileName = getFileNameFromUri(context, it) ?: "dastavej_${System.currentTimeMillis()}.jpg"
      val savedFile = copyUriToInternalStorage(context, it, fileName)
      val finalUriStr = if (savedFile != null) Uri.fromFile(savedFile).toString() else it.toString()
      val docEntry = "$fileName|$finalUriStr"
      viewModel.addAttachedDocument(docEntry)
      Toast.makeText(context, "दस्तावेज सुरक्षित हुआ: $fileName", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "ऑनलाइन आवेदन फॉर्म",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = currentService.nameHindi,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFFD54F),
              maxLines = 1
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
      // Service Header Card with "Only for Application Payment"
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(PortalNavy)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = currentService.nameHindi,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = PortalNavy
                )
                Text(
                  text = "अनुमानित समय: ${currentService.estimatedTime}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F52BA)
                )
              }

              // Only for Application / Receipt Payment badge
              Column(horizontalAlignment = Alignment.End) {
                Box(
                  modifier = Modifier
                    .background(SuccessGreenLight, RoundedCornerShape(6.dp))
                    .border(1.5.dp, SuccessGreenDark, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "ओनली फॉर आवेदन पेमेंट: ₹${currentService.applicationFee}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = SuccessGreenDark
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "बाकी पेमेंट रिसीप्ट मिलने के बाद",
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "📌 सूचना: ऐप पर केवल आवेदन व पावती रसीद का ₹${currentService.applicationFee} शुल्क लिया जा रहा है। बाकी कार्य शुल्क पावती जारी होने के बाद संचालक से संपर्क पर देय होगा।",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF78350F)
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // =====================================================================
      // CRITICAL REQUIREMENT 3 & 4:
      // REQUIRED DOCUMENTS LIST & DIRECT PDF / DOC UPLOAD RIGHT THERE
      // =====================================================================
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF94A3B8))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📄 आवश्यक दस्तावेज (दस्तावेज यहीं अपलोड करें):",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
            }
            Text(
              text = "ग्राहक नीचे दी गई सूची देखकर सीधे अपने फोन से PDF या फोटो अपलोड कर सकते हैं:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Required Document List with direct Upload buttons
            currentService.requiredDocs.forEach { docName ->
              val isAttached = formInput.attachedDocuments.any { it.contains(docName.take(4), ignoreCase = true) }

              Surface(
                color = if (isAttached) SuccessGreenLight else Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isAttached) SuccessGreenDark else Color(0xFFCBD5E1)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = if (isAttached) Icons.Default.CheckCircle else Icons.Default.AttachFile,
                      contentDescription = null,
                      tint = if (isAttached) SuccessGreenDark else PortalNavy,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = docName,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isAttached) SuccessGreenDark else Color(0xFF0F172A)
                    )
                  }

                  // Upload Button for this document
                  Button(
                    onClick = {
                      documentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (isAttached) SuccessGreenDark else PortalNavy,
                      contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.UploadFile,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (isAttached) "अपलोड है" else "PDF / फोटो चुनें",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Direct Browse Any PDF Document Button
            OutlinedButton(
              onClick = {
                documentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("browse_pdf_doc_btn")
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = PortalNavy, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "+ अन्य कोई भी PDF या फोटो दस्तावेज जोड़ें (Browse Files)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PortalNavy
              )
            }

            // Show Selected / Attached Files
            if (formInput.attachedDocuments.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "संलग्न दस्तावेज (${formInput.attachedDocuments.size} फाइलें):",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
              Spacer(modifier = Modifier.height(4.dp))

              formInput.attachedDocuments.forEach { fileName ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F1FC), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF0F52BA), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = fileName.split("|").firstOrNull() ?: fileName,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0F172A),
                      maxLines = 1
                    )
                  }
                  IconButton(
                    onClick = { viewModel.removeAttachedDocument(fileName) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
              }
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(14.dp)) }

      // Customer Details Header
      item {
        Text(
          text = "ग्राहक संपर्क विवरण (Customer Details):",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
      }

      item { Spacer(modifier = Modifier.height(8.dp)) }

      // Name Input
      item {
        OutlinedTextField(
          value = formInput.customerName,
          onValueChange = { name -> viewModel.updateFormInput { it.copy(customerName = name) } },
          label = { Text("ग्राहक का पूरा नाम (Full Name) *", fontWeight = FontWeight.Bold) },
          placeholder = { Text("उदा. राहुल कुमार / अंजलि शर्मा") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PortalNavy) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_name_input"),
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

      item { Spacer(modifier = Modifier.height(10.dp)) }

      // Phone Input
      item {
        OutlinedTextField(
          value = formInput.customerPhone,
          onValueChange = { phone ->
            if (phone.length <= 10 && phone.all { it.isDigit() }) {
              viewModel.updateFormInput { it.copy(customerPhone = phone) }
            }
          },
          label = { Text("मोबाइल नंबर (Mobile Number) *", fontWeight = FontWeight.Bold) },
          placeholder = { Text("10 अंकों का मोबाइल नंबर") },
          prefix = { Text("+91 ", fontWeight = FontWeight.Black, color = PortalNavy) },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PortalNavy) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_phone_input"),
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

      item { Spacer(modifier = Modifier.height(10.dp)) }

      // Father/Spouse Name
      item {
        OutlinedTextField(
          value = formInput.fatherOrSpouseName,
          onValueChange = { father -> viewModel.updateFormInput { it.copy(fatherOrSpouseName = father) } },
          label = { Text("पिता या पति का नाम (Father / Husband)", fontWeight = FontWeight.Bold) },
          placeholder = { Text("पिता या पति का नाम लिखें") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("father_spouse_input"),
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

      item { Spacer(modifier = Modifier.height(10.dp)) }

      // Address
      item {
        OutlinedTextField(
          value = formInput.address,
          onValueChange = { addr -> viewModel.updateFormInput { it.copy(address = addr) } },
          label = { Text("पता व पिनकोड (Address & PIN)", fontWeight = FontWeight.Bold) },
          placeholder = { Text("गांव/शहर, तहसील, जिला, पिनकोड") },
          leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = PortalNavy) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("address_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedBorderColor = PortalNavy,
            unfocusedBorderColor = Color(0xFF94A3B8)
          ),
          maxLines = 2
        )
      }

      item { Spacer(modifier = Modifier.height(10.dp)) }

      // Additional Notes
      item {
        OutlinedTextField(
          value = formInput.additionalDetails,
          onValueChange = { notes -> viewModel.updateFormInput { it.copy(additionalDetails = notes) } },
          label = { Text("विशेष निर्देश (Special Instructions / Remarks)", fontWeight = FontWeight.Bold) },
          placeholder = { Text("कोई विशेष सुधार या निर्देश यदि हो तो यहाँ लिखें...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("additional_details_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedBorderColor = PortalNavy,
            unfocusedBorderColor = Color(0xFF94A3B8)
          ),
          minLines = 2,
          maxLines = 3
        )
      }

      item { Spacer(modifier = Modifier.height(10.dp)) }

      // WhatsApp / Document Readiness Checkbox
      item {
        Surface(
          color = Color.White,
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF94A3B8)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = formInput.documentsReady,
              onCheckedChange = { checked -> viewModel.updateFormInput { it.copy(documentsReady = checked) } },
              colors = CheckboxDefaults.colors(checkedColor = PortalNavy)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "मेरे दस्तावेज अपलोड हो चुके हैं या मैं सीधे संचालक को व्हाट्सएप पर भेज दूंगा/दूंगी।",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }
        }
      }

      // Error Message if any
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

      item { Spacer(modifier = Modifier.height(16.dp)) }

      // Proceed Button
      item {
        Button(
          onClick = { viewModel.proceedToPayment() },
          colors = ButtonDefaults.buttonColors(
            containerColor = PortalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("proceed_to_payment_btn")
        ) {
          Icon(Icons.Default.Payment, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "आवेदन व पावती पेमेंट पर आगे बढ़ें (Pay ₹${currentService.applicationFee})",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}

// Helper to extract file name from android Uri
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
  var result: String? = null
  if (uri.scheme == "content") {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
      if (it.moveToFirst()) {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1) {
          result = it.getString(nameIndex)
        }
      }
    }
  }
  if (result == null) {
    result = uri.path
    val cut = result?.lastIndexOf('/')
    if (cut != null && cut != -1) {
      result = result?.substring(cut + 1)
    }
  }
  return result
}

private fun copyUriToInternalStorage(context: android.content.Context, uri: Uri, fileName: String): java.io.File? {
  return try {
    val docsDir = java.io.File(context.filesDir, "customer_docs")
    if (!docsDir.exists()) docsDir.mkdirs()
    val destFile = java.io.File(docsDir, "${System.currentTimeMillis()}_$fileName")
    context.contentResolver.openInputStream(uri)?.use { input ->
      java.io.FileOutputStream(destFile).use { output ->
        input.copyTo(output)
      }
    }
    destFile
  } catch (e: Exception) {
    android.util.Log.e("ServiceFormScreen", "Error copying file", e)
    null
  }
}
