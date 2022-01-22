package com.example.saloon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeItem(val time: String, val maxTime: String? = null): Parcelable
@Parcelize
data class StyleItem(val name: String,val price: Float, val time: TimeItem,val info: String="",
                     val tags: MutableList<String> =mutableListOf(),val id: String = "",val bookingId:String=""): Parcelable
@Parcelize
data class AccountItem(val id: String,var name: String, var password: String? = null,var number: String="",
                       var open: String? = null,var close: String? = null,var addressItem: AddressItem? = null): Parcelable
@Parcelize
data class AddressItem(val city: String,val postcode: String,val country: String,val address: String): Parcelable
data class ReviewItem(val review: String,val rating: Int ,val date: String)
@Parcelize
data class CalendarItem(val start: String="", val end: String="",val name: String="" ,var span: Int=4,val calendarType: Int = 0,
                        var gone: Boolean = false,val date: String = "",val id: Int = -1, val styleId: Int = -1): Parcelable
data class CheckItem(val id: String,val style: String,var checked: Boolean = false)
@Parcelize
data class CategoryItem(val id: String,val category: String): Parcelable