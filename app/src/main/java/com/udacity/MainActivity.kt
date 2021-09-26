package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        registerReceiver(secondReceiver, IntentFilter(Intent.ACTION_POWER_CONNECTED))

        radio_group.setOnCheckedChangeListener { group, checkedId ->
            URL = when(checkedId){
                R.id.glid_button-> GLIDE_URL
                R.id.udacity_button-> UDACITY_URL
                R.id.retrofit_button-> RETROFIT_URL
                else-> URL
            }
        }

        custom_button.setOnClickListener {
            if (URL != "nothing selected"){
                download()
                custom_button.uponViewClicked()
                custom_button.animateProgress()
            }else{
                Toast.makeText(this,"Please select an option!",Toast.LENGTH_LONG).show()
            }
        }

        createChannel(CHANNEL_ID,"Download Channel")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id==downloadID){
                custom_button.uponDownloadComplete()
                notificationManager = ContextCompat.getSystemService(context!!,NotificationManager::class.java) as NotificationManager
                notificationManager.cancelNotification()
                notificationManager.sendNotification(getString(R.string.notification_message_body),context)
            }
            val query = DownloadManager.Query().setFilterById(downloadID)
            val d = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val c = d.query(query)
            Log.i("MainActivityCursor",c.moveToFirst().toString())
            if (c.moveToFirst()){
                val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = c.getInt(columnIndex)
                if (status==DownloadManager.STATUS_SUCCESSFUL){
                    isFailed = false
                }
            }
        }
    }

    private val secondReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notificationManager = ContextCompat.getSystemService(context!!,NotificationManager::class.java) as NotificationManager
            val who = intent?.action
            if (who == Intent.ACTION_POWER_CONNECTED){
                context.startActivity(intent)
            }
            notificationManager.cancelNotification()
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        var URL = "nothing selected"


        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
        var isFailed : Boolean = true
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context){
        val contentIntent = Intent(applicationContext,DetailActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        action = NotificationCompat.Action.Builder(R.drawable.ic_assistant_black_24dp,"Details",pendingIntent).build()

        val image = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.download)

        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(image)
            .bigLargeIcon(null)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.download_complete))
            .setContentText(messageBody)
            .setStyle(bigPicStyle)
            .setLargeIcon(image)
            .addAction(action)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .build()

        notify(NOTIFICATION_ID,notification)
    }

    private fun createChannel(channelId: String, channelName: String){
        if (android.os.Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Download completed!"
            }
            notificationManager = this.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun NotificationManager.cancelNotification(){
        cancelAll()
    }
}


