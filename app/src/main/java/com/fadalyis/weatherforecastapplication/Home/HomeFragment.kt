package com.fadalyis.weatherforecastapplication.Home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fadalyis.weatherforecastapplication.databinding.FragmentHomeBinding
import com.fadalyis.weatherforecastapplication.db.ConcreteLocalSource
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.network.ApiState
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_ID = 5055
const val TAG = "HOME_FRAGMENT_TAG"

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    lateinit var viewModel: HomeViewModel
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var noInternetSnackbar: Snackbar
    var lati: Double = 0.0
    var longi: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        getLastLocation()

        binding.tv.setOnClickListener {
            refreshWeather()
        }
    }

    private fun makeNoNetworkConnectionSnackbar() {
        noInternetSnackbar = Snackbar.make(
            binding.layout,
            "No internet connection!",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("Setting") {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
                noInternetSnackbar.dismiss()
            }
    }

    private fun handleLocationPermission() {

        showNoLocationViews()

        binding.settingButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun showNoLocationViews() {
        binding.problemImageView.visibility = View.VISIBLE
        binding.problemTitleTv.visibility = View.VISIBLE
        binding.problemDescriptionTv.visibility = View.VISIBLE
        binding.settingButton.visibility = View.VISIBLE
    }

    private fun hideNoLocationViews() {
        binding.problemImageView.visibility = View.GONE
        binding.problemTitleTv.visibility = View.GONE
        binding.problemDescriptionTv.visibility = View.GONE
        binding.settingButton.visibility = View.GONE
    }

    private fun refreshWeather() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        hideNoLocationViews()
                        if (isOnline(requireContext())) {
                            viewModel.getOnlineWeather(
                                lati.toString(),
                                longi.toString(),
                                "d7b359e69914f81117abea49314510cf"
                            )
                        } else {
                            viewModel.getSavedWeather()
                        }
                    } else {
                        Log.i(TAG, "refreshWeather: c")
                        handleLocationPermission()
                    }
                } else {
                    Log.i(TAG, "refreshWeather: d")
                    viewModel.getSavedWeather()
                    viewModel.weather.collectLatest { result ->
                        when (result) {
                            is ApiState.Success -> {
                                hideNoLocationViews()
                                Log.i(TAG, "refreshWeather: sucessssss")
                                if (checkPermissions() && isLocationEnabled() && isOnline(requireContext())) {
                                    Log.i(TAG, "refreshWeather: ygeb data men network")
                                    requestNewLocationData()
//                                    refreshWeather()
                                    viewModel.getOnlineWeather(
                                        lati.toString(),
                                        longi.toString(),
                                        "d7b359e69914f81117abea49314510cf"
                                    )
                                    this.cancel()
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Log.i(TAG, "refreshWeather: main thread & should update ui")
                                        binding.tv.text =
                                            getDateTime(result.data.current.dt.toString())
                                    }
                                }
                            }
                            else -> {
                                Log.i(TAG, "refreshWeather: failed")
                                withContext(Dispatchers.Main) {
                                    handleLocationPermission()
                                }
                            }
                        }
                    }
                }

            }

            viewModel.weather.collectLatest { result ->
                Log.i(TAG, "onViewCreated: collected over network")
                when (result) {
                    is ApiState.Success -> {
                        Log.i(TAG, "refreshWeather: feee resultttt")
                        getWeather(result)
                    }
                    is ApiState.Loading -> {
                        if (isOnline(requireContext())) {
                            Log.i("iecrhje", "timezone: loading")
                        } else {
                            //TODO hide loading
                            Log.i("iecrhje", "timezone: else loading")
                            makeNoNetworkConnectionSnackbar()
                            if (isLocationEnabled())
                                noInternetSnackbar.show()
                        }
                    }
                    else -> {
                        Log.i("iecrhje", "timezone: error maybe no data")
                    }
                }
            }
        }
    }

    private suspend fun CoroutineScope.getWeather(result: ApiState.Success) {
        if (isOnline(requireContext())) {
            Log.i(
                "iecrhje",
                "timezone:${getDateTime(result.data.current.dt.toString())} ${result.data}"
            )
            withContext(Dispatchers.Main) {
                binding.tv.text = getDateTime(result.data.current.dt.toString())
            }
            viewModel.saveCurrentWeather(result.data)
        } else {
            viewModel.getSavedWeather()
            Log.i(
                TAG,
                "onViewCreated: saved res from db ${result.data.lat}, ${result.data.lon}, ${
                    getDateTime(
                        result.data.current.dt.toString()
                    )
                }, ${result.data.timezone} ----- ${result.data.current} "
            )
            withContext(Dispatchers.Main) {
                binding.tv.text = getDateTime(result.data.current.dt.toString())
            }
            hideNoLocationViews()
            this.cancel()
        }
    }

    private fun initViewModel() {
        viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                CurrentWeatherClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(HomeViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        //getLastLocation()

        refreshWeather()
        if (!isOnline(requireContext())) {
            lifecycleScope.launch {
                viewModel.weather.collectLatest { result ->
                    when (result) {
                        is ApiState.Success -> {
                            if (result.data.timezone.isEmpty()) {
                                Log.i("no network, there is data", "onResume: ")
                                refreshWeather()
                            } else {
                                Log.i("no network no data", "onResume: ")
                            }
                        }

                        else -> {
                            refreshWeather()
                            Log.i("else", "onResume: ")
                        }
                    }
                }
            }
        } else
            refreshWeather()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
     //           || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.i("kjnvrnt", "get last loc ")
        if (checkPermissions()) {
            Log.i("kjnvrnt", "checkper true ")
            if (isLocationEnabled()) {
                Log.i("kjnvrnt", "loc enabled true ")
                requestNewLocationData()
            } else {
                Log.i("kjnvrnt", "not loc enabled true ")
                handleLocationPermission()
            }
        } else {
            Log.i("kjnvrnt", "not checkper true ")
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.interval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLastLocation: Location = locationResult.lastLocation
            lati = mLastLocation.latitude
            longi = mLastLocation.longitude
            viewModel.getOnlineWeather(
                lati.toString(),
                longi.toString(),
                "d7b359e69914f81117abea49314510cf"
            )

            Log.i("kjnvrnt", "lat: $lati, long: $longi")
//            latitudeTV.text = mLastLocation.latitude.toString()
//            longitudeTV.text = mLastLocation.longitude.toString()
//            val address =
//                geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
//            addressTV.text = address?.get(0)?.getAddressLine(0).toString()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isOnline(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy - h:m:s")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}