package com.fadalyis.weatherforecastapplication.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.ItemAlertBinding
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter(var alertList: List<AlertSchedule>, var onAlertClickListener: OnAlertClickListener, var context: Context) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: ItemAlertBinding
    private lateinit var fullFormat: SimpleDateFormat

    class ViewHolder(var binding: ItemAlertBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
//        fullFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm a")
        fullFormat = SimpleDateFormat("dd MMM - hh:mm a")
        return ViewHolder(binding)
    }

    override fun getItemCount() = alertList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlert = alertList[position]

        holder.binding.apply {

            alarmTypeTv.text = if(currentAlert.type == "Alarm") context.getString(R.string.alarm) else context.getString(R.string.notification)
            fromDateTv.text = getDateFromTimeStamp(currentAlert.startDate)
            toDateTv.text = getDateFromTimeStamp(currentAlert.endDate)

            deleteAlarmIcon.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.delete_alert))
                    .setMessage(context.getString(R.string.delete_alert_message))
                    .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->

                    }
                    .setPositiveButton(context.getString(R.string.delete)) { dialog, which ->
                        onAlertClickListener.deleteAlert(currentAlert.id)
                    }
                    .show()
            }
        }
    }

    private fun getDateFromTimeStamp(time: Long): String? {
        return try {
            val netDate = Date(time)
            fullFormat.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}