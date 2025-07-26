package djabari.dev.workquotes.domain.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import djabari.dev.workquotes.MainActivity
import djabari.dev.workquotes.R
import djabari.dev.workquotes.data.model.Quote
import me.tatarka.inject.annotations.Inject

@Inject
class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "workquote_channel"
        const val GROUP_KEY_QUOTES = "workquotes_group"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "WorkQuotes Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for daily quotes"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showQuoteNotification(quote: Quote) {
        val notificationId = quote.id.toIntOrNull() ?: quote.hashCode()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigation_target", "quotes_history")
            putExtra("quote_id", quote.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quotes)
            .setContentTitle("Your Quote for Today")
            .setContentText("\"${quote.content}\" — ${quote.author}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("\"${quote.content}\" — ${quote.author}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY_QUOTES)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
        Log.d("WorkQuotes-DEBUG", "Notification shown: ${quote.content} by ${quote.author}")
    }
}