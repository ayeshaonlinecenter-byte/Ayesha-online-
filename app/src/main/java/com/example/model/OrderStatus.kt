package com.example.model

enum class OrderStatus(val titleHindi: String, val titleEnglish: String) {
  PENDING_PAYMENT("भुगतान लंबित", "Pending Payment"),
  PAYMENT_SUCCESSFUL("भुगतान सफल", "Payment Successful"),
  PAYMENT_VERIFIED("भुगतान सफल - कार्य स्वीकार", "Paid & Accepted"),
  IN_PROGRESS("कार्य प्रगति पर है", "In Progress"),
  COMPLETED("कार्य पूर्ण - स्लिप/दस्तावेज तैयार", "Completed - Ready"),
  REJECTED("अस्वीकृत - दस्तावेज त्रुटि", "Rejected / Incomplete")
}

enum class ServiceCategory(val titleHindi: String, val titleEnglish: String) {
  ALL("सभी सेवाएं", "All Services"),
  PAN_CARD("पैन कार्ड", "PAN Card"),
  AADHAAR_ID("आधार, वोटर व समग्र", "Aadhaar, Voter & ID"),
  CERTIFICATES("प्रमाण पत्र", "Certificates"),
  SARKARI_EXAM("सरकारी नौकरी/फॉर्म", "Govt Jobs & Forms"),
  LOAN_CIBIL("लोन व सिबिल", "Loan & CIBIL"),
  BUSINESS_TAX("टैक्स व रजिस्ट्रेशन", "ITR & Business"),
  BILLS_RECHARGE("बिल व यात्रा", "Bills & Travel"),
  BANKING_PF("श्रम व बैंकिंग", "E-Shram & Banking"),
  CUSTOM_WORK("अन्य ऑनलाइन कार्य", "Custom Work")
}
