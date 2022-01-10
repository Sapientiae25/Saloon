package com.example.saloon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeItem(val time: String, val maxTime: String? = null): Parcelable
@Parcelize
data class StyleItem(val name: String,val price: Float, val time: TimeItem,val info: String,val tags: MutableList<String>,val id: String = ""): Parcelable
@Parcelize
data class AccountItem(val id: String,var name: String, var password: String? = null,var number: String="",
                       var open: String? = null,var close: String? = null,var addressItem: AddressItem? = null): Parcelable
@Parcelize
data class AddressItem(val city: String,val postcode: String,val country: String,val address: String): Parcelable
data class TimeObject(var hour: Int,var minute: Int,val id: String="",val bookingItem: BookingItem? = null)
data class ReviewItem(val review: String,val rating: Int ,val date: String)
data class CalendarItem(val start: String="", val end: String="")
data class BookingItem(val id: String="",val name: String="" ,val start: String="", val end: String="",val span: Int)