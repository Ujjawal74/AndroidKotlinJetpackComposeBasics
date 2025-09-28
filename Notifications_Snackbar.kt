package com.example.tasker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tasker.ui.theme.TaskerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val CHANNEL_ID = "tasker_channel"
        const val NOTIF_SIMPLE = 1001
        const val NOTIF_PROGRESS = 1002
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled in UI by checking permission again */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()

        // 🔹 Ask immediately on first launch if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            TaskerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotificationUI(
                        askPermission = { showSettingsSnack ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ActivityCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                                        // Denied once, can still show system dialog
                                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        // Denied twice / "Don't ask again"
                                        showSettingsSnack()
                                    }
                                    return@NotificationUI false
                                }
                            }
                            return@NotificationUI true
                        }
                    )
                }
            }
        }
    }

    /** Create Notification Channel */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tasker Notifications"
            val descriptionText = "Channel for Tasker reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun NotificationUI(
    askPermission: ((showSettingsSnack: () -> Unit) -> Boolean)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    /** Open this app's Settings screen */
    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }

    /** Simple notification */
    fun showSimpleNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.potato_color)

        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle("Task Reminder")
            .setContentText("This is your Tasker notification!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) notify(MainActivity.NOTIF_SIMPLE, builder.build())
        }
    }

    /** Progress notification */
    fun showProgressNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.potato_color)

        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle("Downloading File")
            .setContentText("Download in progress")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, false)

        scope.launch {
            for (i in 1..10) {
                builder.setProgress(100, i * 10, false)
                    .setContentText("Download ${i * 10}%")
                notificationManager.notify(MainActivity.NOTIF_PROGRESS, builder.build())
                delay(1000)
            }
            builder.setContentText("Download complete")
                .setProgress(0, 0, false)
            notificationManager.notify(MainActivity.NOTIF_PROGRESS, builder.build())
        }
    }

    /** Show Snackbar that links to Settings */
    fun showSettingsSnackbar() {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "Notifications are disabled. Enable in Settings.",
                actionLabel = "Settings",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                openAppSettings()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        if (askPermission { showSettingsSnackbar() }) showSimpleNotification()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show Notification")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (askPermission { showSettingsSnackbar() }) showProgressNotification()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show Progress Notification")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUI() {
    TaskerTheme {
        NotificationUI(askPermission = { _ -> true })
    }
}
