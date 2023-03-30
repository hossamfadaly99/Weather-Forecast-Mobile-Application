package com.fadalyis.weatherforecastapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fadalyis.weatherforecastapplication.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("TAGTAGTAG", "onCreate: ${AppCompatDelegate.getApplicationLocales().toLanguageTags()}")
        val sharedPreferences = getSharedPreferences(Constants.SETTING_SHARED_PREF, Context.MODE_PRIVATE)
        val language = sharedPreferences.getString(Constants.LANGUAGE, Constants.ENGLISH)
        if (Locale.getDefault().language != language)
            setLanguage(language.toString())
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)

        bottomNavigationView.setupWithNavController(navController)

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
//        this.recreate()
    }

    private fun setLanguage(language: String) {
        val appLocale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)

    }

}