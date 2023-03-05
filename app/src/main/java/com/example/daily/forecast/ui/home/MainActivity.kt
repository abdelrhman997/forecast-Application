package com.example.daily.forecast.ui.home

import android.app.Activity
import android.content.Intent

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.daily.forecast.databinding.ActivityMainBinding
import com.example.daily.forecast.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getBundleExtra("data")?.run {
                    mainViewModel.getData(getString("name") ?: "")
                    mainViewModel.cacheData()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.mvm = mainViewModel

        mainViewModel.searchClick.observe(
            this
        ) { resultLauncher.launch(Intent(this, SearchActivity::class.java)) }

    }


}