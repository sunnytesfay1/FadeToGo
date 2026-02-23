package com.fadetogo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadetogo.app.model.Booking
import com.fadetogo.app.model.Service
import com.fadetogo.app.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {

    // connects this ViewModel to the BookingRepository
    // all Firebase operations go through here
    private val repository = BookingRepository()

    // holds the list of bookings being displayed on screen
    // starts as an empty list until data is loaded from Firebase
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    // holds the list of services the barber offers
    // customer sees this list when choosing what haircut they want
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    // holds whether the requested time slot is available
    // null means we haven't checked yet, true means open, false means taken
    private val _isSlotAvailable = MutableStateFlow<Boolean?>(null)
    val isSlotAvailable: StateFlow<Boolean?> = _isSlotAvailable

    // tracks whether a Firebase operation is in progress
    // when true the UI shows a loading spinner
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // holds any error messages that need to be shown to the user
    // null means no error currently
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // tracks whether a booking was successfully created
    // the UI observes this to navigate to the confirmation screen
    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess

    // CREATE BOOKING - called when customer submits a booking request
    // first checks slot availability before creating to prevent double bookings
    fun createBooking(booking: Booking) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // check availability before creating the booking
            // this is our scheduling protection layer
            val slotAvailable = repository.isSlotAvailable(
                barberId = booking.barberId,
                requestedStartTime = booking.timestamp,
                totalDurationMinutes = booking.totalDuration
            )

            // if the slot is taken stop here and tell the customer
            if (!slotAvailable) {
                _errorMessage.value =
                    "That time slot is no longer available. Please choose another time."
                _isLoading.value = false
                return@launch
            }

            // slot is open so go ahead and create the booking in Firestore
            val result = repository.createBooking(booking)

            result.onSuccess {
                // booking created successfully, notify the UI
                _bookingSuccess.value = true
            }

            result.onFailure { exception ->
                // something went wrong, surface the error to the user
                _errorMessage.value = exception.message
                    ?: "Booking failed. Please try again."
            }

            _isLoading.value = false
        }
    }

    // UPDATE BOOKING STATUS - used by barber to accept, decline, or complete a booking
    // status options are: "accepted", "declined", "completed"
    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.updateBookingStatus(bookingId, status)

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not update booking status."
            }

            _isLoading.value = false
        }
    }

    // LOAD CUSTOMER BOOKINGS - fetches all bookings for a specific customer
    // used on the customer's booking history screen
    fun loadCustomerBookings(customerId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getCustomerBookings(customerId)

            result.onSuccess { bookingList ->
                // update the bookings flow so the UI refreshes automatically
                _bookings.value = bookingList
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not load bookings."
            }

            _isLoading.value = false
        }
    }

    // LOAD BARBER BOOKINGS - fetches bookings for the barber dashboard
    // optional status filter lets barber view pending, accepted etc separately
    fun loadBarberBookings(barberId: String, status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getBarberBookings(barberId, status)

            result.onSuccess { bookingList ->
                _bookings.value = bookingList
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not load bookings."
            }

            _isLoading.value = false
        }
    }

    // LOAD SERVICES - fetches the barber's service offerings
    // customer sees this list on the booking screen to pick their haircut
    fun loadServices(barberId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getServices(barberId)

            result.onSuccess { serviceList ->
                _services.value = serviceList
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not load services."
            }

            _isLoading.value = false
        }
    }

    // ADD SERVICE - barber uses this to add a new haircut service
    // automatically reloads the services list after adding
    fun addService(service: Service, barberId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.addService(service, barberId)

            result.onSuccess {
                // reload services so the new one appears on screen immediately
                loadServices(barberId)
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not add service."
            }

            _isLoading.value = false
        }
    }

    // clears the error message after it has been displayed to the user
    // prevents the same error from showing again on recomposition
    fun clearError() {
        _errorMessage.value = null
    }

    // clears the booking success flag after the UI has reacted to it
    // prevents the app from navigating to confirmation screen repeatedly
    fun clearBookingSuccess() {
        _bookingSuccess.value = false
    }
}