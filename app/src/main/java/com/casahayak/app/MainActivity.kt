package com.casahayak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.casahayak.app.ui.navigation.NavGraph
import com.casahayak.app.ui.theme.CaSahayakTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity — Navigation is handled entirely within Compose via NavGraph.
 * @AndroidEntryPoint enables Hilt injection for this Activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Full edge-to-edge display
        setContent {
            CaSahayakTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
