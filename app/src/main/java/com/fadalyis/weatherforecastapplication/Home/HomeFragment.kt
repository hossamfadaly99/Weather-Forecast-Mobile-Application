package com.fadalyis.weatherforecastapplication.Home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.FragmentHomeBinding
import com.fadalyis.weatherforecastapplication.db.ConcreteLocalSource
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.network.ApiState
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.fadalyis.weatherforecastapplication.utils.Constants
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_ID = 5055
const val TAG = "HOME_FRAGMENT_TAG"

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var mHourlyAdapter: HourlyAdapter
    private lateinit var mHourlyLayoutManager: LinearLayoutManager
    private lateinit var mDailyAdapter: DailyAdapter
    private lateinit var mDailyLayoutManager: LinearLayoutManager
    private lateinit var noInternetSnackbar: Snackbar
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var address: Address
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var tempSymbol: String
    private lateinit var windSymbol: String
    lateinit var units: String
    lateinit var lang: String
    lateinit var temp: String
    lateinit var windSetting: String
    var windSpeedConverter: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SETTING_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        windSetting = sharedPreferences.getString(Constants.WIND, Constants.METER_SEC).toString()
        windSymbol = if (windSetting == Constants.METER_SEC) getString(R.string.meter_second) else getString(
                    R.string.mile_hour)
        lang = sharedPreferences.getString(Constants.LANGUAGE, Constants.ENGLISH).toString()
        Log.i("vkjtnvknrfgjk", "onCreateView: $lang")
        temp = sharedPreferences.getString(Constants.TEMPERATURE, Constants.CELSIUS).toString()
        when (temp) {
            Constants.KELVIN -> {
                units = Constants.STANDARD
                tempSymbol = "K"
                windSpeedConverter = if (windSetting == Constants.METER_SEC) 1.0 else 2.23693629
            }
            Constants.CELSIUS -> {
                units = Constants.METRIC
                tempSymbol = "°C"
                windSpeedConverter = if (windSetting == Constants.METER_SEC) 1.0 else 2.23693629
            }
            else -> {
                units = Constants.IMPERIAL
                tempSymbol = "°F"
                windSpeedConverter = if (windSetting == Constants.METER_SEC) 1/2.23693629 else 1.0
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initViewModel()
        binding.progressBar.visibility = View.VISIBLE
        getLastLocation()

        binding.swipeRefreshLayout.setOnRefreshListener {
            enhancedRefreshWeather()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        //setLanguage("en")
//        binding.lastDateTv.setOnClickListener {
//            enhancedRefreshWeather()
//        }
    }

    //TODO replace snackbar with another view
    private fun makeNoNetworkConnectionSnackbar() {
        noInternetSnackbar = Snackbar.make(
            binding.coordinator,
            "No internet connection!",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
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

        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun hideNoLocationViews() {
        binding.problemImageView.visibility = View.GONE
        binding.problemTitleTv.visibility = View.GONE
        binding.problemDescriptionTv.visibility = View.GONE
        binding.settingButton.visibility = View.GONE

        binding.progressBar.visibility = View.VISIBLE
    }

    private fun enhancedRefreshWeather() {
        Log.i(TAG, "enhancedRefreshWeather: ")
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        hideNoLocationViews()
                        if (isOnline(requireContext())) {
                            //network and location
                            //update the database from the network
                            Log.i(TAG, "enhancedRefreshWeather: network and location")
                            Log.i(
                                "kvrntvjrjh",
                                "latttttttt, longgggggggggg first: $latitude, $longitude"
                            )
//                            viewModel.getOnlineWeather(
//                                latitude.toString(),
//                                longitude.toString(),
//                                "en"
//                            )
                            requestNewLocationData()
                            Log.i(
                                "kvrntvjrjh",
                                "latttttttt, longgggggggggg first: $latitude, $longitude"
                            )
                        } else {
                            //location, No network
                            //TODO observe data -> if != null display else show tv and btn to open the network
                            makeNoNetworkConnectionSnackbar()
                            if (isLocationEnabled())
                                noInternetSnackbar.show() else {
                            }
                            //viewModel.getSavedWeather()
                        }
                    } else {
                        //no location
                        Log.i(TAG, "refreshWeather: c")
                        //TODO observe data -> if != null display else show tv and btn to open the location
                        handleLocationPermission()
                    }
                } else {
                }
            }
            viewModel.weather.collectLatest { result ->
                Log.i(TAG, "onViewCreated: collectLatest")
                when (result) {
                    is ApiState.Success -> {
                        Log.i(TAG, "refreshWeather: feee resultttt")
                        getWeather(result)
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                    is ApiState.Loading -> {
                        if (isOnline(requireContext())) {
                            Log.i("iecrhje", "timezone: loading")
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            //TODO hide loading
                            Log.i("iecrhje", "timezone: else loading")
                            makeNoNetworkConnectionSnackbar()
                            if (isLocationEnabled()) {
                                noInternetSnackbar.show()
                                binding.progressBar.visibility = View.INVISIBLE
                            }
                        }
                    }
                    else -> {
                        Log.i("iecrhje", "timezone: error maybe no data $result")
                        if (!(checkPermissions() && isLocationEnabled())) {
                            withContext(Dispatchers.Main) {
                                handleLocationPermission()
                                binding.progressBar.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun getWeather(result: ApiState.Success) {
        Log.i(
            "iecrhje",
            "timezone:${getDateTime(result.data.current.dt.toString())} ${result.data}"
        )

        Log.i(
            "iecrhje",
            "lat & lon:${result.data.lat} ${result.data.timezone} ${result.data.daily} ${result.data.lon}"
        )

        val current = result.data.current
        mHourlyAdapter = HourlyAdapter(result.data.hourly.take(24), tempSymbol, requireContext())
        mHourlyLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mDailyAdapter = DailyAdapter(result.data.daily, tempSymbol, requireContext())
        mDailyLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        withContext(Dispatchers.IO) {
            try {
                val gcd = geocoder.getFromLocation(
                    result.data.lat.toDouble(),
                    result.data.lon.toDouble(),
                    1
                )
                Log.i(TAG, "getWeather gcd: $gcd")
                address = gcd?.get(0)!!
                //Log.i(TAG, "getWeather address: $address")
            } catch (e: Exception) {
//                e.printStackTrace()
            }
        }

        withContext(Dispatchers.Main) {

            binding.apply {

                lastDateTv.text = getDateTime(current.dt.toString())
                cityTv.text = address.locality ?: address.getAddressLine(0).split(',')[0]
                tempTv.text = (current.temp).toInt().toString() + tempSymbol
                weatherDescriptionTv.text = current.weather[0].description
                Glide.with(requireContext())
                    .load("https://openweathermap.org/img/wn/${current.weather.get(0).icon}@4x.png")
                    .into(weatherIconHome)
                tempHighTv.text =
                    (result.data.daily.get(0).temp.max).toInt().toString() + tempSymbol
                tempLowTv.text =
                    (result.data.daily.get(0).temp.min).toInt().toString() + tempSymbol
                hourlyRecyclerview.apply {
                    adapter = mHourlyAdapter
                    layoutManager = mHourlyLayoutManager
                }
                dailyRecyclerview.apply {
                    adapter = mDailyAdapter
                    layoutManager = mDailyLayoutManager
                }
                cloudPercentageTv.text = "${current.clouds}%"
                humidityPercentageTv.text = "${current.humidity}%"

                windSpeedTv.text = "${convertWindSpeed(current.wind_speed)} $windSymbol"

                pressurecloudPercentageTv.text = "${current.pressure} hpa"
                layout.background =
                    if (current.dt > current.sunset || current.dt < current.sunrise)
                        ContextCompat.getDrawable(requireContext(), R.drawable.sky_night)
                    else
                        ContextCompat.getDrawable(requireContext(), R.drawable.summy_sky_cloud)
            }
        }
        withContext(Dispatchers.Main) {
            hideNoLocationViews()
        }
    }

    private fun convertWindSpeed(windSpeed: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(windSpeed * windSpeedConverter)
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
        enhancedRefreshWeather()
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
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
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
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude



            if (isOnline(requireContext())) {
                Log.i("kvrntvjrjh", "latttttttt, longgggggggggg second: $latitude, $longitude")
                Log.i("kvrntvjrjh", "latttttttt, longgggggggggg second: $units")
                viewModel.getOnlineWeather(
                    latitude.toString(),
                    longitude.toString(),
                    lang,
                    units
                )
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                Snackbar.make(binding.layout, "no internet", Snackbar.ANIMATION_MODE_SLIDE).show()
            }
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

    private fun setLanguage(language: String) {
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        Locale.setDefault(Locale(language))
        configuration.setLayoutDirection(Locale(language))
        // update configuration
        resources.updateConfiguration(configuration, metric)
        // notify configuration
        onConfigurationChanged(configuration)
        requireActivity().recreate()
    }

}