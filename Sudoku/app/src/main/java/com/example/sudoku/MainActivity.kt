package com.example.sudoku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Set up code. Also currently resets game for testing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        //resetGame()
    }

    private fun resetGame() {
        val sharedPref =
            getDefaultSharedPreferences(this)          //TODO This currently clears save data
        if (sharedPref != null) {                                       //when app is opened. Need to remove. Just
            with(sharedPref.edit()) {                                   //for testing for now
                putString("SAVED_NUMBERS", null)
                putLong("TIMER_STOPPED", 0L)
                putInt("DIFFICULTY", 0)
                commit()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}