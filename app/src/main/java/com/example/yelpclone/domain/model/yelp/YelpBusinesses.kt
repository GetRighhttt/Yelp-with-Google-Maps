package com.example.yelpclone.domain.model.yelp

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "businesses")
data class YelpBusinesses(
    @PrimaryKey(autoGenerate = true)
    val roomID: Int? = null,
    val rating: Double,
    val phone: String,
    val id: String,
    val alias: String,
    @SerializedName("is_closed") val isClosed: Boolean,
    val categories: List<YelpCategories>,
    @SerializedName("review_count") val reviewCount: Int,
    val name: String,
    val url: String,
    val coordinates: YelpCoordinates,
    @SerializedName("image_url") val imageUrl: String,
    val location: YelpLocations,
    val distance: Double // meters
) : Parcelable {
    /*
    Need a function to convert distance in meters into a primitive type we can use for our UI.
    Convert meters to miles. We could have done this in our view model as well but we will
    demo it here.
     */
    fun displayDistance(): String {
        val milesPerMeter = 0.000621371
        val distanceInMiles = "%.2f".format(distance * milesPerMeter)
        return "$distanceInMiles miles"
    }
}

