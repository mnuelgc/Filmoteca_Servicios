package es.ua.eps.filmoteca

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
//import androidx.work.OneTimeWorkRequest
//import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//import com.google.firebase.messaging.RemoteMessage
//import com.google.firebase.quickstart.fcm.R

class MyFirebaseMessagingService() : FirebaseMessagingService() {

    private lateinit var  context : Context
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (isLongRunningJob()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob(remoteMessage.data)
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage.data)
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body -> sendNotification(it.title, body) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private fun isLongRunningJob() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob(dataMap: MutableMap<String, String>) {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)

        val data = Data.Builder()

        data.putString("type", dataMap.get("type"))
        data.putString("title", dataMap.get("title"))
        data.putString("director", dataMap.get("director"))
        data.putString("year", dataMap.get("year"))
        data.putString("genre", dataMap.get("genre"))
        data.putString("format", dataMap.get("format"))
        data.putString("imdbUrl", dataMap.get("imdbUrl"))
        data.putString("comments", dataMap.get("comments"))
        data.putString("image", dataMap.get("image"))

        work.setInputData(data.build())

        WorkManager.getInstance(this).beginWith(work.build()).enqueue()


        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(data: MutableMap<String, String>) {
        manageDataToFilm(data)
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String?, messageBody: String) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        messageTitle ?: "Notificacion de la filmoteca"


        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun manageDataToFilm( data : Map<String, String>)
    {
        val title = data.get("title")
        val director = data.get("director")
        val year = data.get("year")
        var genre = data.get("genre")
        var format = data.get("format")
        var imdbUrl = data.get("imdbUrl")
        var comments = data.get("comments")

        //TODO
        //var imagesResId = R.mipmap.ic_launcher // Propiedades de la clase

        var film = Film(context)

        film.title = title
        film.director = director
        film.year = year!!.toInt()
        film.genre = genre!!.toInt()
        film.format = format!!.toInt()
        film.imdbUrl = imdbUrl
        film.comments = comments

        FilmDataSource.films.add(film)

        Log.d("Film", film.toString())

    }

    fun setContext (mContext : Context)
    {
        context = mContext
    }
    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}