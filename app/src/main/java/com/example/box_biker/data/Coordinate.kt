package com.example.box_biker.data

import com.example.location.base.DataModel
import com.example.location.domain.TerminalsDomainModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("destinations")
    val destinations: List<Destination>,
    @SerialName("expireDuration")
    val expireDuration: Int,
    @SerialName("origin")
    val origin: Origin,
    @SerialName("price")
    val price: Double
): DataModel {
    override fun toDomainModel(): TerminalsDomainModel {
        return TerminalsDomainModel(destinations.map { it.toDomainModel() }, expireDuration, origin.toDomainModel(), price)
    }
}

@Serializable
data class Destination(
    @SerialName("address")
    val address: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("long")
    val long: Double
): DataModel {
    override fun toDomainModel(): com.example.location.domain.Destination {
        return com.example.location.domain.Destination(address, lat, long)
    }
}

@Serializable
data class Origin(
    @SerialName("address")
    val address: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("long")
    val long: Double
): DataModel {
    override fun toDomainModel(): com.example.location.domain.Origin {
        return com.example.location.domain.Origin(address, lat, long)
    }
}