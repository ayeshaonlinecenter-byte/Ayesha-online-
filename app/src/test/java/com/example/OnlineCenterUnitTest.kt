package com.example

import com.example.model.OrderStatus
import com.example.model.ServiceCategory
import com.example.model.ServiceItem
import org.junit.Assert.assertEquals
import org.junit.Test

class OnlineCenterUnitTest {
  @Test
  fun testServiceItemTotalFeeCalculation() {
    val service = ServiceItem(
      id = "pan_new",
      nameHindi = "नया पैन कार्ड",
      nameEnglish = "New PAN Card",
      category = ServiceCategory.PAN_CARD,
      originalFee = 30,
      discountPercent = 0,
      estimatedTime = "3 दिन",
      iconType = "credit_card",
      requiredDocs = listOf("आधार कार्ड"),
      description = "NSDL नया पैन कार्ड"
    )

    assertEquals(30, service.totalFee)
  }

  @Test
  fun testOrderStatusTitles() {
    assertEquals("भुगतान सफल - कार्य स्वीकार", OrderStatus.PAYMENT_VERIFIED.titleHindi)
    assertEquals("Paid & Accepted", OrderStatus.PAYMENT_VERIFIED.titleEnglish)
  }
}
