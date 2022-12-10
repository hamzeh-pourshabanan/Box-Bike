package com.example.box_biker

import android.content.Intent
import android.util.Log
import com.example.box_biker.data.Destination
import com.example.box_biker.data.Origin
import com.example.box_biker.data.Response
import com.example.location.domain.TerminalsDomainModel
import com.example.location.LocationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Terminals(val lat: Double, val long: Double, val price: Double)
class MyFireBaseMessagingService: FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param message Object representing the message received from Firebase
     */
    override fun onMessageReceived(message: RemoteMessage) {
        // parsing data back
//        val origin = Json.decodeFromString(Origin.serializer(), message.data["origin"] ?: "")
//        val dest = Json.decodeFromString(Origin.serializer(), message.data["destination"] ?: "")
        val res = message.data["destinations"]?.convertToDataClass<List<Destination>>()
        val origin = message.data["origin"]?.convertToDataClass<Origin>()
//        Json.decodeFromString<List<Response>>(Response.serializer(), message.data["destinations"].toString())
        Log.d(this.javaClass.simpleName, "From: ${message.from}")
        Log.d(this.javaClass.simpleName, "From data: ${message.data}")
        Log.d(this.javaClass.simpleName, "From price: " + message.data["price"])
        Log.d(this.javaClass.simpleName, "From dests: " + message.data["destinations"])
        Log.d(this.javaClass.simpleName, "From res: " + res)
        Log.d(this.javaClass.simpleName, "From origin: " + origin)
        Intent()
//        Log.d(this.javaClass.simpleName, "From coordinate origin: $origin")
//        Log.d(this.javaClass.simpleName, "From coordinate dest: $dest")

        // Check if message contains a data payload.
        val notification = message.notification
        if (message.messageId != null) {
            val intent = Intent(this, LocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("response", TerminalsDomainModel(
                destinations = res?.map { it.toDomainModel() } ?: emptyList(),
                expireDuration = message.data["expireDuration"]?.toInt() ?: 30,
                origin = origin?.toDomainModel(),
                price = message.data["price"]?.toDouble() ?: 0.0
            ) )
            startActivity(intent)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM token:", token)

    }

    internal inline fun <reified R : Any> String.convertToDataClass() =
        Json {
            ignoreUnknownKeys = true
        }.decodeFromString<R>(this)
}