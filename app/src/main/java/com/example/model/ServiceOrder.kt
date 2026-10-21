package com.example.model

data class ServiceOrder(
  val id: Long = 0,
  val slipNumber: String = "",
  val serviceId: String = "",
  val serviceNameHindi: String = "",
  val serviceNameEnglish: String = "",
  val category: ServiceCategory = ServiceCategory.CUSTOM_WORK,
  val customerName: String = "",
  val customerPhone: String = "",
  val fatherOrSpouseName: String = "",
  val address: String = "",
  val attachedDocuments: List<String> = emptyList(),
  val additionalDetails: String = "",
  val applicationFeePaid: Int = 0,
  val paymentStatus: String = "",
  val paymentMethod: String = "",
  val transactionRef: String = "",
  val orderTimestamp: Long = 0,
  val status: OrderStatus = OrderStatus.PENDING_PAYMENT,
  val operatorNotes: String = "",
  val govtAckNumber: String = "",
  val finalDocumentUrl: String = ""
)
