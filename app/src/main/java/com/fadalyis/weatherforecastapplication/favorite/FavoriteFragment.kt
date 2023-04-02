package com.fadalyis.weatherforecastapplication.favorite

import android.os.Bundle
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
import com.fadalyis.weatherforecastapplication.db.ConcreteLocalSource
import com.fadalyis.weatherforecastapplication.favorite.FavoriteFragmentDirections.ActionFavoriteFragmentToHomeFragment
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.fadalyis.weatherforecastapplication.network.FavApiState
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

    override fun deleteAddress(address: FavAddress) {
        viewModel.deleteLocation(address)
    }

    override fun viewWeatherData(mapLatLon: String) {
        val action: ActionFavoriteFragmentToHomeFragment = FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment()
        action.mapLatLon = mapLatLon
        Navigation.findNavController(requireView()).navigate(action)
    }
}