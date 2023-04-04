package com.fadalyis.weatherforecastapplication.favorite

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.FragmentFavoriteBinding
import com.fadalyis.weatherforecastapplication.db.*
import com.fadalyis.weatherforecastapplication.favorite.FavoriteFragmentDirections.ActionFavoriteFragmentToHomeFragment
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.fadalyis.weatherforecastapplication.network.FavApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "FavoriteFragmentTag"

class FavoriteFragment : Fragment(), OnAddressClickListener {

    lateinit var binding: FragmentFavoriteBinding
    lateinit var viewModel: FavoriteViewModel
    private lateinit var viewModelFactory: FavoriteViewModelFactory
    private lateinit var mFavoriteAdapter: FavoriteAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()


        setupFavoriteRecyclerView()

        observeLocationState()

        binding.floatingActionButton.setOnClickListener {
            //TODO if no network
            if (!isOnline(requireContext())){
//                val noInternetSnackbar = Snackbar.make(
//                    binding.coordinator,
//                    getString(R.string.no_internet_connection),
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
//                    .setAction(R.string.settings) {
//                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                        startActivity(intent)
//                        noInternetSnackbar.dismiss()
//                    }
            }
            Navigation.findNavController(view)
                .navigate(R.id.action_favoriteFragment_to_mapsFragment)
//            val a: ActionF = FavoriteFragmentDirections.actionFavoriteFragmentToMapsFragment()
        }

    }

    private fun setupFavoriteRecyclerView() {
        mFavoriteAdapter = FavoriteAdapter(listOf(), this, requireContext())
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoriteRecyclerView.apply {
            adapter = mFavoriteAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun observeLocationState() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.location.collectLatest { result ->
                when (result) {
                    is FavApiState.Success -> {
                        Log.i(TAG, "onViewCreated success: ${result.data.size}")
                        if (result.data.isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                binding.emptyTv.visibility = View.GONE
                            }
                            updateFavoriteList(result)
                        }
                        else{
                            withContext(Dispatchers.Main) {
                                binding.emptyTv.visibility = View.VISIBLE
                            }
                            updateFavoriteList(result)
                        }
                    }
                    is FavApiState.Loading -> {
                        Log.i(TAG, "onViewCreated loading: $result")
                    }
                    is FavApiState.Failure -> {
                        Log.i(TAG, "onViewCreated failure: ${result.msg}")
                    }
                }

            }
        }
    }

    private suspend fun updateFavoriteList(result: FavApiState.Success) {
        mFavoriteAdapter.favList = result.data
        withContext(Dispatchers.Main) {
            mFavoriteAdapter.notifyDataSetChanged()
        }
    }

    private fun initViewModel() {
        val weatherDao: WeatherDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getWeatherDao()
        }
        val favoriteDao: FavoriteDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getFavoriteDao()
        }
        val alertDao: AlertDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getAlertDao()
        }
        viewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                CurrentWeatherClient.getInstance(),
                ConcreteLocalSource(weatherDao, favoriteDao, alertDao)
            )
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(FavoriteViewModel::class.java)

    }

    override fun deleteAddress(address: FavAddress) {
        viewModel.deleteLocation(address)
    }

    override fun viewWeatherData(mapLatLon: String) {
        val action: ActionFavoriteFragmentToHomeFragment = FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment()
        action.mapLatLon = mapLatLon
        Navigation.findNavController(requireView()).navigate(action)
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
}