package com.fadalyis.weatherforecastapplication.Home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fadalyis.weatherforecastapplication.databinding.ItemDayBinding
import com.fadalyis.weatherforecastapplication.model.pojo.Daily
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter(var daysList: List<Daily>) :
    RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    private lateinit var binding: ItemDayBinding

    class ViewHolder(var binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDayBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = daysList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("frekrnfjnekjtv", "onBindViewHolder: ${daysList.size}")
        val currentDay = daysList[position]
        val converter = -272.15 //0 //third transform
        holder.binding.apply {
            dailyTempTv.text = "${(currentDay.temp.min + converter).toInt()}°/${(currentDay.temp.max + converter).toInt()}°"
            dailyDayTv.text =
                if (currentDay == daysList[0]) "Today" else getDateDay(currentDay.dt)
            dailyMainTv.text = currentDay.weather[0].main
        }

        Glide.with(holder.binding.dailyIconImageview.context)
            .load("https://openweathermap.org/img/wn/${currentDay.weather.get(0).icon}@2x.png")
            .into(holder.binding.dailyIconImageview)
    }

    private fun getDateDay(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("E")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}