package com.fadalyis.weatherforecastapplication.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.ItemLocationFavoriteBinding
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FavoriteAdapter(var favList: List<FavAddress>, var onAddressClickListener: OnAddressClickListener, var context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private lateinit var binding: ItemLocationFavoriteBinding

    class ViewHolder(var binding: ItemLocationFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemLocationFavoriteBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = favList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFav = favList[position]

        holder.binding.apply {
            favCityTv.text = currentFav.city

            deleteFavIcon.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    //.setTitle(context.resources.getString(R.string.title))
                    .setTitle("Delete location")
                    .setMessage("Are you sure you want to delete this location from favorites?")
                    .setNeutralButton("Cancel") { dialog, which ->

                    }
                    .setPositiveButton("Delete") { dialog, which ->
                        onAddressClickListener.deleteAddress(currentFav)
                    }
                    .show()
            }
        }

    }
}