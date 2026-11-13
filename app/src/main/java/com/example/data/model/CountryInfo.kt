package com.example.data.model

data class CountryInfo(
    val code: String,          // ISO 3166-1 alpha-2 e.g. "NG", "US", "GB", "AE", "CA", "ZA"
    val name: String,          // Full English name
    val flagEmoji: String,     // Flag emoji e.g. "🇳🇬"
    val defaultCurrency: String, // ISO 4217 code e.g. "NGN", "USD"
    val callingCode: String,   // E.164 dial prefix e.g. "+234", "+1"
    val defaultUnit: String,   // "sq ft" or "m²"
    val activeOperations: Boolean = true // Administrative availability switch
)

object GlobalJurisdictionCatalog {
    val ALL_COUNTRIES: List<CountryInfo> = listOf(
        CountryInfo("NG", "Nigeria", "🇳🇬", "NGN", "+234", "sq ft", true),
        CountryInfo("US", "United States", "🇺🇸", "USD", "+1", "sq ft", true),
        CountryInfo("GB", "United Kingdom", "🇬🇧", "GBP", "+44", "sq ft", true),
        CountryInfo("AE", "United Arab Emirates", "🇦🇪", "AED", "+971", "sq ft", true),
        CountryInfo("CA", "Canada", "🇨🇦", "CAD", "+1", "sq ft", true),
        CountryInfo("ZA", "South Africa", "🇿🇦", "ZAR", "+27", "m²", true),
        CountryInfo("GH", "Ghana", "🇬🇭", "GHS", "+233", "m²", true),
        CountryInfo("KE", "Kenya", "🇰🇪", "KES", "+254", "m²", true),
        CountryInfo("FR", "France", "🇫🇷", "EUR", "+33", "m²", true),
        CountryInfo("DE", "Germany", "🇩🇪", "EUR", "+49", "m²", true),
        CountryInfo("ES", "Spain", "🇪🇸", "EUR", "+34", "m²", true),
        CountryInfo("PT", "Portugal", "🇵🇹", "EUR", "+351", "m²", true),
        CountryInfo("IT", "Italy", "🇮🇹", "EUR", "+39", "m²", true),
        CountryInfo("CH", "Switzerland", "🇨🇭", "CHF", "+41", "m²", true),
        CountryInfo("AU", "Australia", "🇦🇺", "AUD", "+61", "m²", true),
        CountryInfo("NZ", "New Zealand", "🇳🇿", "NZD", "+64", "m²", true),
        CountryInfo("SG", "Singapore", "🇸🇬", "SGD", "+65", "sq ft", true),
        CountryInfo("JP", "Japan", "🇯🇵", "JPY", "+81", "m²", true),
        CountryInfo("IN", "India", "🇮🇳", "INR", "+91", "sq ft", true),
        CountryInfo("BR", "Brazil", "🇧🇷", "BRL", "+55", "m²", true),
        CountryInfo("RW", "Rwanda", "🇷🇼", "RWF", "+250", "m²", true),
        CountryInfo("EG", "Egypt", "🇪🇬", "EGP", "+20", "m²", true),
        CountryInfo("SA", "Saudi Arabia", "🇸🇦", "SAR", "+966", "m²", true),
        CountryInfo("QA", "Qatar", "🇶🇦", "QAR", "+974", "m²", true)
    )

    fun getCountry(code: String): CountryInfo? {
        return ALL_COUNTRIES.find { it.code.equals(code, ignoreCase = true) }
    }

    fun getCurrencyForCountry(code: String): String {
        return getCountry(code)?.defaultCurrency ?: "USD"
    }

    fun getCallingCodeForCountry(code: String): String {
        return getCountry(code)?.callingCode ?: "+1"
    }

    val SUPPORTED_CURRENCIES: List<String> = listOf(
        "NGN", "USD", "GBP", "EUR", "AED", "CAD", "AUD", "ZAR", "GHS", "KES", "INR", "JPY", "CHF", "SGD", "SAR", "QAR", "BRL"
    )

    val MEASUREMENT_UNITS: List<String> = listOf(
        "sq ft", "m²", "Acres", "Hectares"
    )
}
