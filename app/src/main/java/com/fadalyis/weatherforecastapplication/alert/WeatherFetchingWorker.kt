package com.fadalyis.weatherforecastapplication.alert

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.fadalyis.weatherforecastapplication.MainActivity
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.model.pojo.Alert
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherService
import com.fadalyis.weatherforecastapplication.network.RetrofitHelper
import com.fadalyis.weatherforecastapplication.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherFetchingWorker(
    private var context: Context,
    private var workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {
    override suspend fun doWork(): Result {
        try {

            val inputData = inputData
            val alertType = inputData.getString("alertType")

            val apiInstance = RetrofitHelper.getInstance().create(CurrentWeatherService::class.java)

            val result =
                apiInstance.getCurrentWeather("33.0767125", "-113.0157329", "en", "standard")
            if (result.isSuccessful) {

                var firstWeatherCondition = ""
                val alertObj = result.body()?.alerts?.get(0)
                if (alertObj != null) {
                    var weatherConditionList = alertObj.description.split("...")
                    firstWeatherCondition =
                        if (weatherConditionList[0].isNotEmpty() && weatherConditionList[0].isNotBlank()) weatherConditionList[0] else weatherConditionList[1]
                } else {
                    firstWeatherCondition = "Today is fine don't worry"
                }

                if (alertType == "Alarm") {
                    createAlarm(context, firstWeatherCondition)
                } else {
                    createNotification(context, firstWeatherCondition)
                }
                return Result.success()
            } else
                return Result.failure(workDataOf(Constants.FAILURE_REASON to result.errorBody()))
        } catch (e: Exception) {
            return Result.failure(workDataOf(Constants.FAILURE_REASON to e.message))
        }

    }

    private suspend fun createAlarm(context: Context, contentText: String) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.calm_alarm)
        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val view: View = LayoutInflater.from(context).inflate(R.layout.alert_over_layout, null)
        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)
        val descriptionTV = view.findViewById<TextView>(R.id.description_tv)


        val layoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER

        var windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            descriptionTV.text = contentText
        }

        mediaPlayer.start()
        mediaPlayer.isLooping = true
        dismissBtn.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
    }

    private fun createNotification(context: Context?, contentText: String) {
        val i = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT)
        }

        val builder = NotificationCompat.Builder(context!!, "alertsChannel")
            .setSmallIcon(R.drawable.dummy_weather_icon)
            .setContentTitle("Weather Condition")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("TAG_ALARM", "startNotification: no permission")
                return
            }
            notify(1212, builder.build())
        }
    }


}