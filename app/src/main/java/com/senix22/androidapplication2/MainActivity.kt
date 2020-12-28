package com.senix22.androidapplication2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val Channel_ID = "channel_id"
    private val notificationId = 101

    // Идентификатор канала
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificatoinChannel()

        simpleNotification.setOnClickListener {
            sendNotification()
        }
        actionNotification.setOnClickListener {
            sendNotificatioWithAction()
        }
        replyNotification.setOnClickListener {
            sendNotificationWithReply()
        }
        progressNotification.setOnClickListener {
            sendNotificationWithProgress()
        }
    }

    private fun createNotificatoinChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = " Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Channel_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, Channel_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Simple Title")
            .setContentText("Simple desrciption")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    private fun sendNotificatioWithAction() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, Channel_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Action Title")
            .setContentText("Tap me")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    private fun sendNotificationWithReply() {
        if (Build.VERSION.SDK_INT >= 24) {
            // Create an instance of remote input builder
            val remoteInput: RemoteInput = RemoteInput.Builder("KEY_TEXT_REPLY")
                .run {
                    setLabel("Write your message here")
                    build()
                }

            // Create an intent
            val intent = Intent(this, MyBroadcastReceiver::class.java)
            intent.action = "REPLY_ACTION"
            intent.putExtra("KEY_NOTIFICATION_ID", notificationId)
            intent.putExtra("KEY_CHANNEL_ID", Channel_ID)
            intent.putExtra("KEY_MESSAGE_ID", 2)

            // Create a pending intent for the reply button
            val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                101,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Create reply action and add the remote input
            val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "Reply",
                replyPendingIntent
            ).addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()


            // Build a notification and add the action
            val builder = NotificationCompat.Builder(this, Channel_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Hello! how are you?")
                .addAction(action)

            // Finally, issue the notification
            NotificationManagerCompat.from(this).apply {
                notify(notificationId, builder.build())
            }
        }
    }

    private fun sendNotificationWithProgress() {
        val max = 100
        val builder = NotificationCompat.Builder(this, Channel_ID).apply {
            setContentTitle("Picture Download")
            setContentText("Download in progress")
            setSmallIcon(R.drawable.notification)
            priority = NotificationCompat.PRIORITY_LOW
        }
        Thread {
            try {
                TimeUnit.SECONDS.sleep(3)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            var progress = 0
            while (progress < max) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progress += 10

                // show notification with current progress
                builder.setProgress(max, progress, false)
                    .setContentText("$progress of $max")
                NotificationManagerCompat.from(this).apply {
                    notify(notificationId, builder.build())
                }
            }

            // show notification without progressbar
            builder.setProgress(0, 10, false)
                .setContentText("Completed")
            NotificationManagerCompat.from(this).apply {
                notify(notificationId, builder.build())
            }
        }.start()
    }
}