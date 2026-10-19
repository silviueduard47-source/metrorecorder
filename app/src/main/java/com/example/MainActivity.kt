package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.MetroMainScreen
import com.example.ui.theme.MetroWeatherTheme
import com.example.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val accent by viewModel.accentFlow.collectAsState()
            val isDark by viewModel.isDarkFlow.collectAsState()

            MetroWeatherTheme(
                accent = accent,
                isDark = isDark
            ) {
                MetroMainScreen(viewModel = viewModel)
            }
        }
    }
}
