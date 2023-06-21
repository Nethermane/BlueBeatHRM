package com.nishi.corebluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.nishi.corebluetoothscanner.RequiredPermissions.REQUIRED_BT_PERMISSIONS
import kotlinx.coroutines.launch

@Composable
@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
fun makeBTScannerScreen(context: Context) {
    val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    //TODO: Fix
    val bluetoothAdapter = bluetoothManager.adapter!!
    return BTScannerScreen(bluetoothAdapter)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
private fun BTScannerScreen(
    adapter: BluetoothAdapter
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = BTScannerViewModel(adapter)
    val shouldRequestBluetooth = viewModel.requestBTPermission.collectAsStateWithLifecycle()
    Button(onClick = {
        coroutineScope.launch {
            viewModel.scanLeDevice()
        }
    }) {
        Text(text = "Sync new bluetooth device")
    }
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun BTPermissionScreen(navController: NavController) {
    var permissionAlreadyRequested by rememberSaveable {
        mutableStateOf(false)
    }

    val cameraPermissionState = rememberMultiplePermissionsState(REQUIRED_BT_PERMISSIONS) {
        permissionAlreadyRequested = true
    }
    if (cameraPermissionState.allPermissionsGranted) {
        navController.navigate(stringResource(R.string.bt_sync_destination))
        return
    }

    val shouldEnabledPermissionButton by remember {
        derivedStateOf {
            !permissionAlreadyRequested || cameraPermissionState.shouldShowRationale
        }
    }
    val bannerText: String? = if (cameraPermissionState.shouldShowRationale) {
        "The entire point of this app is to connect to bluetooth heart rate monitors." +
                " Please enable this feature when prompted"
    } else if (permissionAlreadyRequested) {
        "Please manually enable bluetooth permissions in Settings"
    } else {
        null
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            cameraPermissionState.launchMultiplePermissionRequest()
                        },
                        enabled = shouldEnabledPermissionButton,
                        ) {
                        Text(text = "Grant Bluetooth Permissions")
                    }
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    AnimatedBanner(bannerText)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun AnimatedBanner(text: String?) {
    AnimatedVisibility(
        visible = text != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider()
            Text(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
                text = text ?: ""
            )

        }
    }
}
