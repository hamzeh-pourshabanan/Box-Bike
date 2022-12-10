package com.example.location.domain

import android.os.Parcelable
import com.example.location.base.DomainModel
import kotlinx.android.parcel.Parcelize

@Parcelize
class TerminalsDomainModel(
    val destinations: List<Destination>,
    val expireDuration: Int,
    val origin: Origin?,
    val price: Double
): DomainModel, Parcelable

@Parcelize
data class Destination(
    val address: String,
    val lat: Double,
    val long: Double
): DomainModel, Parcelable

@Parcelize
data class Origin(
    val address: String,
    val lat: Double,
    val long: Double
):DomainModel, Parcelable