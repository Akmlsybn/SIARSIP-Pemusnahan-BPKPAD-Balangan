package com.bpkpad.siarsip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bpkpad.siarsip.ui.screens.auth.LoginScreen
import com.bpkpad.siarsip.ui.theme.SiARSIPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiARSIPTheme {
                LoginScreen()
            }
        }
    }
}