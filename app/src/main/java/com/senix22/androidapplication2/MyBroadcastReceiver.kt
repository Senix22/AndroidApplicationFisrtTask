package com.senix22.androidapplication2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.app.NotificationManagerCompat

class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if ("REPLY_ACTION".equals(action)){
                val message = replyMessage(this)
                val messageId = getIntExtra("KEY_MESSAGE_ID",0)
                Toast.makeText(context,"$messageId : $message",Toast.LENGTH_LONG).show()

            }

            context?.apply {
                val notificationId = getIntExtra("KEY_NOTIFICATION_ID",0)
                val channelId = getStringExtra("KEY_CHANNEL_ID")

                // Build a notification and add the action
                val builder = NotificationCompat.Builder(this, channelId!!)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Title")
                    .setContentText("message sent!")

                // Finally, issue the notification
                NotificationManagerCompat.from(this).apply {
                    notify(notificationId, builder.build())
                }
            }
        }
    }


    private fun replyMessage(intent: Intent): CharSequence? {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return remoteInput?.getCharSequence("KEY_TEXT_REPLY")
    }
}