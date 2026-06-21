package com.example.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("EXTRA_TITLE") ?: "Waktunya Sehat!"
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: "Jangan lupa penuhi target kalori dan air minum hari ini."
        val notificationId = intent?.getIntExtra("EXTRA_ID", 1) ?: 1

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val builder = NotificationCompat.Builder(context, "nutritrack_channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
        
        // Reschedule for next day
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val nextIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
            putExtra("EXTRA_ID", notificationId)
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            notificationId,
            nextIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val nextTime = System.currentTimeMillis() + android.app.AlarmManager.INTERVAL_DAY
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, nextTime, pendingIntent)
        } else {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, nextTime, pendingIntent)
        }
    }
}
