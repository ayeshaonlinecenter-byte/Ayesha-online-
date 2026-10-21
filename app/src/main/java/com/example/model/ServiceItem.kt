package com.example.model

data class ServiceItem(
  val id: String = "",
  val nameHindi: String = "",
  val nameEnglish: String = "",
  val category: ServiceCategory = ServiceCategory.ALL,
  val originalFee: Int = 0,
  val discountPercent: Int = 10,
  val isFree: Boolean = false,
  val freeConditionText: String = "",
  val estimatedTime: String = "",
  val iconType: String = "",
  val requiredDocs: List<String> = emptyList(),
  val description: String = "",
  val isPopular: Boolean = false
) {
  // Calculates net fee after 10% flat online discount or ₹0 if free
  val applicationFee: Int get() {
    if (isFree) return 0
    val discount = (originalFee * discountPercent) / 100
    return (originalFee - discount).coerceAtLeast(0)
  }

  val totalFee: Int get() = applicationFee
}
