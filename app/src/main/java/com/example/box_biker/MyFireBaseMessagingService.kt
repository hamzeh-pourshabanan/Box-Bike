package com.example.box_biker

import android.content.Intent
import android.util.Log
import com.example.box_biker.data.Destination
import com.example.box_biker.data.Origin
import com.example.location.domain.TerminalsDomainModel
import com.example.location.LocationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MyFireBaseMessagingService: FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param message Object representing the message received from Firebase
     */
    override fun onMessageReceived(message: RemoteMessage) {
        val destinations = message.data["destinations"]?.convertToDataClass<List<Destination>>()
        val origin = message.data["origin"]?.convertToDataClass<Origin>()

        // Check if message contains a data payload.
        if (message.messageId != null) {
            val intent = Intent(this, LocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("response", TerminalsDomainModel(
                destinations = destinations?.map { it.toDomainModel() } ?: emptyList(),
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