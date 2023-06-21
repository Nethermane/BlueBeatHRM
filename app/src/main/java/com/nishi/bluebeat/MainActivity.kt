package com.nishi.bluebeat

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nishi.bluebeat.ui.theme.BlueBeatHRMTheme
import com.nishi.corebluetoothscanner.BTPermissionScreen
import com.nishi.corebluetoothscanner.R
import com.nishi.corebluetoothscanner.RequiredPermissions
import com.nishi.corebluetoothscanner.makeBTScannerScreen
import com.nishi.corebluetoothscanner.R as BTScannerR

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            BlueBeatHRMTheme {
                NavHost(
                    navController = navController,
                    startDestination = getString(BTScannerR.string.bt_permission_destination)
                ) {
                    composable(getString(BTScannerR.string.bt_permission_destination)) {
                        BTPermissionScreen(
                            navController
                        )
                    }
                    composable(getString(R.string.bt_sync_destination)) {
                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.BLUETOOTH_SCAN
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            navController.navigate(
                                getString(BTScannerR.string.bt_permission_destination)
                            )
                        } else {
                            makeBTScannerScreen(this@MainActivity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlueBeatHRMTheme {
        Greeting("Android")
    }
}