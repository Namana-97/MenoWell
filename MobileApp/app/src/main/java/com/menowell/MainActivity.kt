package com.menowell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import com.menowell.ui.navigation.MenoWellNavGraph
import com.menowell.ui.theme.MenoWellTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenoWellTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MenoWellNavGraph()
                }
            }
        }
    }
}
