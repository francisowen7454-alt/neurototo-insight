package com.example.model

object PaymentConstants {
    const val LIPA_NAMBA = "67757495"
    const val ACCOUNT_NAME = "Owen Mmehwa"
    const val SUPPORT_WHATSAPP = "+255759237454"
    const val SUPPORT_PHONE = "+255792237454"
}

enum class SubscriptionTier {
    FREE_TRIAL,
    MONTHLY,
    ANNUAL
}

enum class MobilePaymentProvider(val displayName: String, val dialCode: String, val country: String) {
    MPESA_TZ("Vodacom M-Pesa (TZ)", "*150*00#", "Tanzania"),
    TIGO_PESA("Mixx by Yas (Tigo Pesa)", "*150*01#", "Tanzania"),
    AIRTEL_MONEY_TZ("Airtel Money (TZ)", "*150*60#", "Tanzania"),
    HALOPESA("Halopesa (TZ)", "*150*88#", "Tanzania"),
    MPESA_KE("Safaricom M-Pesa (KE)", "*334#", "Kenya"),
    AIRTEL_MONEY_KE("Airtel Money (KE)", "*334#", "Kenya"),
    MTN_UG("MTN Mobile Money (UG)", "*165#", "Uganda"),
    VISA_MASTERCARD("Kadi ya Benki (Visa / Mastercard)", "", "East Africa")
}

data class SubscriptionPlan(
    val tier: SubscriptionTier,
    val titleSwahili: String,
    val titleEnglish: String,
    val priceSwahili: String,
    val priceEnglish: String,
    val billingCycleSwahili: String,
    val billingCycleEnglish: String,
    val featuresSwahili: List<String>,
    val featuresEnglish: List<String>,
    val isPopular: Boolean = false
)

data class DailyTip(
    val id: String,
    val titleSwahili: String,
    val titleEnglish: String,
    val bodySwahili: String,
    val bodyEnglish: String,
    val brainActivitySwahili: String,
    val brainActivityEnglish: String,
    val ageTarget: String,
    val iconName: String = "brain"
)

data class AdminAnalytics(
    val totalRegisteredParents: Int = 18450,
    val activeSubscribers: Int = 7820,
    val totalVideoWatches: Int = 45910,
    val completedSpecialistConsultations: Int = 1430,
    val totalRevenueTzs: Long = 117300000L,
    val userSatisfactionRate: Double = 98.4
)
