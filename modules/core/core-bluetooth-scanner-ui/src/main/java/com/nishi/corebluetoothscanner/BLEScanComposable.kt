package com.nishi.corebluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

@Composable
@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
fun makeBTScannerScreen(context: Context) {
    val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    //TODO: Fix
    val bluetoothAdapter = bluetoothManager.adapter!!
    return BTScannerScreen(bluetoothAdapter)
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
private fun BTScannerScreen(
    adapter: BluetoothAdapter
) {
    val scanViewModel = viewModel<BTScannerViewModel>(factory = BTScannerViewModelFactory(adapter))
    val devices by scanViewModel.leDevices.collectAsStateWithLifecycle()
    val refreshScope = rememberCoroutineScope()
    val refreshing by scanViewModel.scanning.collectAsStateWithLifecycle()

    val state = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch {
            scanViewModel.scanLeDevice()
        }
    })
    LaunchedEffect(Unit) {
        scanViewModel.scanLeDevice()
    }

    Box(Modifier.pullRefresh(state)) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!refreshing) {
                items(devices.size) {
                    for (device in devices) {
                        ListItem(
                            modifier = Modifier,
                            headlineText = { Text(text = "Item ${device.name}") },
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        devices.forEach {
            Text("Item ${it.name}", modifier = Modifier.padding(2.dp))
        }
    }
    if (devices.isEmpty()) {
        Text("No devices found, swipe to scan again")
    }

}