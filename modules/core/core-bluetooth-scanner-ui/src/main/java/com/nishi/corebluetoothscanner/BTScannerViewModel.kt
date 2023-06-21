package com.nishi.corebluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BTScannerViewModel(
    private val btAdapter: BluetoothAdapter
): ViewModel() {
    private val scanning = MutableStateFlow(false)
    private val btEnabled = MutableStateFlow(false)
    private val _requestBTPermission = MutableStateFlow<Boolean>(false)
    internal val requestBTPermission: StateFlow<Boolean> = _requestBTPermission

    private val btScanner = btAdapter.bluetoothLeScanner

    private val leDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            viewModelScope.launch {
                leDevices.emit(leDevices.value.plus(result.device))
            }
        }
    }




    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    internal suspend fun scanLeDevice() {
        if (!scanning.value) { // Stops scanning after a pre-defined scan period.
            viewModelScope.launch {
                delay(SCAN_PERIOD)
                scanning.value = false
                btScanner.stopScan(leScanCallback)
            }
            scanning.value = true
            btScanner.startScan(leScanCallback)
        } else {
            scanning.value = false
            btScanner.stopScan(leScanCallback)
        }
    }


    companion object {
        const val REQUEST_ENABLE_BT = 1
        // Stops scanning after 10 seconds.
        private const val SCAN_PERIOD: Long = 10000
    }

}