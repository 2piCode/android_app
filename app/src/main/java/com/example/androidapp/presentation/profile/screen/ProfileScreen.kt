package com.example.androidapp.presentation.profile.screen

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.androidapp.R
import com.example.androidapp.presentation.profile.utils.SystemBroadcastReceiver
import com.example.androidapp.presentation.profile.viewModel.ProfileViewModel
import com.example.androidapp.screens.Screen

import java.io.File

val DOWNLOAD_COMPLETE_ACTION = "android.intent.action.DOWNLOAD_COMPLETE"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel) {
    val context = LocalContext.current

    InitBroadcastReceiver(context)

    val state = viewModel.viewState

    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        TopAppBar(title = {
            Text(text = stringResource(R.string.profile))
        }, actions = {
            Icon(imageVector = Icons.Default.Edit,
                contentDescription = null,
                Modifier
                    .padding(end = 8.dp)
                    .clickable { navController.navigate(Screen.ProfileEdit.title) })
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = state.photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .clip(CircleShape)
                    .size(128.dp),
                error = painterResource(R.drawable.default_avatar)
            )
            Text(text = state.name, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = {
                enqueueDownloadRequest(
                    state.resumeUrl,
                    context
                )
            }) {
                Text(text = stringResource(R.string.resume))
            }
        }
    }
}


@Composable
private fun InitBroadcastReceiver(context: Context) {
    SystemBroadcastReceiver(systemAction = DOWNLOAD_COMPLETE_ACTION, onSystemEvent = { intent ->
        if (intent?.action == DOWNLOAD_COMPLETE_ACTION) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {
                navigateToDownloadedInvoice(context)
            }
        }
    })
}

private fun enqueueDownloadRequest(
    url: String, context: Context
) {
    val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
    with(request) {
        setTitle("Test pdf")
        setMimeType("application/pdf")
        setDescription("Downloading pdf...")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, "test.pdf"
        )
    }
    val manager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}

private fun navigateToDownloadedInvoice(context: Context) {
    try {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), "Test.pdf"
        )
        val uri = FileProvider.getUriForFile(
            context, context.applicationContext?.packageName + ".provider", file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        with(intent) {
            setDataAndType(
                uri, "application/pdf"
            )
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}