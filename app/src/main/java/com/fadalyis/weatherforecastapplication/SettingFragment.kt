package com.fadalyis.weatherforecastapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.fadalyis.weatherforecastapplication.databinding.FragmentSettingBinding
import com.fadalyis.weatherforecastapplication.utils.Constants

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences(Constants.SETTING_SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        binding.apply {

            languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (englishRadioBtn.isEnabled)
                    editor.putString(Constants.LANGUAGE, Constants.ENGLISH)
                else
                    editor.putString(Constants.LANGUAGE, Constants.ARABIC)
            }
            locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (gpsRadioBtn.isEnabled)
                    editor.putString(Constants.LOCATION, Constants.GPS)
                else
                    editor.putString(Constants.LOCATION, Constants.MAP)
            }
            windRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (meterSecRadioBtn.isEnabled)
                    editor.putString(Constants.WIND, Constants.METER_SEC)
               else
                    editor.putString(Constants.WIND, Constants.MILE_HOUR)
            }
            tempRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (celsiusRadioBtn.isEnabled)
                    editor.putString(Constants.TEMPERATURE, Constants.CELSIUS)
                else if (kelvinRadioBtn.isEnabled)
                    editor.putString(Constants.TEMPERATURE, Constants.KELVIN)
                else
                editor.putString(Constants.TEMPERATURE, Constants.FAHRENHEIT)
            }

            notificationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (enableRadioBtn.isEnabled)
                    editor.putBoolean(Constants.NOTIFICATIONS, true)
                else
                    editor.putBoolean(Constants.NOTIFICATIONS, false)
            }


        }
    }
}