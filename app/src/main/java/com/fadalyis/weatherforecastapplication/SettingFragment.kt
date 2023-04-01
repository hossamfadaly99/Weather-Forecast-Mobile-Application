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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.fadalyis.weatherforecastapplication.databinding.FragmentSettingBinding
import com.fadalyis.weatherforecastapplication.utils.Constants
import java.util.*

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

            loadData(sharedPreferences)
            checkChange(editor)

            editor.apply()

        }
    }

    private fun FragmentSettingBinding.loadData(
        sharedPreferences: SharedPreferences
    ) {
        val language = sharedPreferences.getString(Constants.LANGUAGE, Constants.ENGLISH)
        val location = sharedPreferences.getString(Constants.LOCATION, Constants.GPS)
        val windSpeed = sharedPreferences.getString(Constants.WIND, Constants.METER_SEC)
        val temp = sharedPreferences.getString(Constants.TEMPERATURE, Constants.CELSIUS)
        val notification = sharedPreferences.getBoolean(Constants.NOTIFICATIONS, true)

        if (language == Constants.ENGLISH) englishRadioBtn.isChecked = true
        else arabicRadioBtn.isChecked = true

        if (location == Constants.GPS) gpsRadioBtn.isChecked = true
        else mapsRadioBtn.isChecked = true

        if (windSpeed == Constants.METER_SEC) meterSecRadioBtn.isChecked = true
        else mileHourRadioBtn.isChecked = true

        if (temp == Constants.CELSIUS) celsiusRadioBtn.isChecked = true
        else if (temp == Constants.KELVIN) kelvinRadioBtn.isChecked = true
        else fahrenheitRadioBtn.isChecked = true

        if (notification) enableRadioBtn.isChecked = true
        else disableRadioBtn.isChecked = true
    }

    private fun FragmentSettingBinding.checkChange(
        editor: SharedPreferences.Editor
    ) {
        languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (englishRadioBtn.isChecked){
                editor.putString(Constants.LANGUAGE, Constants.ENGLISH)
                Log.i("ayhaga", "checkChange: english")
                setLanguage(Constants.ENGLISH)
            }
            else{
                editor.putString(Constants.LANGUAGE, Constants.ARABIC)
                Log.i("ayhaga", "checkChange: arabic")
                setLanguage(Constants.ARABIC)
            }
            editor.apply()
        }
        locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (gpsRadioBtn.isChecked) {
                editor.putString(Constants.LOCATION, Constants.GPS)
                editor.apply()
            }
            else {
                editor.putString(Constants.LOCATION, Constants.MAP)
                editor.apply()
            }
        }
        windRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (meterSecRadioBtn.isChecked) {
                editor.putString(Constants.WIND, Constants.METER_SEC)
                editor.apply()
            }
            else {
                editor.putString(Constants.WIND, Constants.MILE_HOUR)
                editor.apply()
            }
        }
        tempRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (celsiusRadioBtn.isChecked) {
                editor.putString(Constants.TEMPERATURE, Constants.CELSIUS)
            }
            else if (kelvinRadioBtn.isChecked)
                editor.putString(Constants.TEMPERATURE, Constants.KELVIN)
            else
                editor.putString(Constants.TEMPERATURE, Constants.FAHRENHEIT)
            editor.apply()
        }

        notificationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (enableRadioBtn.isChecked)
                editor.putBoolean(Constants.NOTIFICATIONS, true)
            else
                editor.putBoolean(Constants.NOTIFICATIONS, false)
            editor.apply()
        }
    }

    private fun setLanguageOld(language: String) {
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

    private fun setLanguage(language: String) {
        val appLocale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}