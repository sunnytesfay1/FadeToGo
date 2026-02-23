package com.fadetogo.app.utils

object PricingUtils {

    // calculates the travel surcharge based on distance in miles
    // now accepts dynamic values from the barber's settings
    // returns 0.0 if within base radius
    // returns null if beyond max radius meaning barber should decline
    fun calculateTravelSurcharge(
        distanceMiles: Double,
        baseRadiusMiles: Double,
        maxRadiusMiles: Double,
        costPerMile: Double
    ): Double? {
        return when {
            distanceMiles <= baseRadiusMiles -> 0.0
            distanceMiles > maxRadiusMiles -> null
            else -> {
                val extraMiles = distanceMiles - baseRadiusMiles
                Math.round(extraMiles * costPerMile * 100.0) / 100.0
            }
        }
    }

    // calculates total price using barber's dynamic settings
    fun calculateTotalPrice(
        basePrice: Double,
        distanceMiles: Double,
        baseRadiusMiles: Double,
        maxRadiusMiles: Double,
        costPerMile: Double
    ): Double? {
        val surcharge = calculateTravelSurcharge(
            distanceMiles,
            baseRadiusMiles,
            maxRadiusMiles,
            costPerMile
        ) ?: return null
        return Math.round((basePrice + surcharge) * 100.0) / 100.0
    }

    // calculates total booking duration in minutes
    // travel time + service duration + buffer
    fun calculateTotalDuration(
        travelTimeMinutes: Int,
        serviceDurationMinutes: Int,
        bufferMinutes: Int = 10
    ): Int {
        return travelTimeMinutes + serviceDurationMinutes + bufferMinutes
    }

    // converts meters to miles
    // Google Distance Matrix API returns distance in meters
    fun metersToMiles(meters: Double): Double {
        return Math.round((meters / 1609.344) * 100.0) / 100.0
    }

    // returns a human readable price string
    fun formatPrice(price: Double): String {
        return "$%.2f".format(price)
    }

    // returns a human readable distance string
    fun formatDistance(miles: Double): String {
        return "%.1f mi".format(miles)
    }

    // returns a human readable duration string
    fun formatDuration(minutes: Int): String {
        return if (minutes < 60) {
            "${minutes}min"
        } else {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins == 0) "${hours}hr" else "${hours}hr ${mins}min"
        }
    }

    // checks if a distance is within the barber's service area
    fun isWithinServiceArea(
        distanceMiles: Double,
        maxRadiusMiles: Double
    ): Boolean {
        return distanceMiles <= maxRadiusMiles
    }

    // returns a descriptive pricing message for the customer
    fun getPricingMessage(
        distanceMiles: Double,
        basePrice: Double,
        baseRadiusMiles: Double,
        maxRadiusMiles: Double,
        costPerMile: Double
    ): String {
        return when {
            distanceMiles <= baseRadiusMiles ->
                "Within free travel zone — no extra charge!"
            distanceMiles > maxRadiusMiles ->
                "Outside service area — barber may decline this booking."
            else -> {
                val surcharge = calculateTravelSurcharge(
                    distanceMiles,
                    baseRadiusMiles,
                    maxRadiusMiles,
                    costPerMile
                ) ?: 0.0
                "Travel surcharge of ${formatPrice(surcharge)} applies for your location."
            }
        }
    }
}