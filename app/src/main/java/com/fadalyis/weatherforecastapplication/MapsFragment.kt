package com.example.navigationcomponent

import android.app.Activity
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.db.ConcreteLocalSource
import com.fadalyis.weatherforecastapplication.favorite.FavoriteViewModel
import com.fadalyis.weatherforecastapplication.favorite.FavoriteViewModelFactory
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

const val TAG = "MapsFragment"
class MapsFragment : Fragment() {
    lateinit var imgBtn: ImageButton
    lateinit var confirmLocationBtn: Button
    lateinit var latLng: LatLng
    lateinit var geocoder: Geocoder
    lateinit var searchEdt: EditText
    lateinit var map: GoogleMap
    lateinit var city: String
    lateinit var markerOptions: MarkerOptions
    lateinit var viewModel: FavoriteViewModel
    private lateinit var viewModelFactory: FavoriteViewModelFactory
    lateinit var bottomNavigationView: BottomNavigationView
    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        latLng = LatLng(31.038692, 31.388311)
        markerOptions = MarkerOptions().position(latLng).title("Mansoura")
        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(cameraUpdate)
        //map.clear()

        googleMap.setOnMapClickListener {
            latLng = it
            googleMap.clear()
            Log.i("fejfbtrehj", ":map clicked ")
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            city = if (address != null)
                address[0].locality ?: address[0].getAddressLine(0).split(',')[0]
            else
                "current location"

            markerOptions = MarkerOptions().position(it).title(city)
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap.addMarker(markerOptions)
            googleMap.animateCamera(cameraUpdate)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        initViewModel()

        imgBtn = view.findViewById(R.id.back_img_btn)
        confirmLocationBtn = view.findViewById(R.id.confirm_location_btn)
        searchEdt = view.findViewById(R.id.search_editText)

        imgBtn.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_mapsFragment_to_favoriteFragment)
            bottomNavigationView.visibility = View.VISIBLE
        }
        confirmLocationBtn.setOnClickListener {

            latLng = markerOptions.position
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude , 1)
            if (address != null)
                city = address[0].locality ?: address[0].getAddressLine(0).split(',')[0]


            val f1 = FavAddress(latLng.latitude, latLng.longitude, city , 0)
            viewModel.saveLocation(f1)


            Navigation.findNavController(view).navigate(R.id.action_mapsFragment_to_favoriteFragment)
            bottomNavigationView.visibility = View.VISIBLE
        }

        searchEdt.setOnEditorActionListener { v, actionId, event ->

            val address = geocoder.getFromLocationName(searchEdt.text.toString(), 1)

            if (address != null) {
                val searchedLatLng = LatLng(address[0].latitude, address[0].longitude)
                latLng =LatLng( address[0].latitude, address[0].longitude)
                map.clear()
                val city = address[0].locality ?: address[0].getAddressLine(0).split(',')[0]
                markerOptions =
                    MarkerOptions().position(searchedLatLng).title(city)
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(searchedLatLng, 15f)
                map.addMarker(markerOptions)
                map.animateCamera(cameraUpdate)
//
            }
            val inputMethodManager: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            return@setOnEditorActionListener false
        }

    }

    private fun initViewModel() {
        viewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                CurrentWeatherClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(FavoriteViewModel::class.java)

    }


}