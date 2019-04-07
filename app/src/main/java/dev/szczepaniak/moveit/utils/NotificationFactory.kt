package dev.szczepaniak.moveit.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import dev.szczepaniak.moveit.MainActivity
import dev.szczepaniak.moveit.R
import java.util.*

const val CARD_ID = "CARD_NAME"

class NotificationFactory {

    fun show(
        context: Context, title: String?,  message: String?, cardId: String?=null,
        @StringRes channelResId: Int = R.string.notification_channel_default_id
    ) =
        create(context, title, message, channelResId, cardId)
            .let { context.notificationManager?.notify(Random().nextInt(), it) }

    fun create(
        context: Context, title: String?, message: String?,
        @StringRes channelResId: Int = R.string.notification_channel_default_id,
        cardId: String?
    ): Notification {
        val defaultTitle = context.getString(R.string.app_name)
        val channelId = context.getString(channelResId)

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: defaultTitle)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(createPendingIntent(context, cardId))
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannels(context: Context) {
        val channelId = context.getString(R.string.notification_channel_default_id)
        val channelName = context.getString(R.string.notification_channel_default_name)
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            .let { context.notificationManager?.createNotificationChannel(it) }
    }

    private val Context.notificationManager: NotificationManager?
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    private fun createPendingIntent(context: Context, cardId: String?) =
        Intent(context, MainActivity::class.java).putExtra(CARD_ID, cardId)
            .let { it.toPendingIntent(context) }

    private fun Intent.toPendingIntent(context: Context) =
        PendingIntent.getActivity(context, 0, this, PendingIntent.FLAG_ONE_SHOT)
}