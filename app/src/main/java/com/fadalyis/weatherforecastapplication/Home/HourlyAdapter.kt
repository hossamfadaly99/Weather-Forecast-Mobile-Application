package com.fadalyis.weatherforecastapplication.Home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.ItemHourBinding
import com.fadalyis.weatherforecastapplication.model.pojo.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(var hoursList: List<Hourly>, var tempSymbol: String, var context: Context) :
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

        holder.binding.hourlyTempTv.text = (currentHour.temp ).toInt().toString() + tempSymbol

        holder.binding.hourlyTimeTv.text =
            if (currentHour == hoursList[0])
                context.getString(R.string.now)
            else
                getDateHour(currentHour.dt)

        Glide.with(holder.binding.imageView.context)
            .load("https://openweathermap.org/img/wn/${currentHour.weather.get(0).icon}@2x.png")
            .into(holder.binding.imageView)
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