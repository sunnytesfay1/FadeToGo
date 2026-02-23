package com.fadetogo.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.fadetogo.app.model.Booking
import com.fadetogo.app.model.Service
import kotlinx.coroutines.tasks.await

class BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // CREATE BOOKING - saves a new booking request to Firestore
    // status starts as "pending" until barber accepts or declines
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val docRef = firestore.collection("bookings")
                .document()

            val bookingWithId = booking.copy(
                bookingId = docRef.id,
                status = "pending",
                timestamp = System.currentTimeMillis()
            )

            docRef.set(bookingWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE BOOKING STATUS - barber uses this to accept or decline
    // also used to mark a job as complete
    suspend fun updateBookingStatus(
        bookingId: String,
        status: String
    ): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET BOOKINGS FOR CUSTOMER - fetches all bookings by customer id
    suspend fun getCustomerBookings(customerId: String): Result<List<Booking>> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("customerId", customerId)
                .get()
                .await()

            val bookings = snapshot.documents.mapNotNull {
                it.toObject(Booking::class.java)
            }

            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET BOOKINGS FOR BARBER - fetches all bookings for the barber
    // filters by status so barber can see pending vs accepted etc
    suspend fun getBarberBookings(
        barberId: String,
        status: String? = null
    ): Result<List<Booking>> {
        return try {
            var query = firestore.collection("bookings")
                .whereEqualTo("barberId", barberId)

            // if a status filter is passed in, apply it
            if (status != null) {
                query = query.whereEqualTo("status", status)
            }

            val snapshot = query.get().await()

            val bookings = snapshot.documents.mapNotNull {
                it.toObject(Booking::class.java)
            }

            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CHECK SLOT AVAILABILITY - this is our scheduling logic
    // it checks if a requested time overlaps with any existing accepted booking
    suspend fun isSlotAvailable(
        barberId: String,
        requestedStartTime: Long,
        totalDurationMinutes: Int
    ): Boolean {
        return try {
            // fetch all accepted bookings for this barber
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("barberId", barberId)
                .whereEqualTo("status", "accepted")
                .get()
                .await()

            val existingBookings = snapshot.documents.mapNotNull {
                it.toObject(Booking::class.java)
            }

            // convert requested time to milliseconds for comparison
            val requestedEndTime = requestedStartTime +
                    (totalDurationMinutes * 60 * 1000)

            // check if requested slot overlaps with any existing booking
            val hasOverlap = existingBookings.any { existing ->
                val existingStart = existing.timestamp
                val existingEnd = existingStart +
                        (existing.totalDuration * 60 * 1000)

                // overlap exists if the new booking starts before
                // an existing one ends and ends after it starts
                requestedStartTime < existingEnd &&
                        requestedEndTime > existingStart
            }

            !hasOverlap
        } catch (e: Exception) {
            false
        }
    }

    // GET SERVICES - fetches the barber's list of offered services
    suspend fun getServices(barberId: String): Result<List<Service>> {
        return try {
            val snapshot = firestore.collection("services")
                .whereEqualTo("barberId", barberId)
                .get()
                .await()

            val services = snapshot.documents.mapNotNull {
                it.toObject(Service::class.java)
            }

            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ADD SERVICE - barber uses this to add a new service offering
    suspend fun addService(service: Service, barberId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("services").document()
            val serviceWithId = service.copy(serviceId = docRef.id)
            docRef.set(serviceWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}