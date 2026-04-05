package com.aarogyam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aarogyam.ui.navigation.AppNavGraph
import com.aarogyam.ui.theme.AarogyamThemeFromDataStore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AarogyamThemeFromDataStore {
                AppNavGraph()
            }
        }
    }
}
