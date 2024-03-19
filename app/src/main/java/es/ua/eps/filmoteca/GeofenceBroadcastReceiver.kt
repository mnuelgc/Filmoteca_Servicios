package es.ua.eps.filmoteca

import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.e(TAG, "Error receiving geofence event: ${geofencingEvent.errorCode}")
                return
            }
        }

        val geofenceList = geofencingEvent?.triggeringGeofences
        if (geofenceList != null) {
            for (geofence in geofenceList) {
                Log.d(TAG, "Triggered geofence ID: ${geofence.requestId}")
            }
        }

        val transitionType = geofencingEvent?.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(TAG, "Entered geofence")
                Toast.makeText(context, "Has entrado en el geocercado", Toast.LENGTH_SHORT).show()

                notificationHelper.sendHighPriorityNotification(
                    "Cerca de lugar de filmación", "Has entrado en el lugar de una filmación",
                    MapActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.d(TAG, "Moving in geofence")
                Toast.makeText(context, "Andando por el set", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "Andando por el set", "Te estas desplazando por el lugar de la filmación",
                    MapActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "Exited geofence")
                Toast.makeText(context, "Estas abandonando el lugar de filmación", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "Saliendo de la zona", "Has abandonado el lugar de filmación",
                    MapActivity::class.java
                )
            }
            else -> {
                Log.e(TAG, "Unknown transition type: $transitionType")
                Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val TAG = "GeoBrodMessage"
        const val ACTION_GEOFENCE_EVENT = "es.ua.eps.filmoteca.ACCION_GEOFENCE_EVENT"
    }
}