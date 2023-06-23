package com.nishi.corebluetoothscanner

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BTScannerViewModelFactory(private val btAdapter: BluetoothAdapter) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BTScannerViewModel(btAdapter) as T
    }
}