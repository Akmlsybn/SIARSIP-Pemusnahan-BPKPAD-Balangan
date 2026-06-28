package com.bpkpad.siarsip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bpkpad.siarsip.core.database.AppDatabase
import com.bpkpad.siarsip.core.navigation.Screen
import com.bpkpad.siarsip.core.navigation.SiArsipNavGraph
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import com.bpkpad.siarsip.ui.theme.SiARSIPTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sementara: paksa Room membuat database agar terlihat di Database Inspector
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.userDao().countUsers()
        }

        val isRemembered = authRepository.isRememberMe() && authRepository.getLoggedInUser() != null
        val startDestination = if (isRemembered) Screen.Dashboard.route else Screen.Login.route

        setContent {
            SiARSIPTheme {
                SiArsipNavGraph(
                    startDestination = startDestination,
                    onLogout = {
                        authRepository.logout()
                    }
                )
            }
        }
    }
}