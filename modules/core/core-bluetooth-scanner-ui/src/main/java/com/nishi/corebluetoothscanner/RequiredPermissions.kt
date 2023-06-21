package com.nishi.corebluetoothscanner

object RequiredPermissions {
    val REQUIRED_BT_PERMISSIONS = listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT
    )
}