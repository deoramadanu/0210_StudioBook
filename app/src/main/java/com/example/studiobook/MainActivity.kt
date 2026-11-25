package com.example.studiobook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studiobook.ui.screen.StudioSimulationScreen
import com.example.studiobook.ui.theme.StudioBookTheme
import com.example.studiobook.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudioBookTheme {
                val studioViewModel: StudioViewModel = viewModel()
                StudioSimulationScreen(viewModel = studioViewModel)
            }
        }
    }
}