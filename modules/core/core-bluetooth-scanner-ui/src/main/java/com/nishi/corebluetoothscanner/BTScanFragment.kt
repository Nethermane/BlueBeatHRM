package com.nishi.corebluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nishi.corebluetoothscanner.RequiredPermissions.REQUIRED_BT_PERMISSIONS
import kotlinx.coroutines.launch
import com.nishi.corebluetoothscanner.BuildConfig


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

    val shouldLinkoutToSettings by remember {
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
    val context = LocalContext.current
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
                            if (shouldLinkoutToSettings) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                // Fix this later
                                val uri = Uri.fromParts("package", "com.nishi.bluebeat", null)
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            } else {
                                cameraPermissionState.launchMultiplePermissionRequest()
                            }
                        },
                    ) {
                        Text(
                            text = if (shouldLinkoutToSettings) {
                                "Change permissions manually"
                            } else {
                                "Grant Bluetooth Permissions"
                            }
                        )
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
