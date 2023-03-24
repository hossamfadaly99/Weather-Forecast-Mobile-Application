package com.fadalyis.weatherforecastapplication.Home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fadalyis.weatherforecastapplication.databinding.ItemHourBinding
import com.fadalyis.weatherforecastapplication.model.pojo.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(var hoursList: List<Hourly>) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {
    private lateinit var binding: ItemHourBinding

    class ViewHolder(var binding: ItemHourBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = hoursList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.i("frekrnfjnekjtv", "onBindViewHolder: ${hoursList.size}")
        val currentHour = hoursList[position]
        val converter = -272.15 //0 //third transform
        holder.binding.hourlyTempTv.text = (currentHour.temp + converter).toInt().toString() + "°"

        holder.binding.hourlyTimeTv.text =
            if (currentHour == hoursList[0])
                "Now"
            else
                getDateHour(currentHour.dt)

        Glide.with(holder.binding.imageView.context)
            .load("https://openweathermap.org/img/wn/${currentHour.weather.get(0).icon}@2x.png")
            .into(holder.binding.imageView)
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

    private fun getDateHour(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("h a")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}