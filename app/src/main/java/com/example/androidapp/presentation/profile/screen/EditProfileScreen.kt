package com.example.androidapp.presentation.profile.screen

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.androidapp.R
import com.example.androidapp.presentation.profile.viewModel.EditProfileViewModel

import java.io.File
import java.time.LocalTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, viewModel: EditProfileViewModel) {
    val context = LocalContext.current

    val state = viewModel.viewState
    viewModel.updateContext(context)

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia: ActivityResultLauncher<PickVisualMediaRequest> =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.onImageSelected(uri)
        }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map: Map<String, Boolean> ->
            if (map.values.contains(false)) {
                val dialog = AlertDialog.Builder(context)
                    .setMessage("Ну, так не пойдет...")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        viewModel.onPermissionDenied()
                    }

                dialog.show()
            }
            viewModel.onPermissionClosed()
        }

    val mGetContent = rememberLauncherForActivityResult<Uri, Boolean>(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            viewModel.onImageSelected(imageUri)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.edit_profile))
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        Modifier
                            .padding(end = 8.dp)
                            .clickable { navController.popBackStack() }
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                navController.popBackStack()
                                viewModel.onDoneClicked()
                            }
                    )
                },
                modifier = Modifier.shadow(elevation = 1.dp)
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = state.photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(128.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onAvatarClicked() },
                error = painterResource(R.drawable.default_avatar)
            )
            TextField(
                value = state.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            TextField(
                value = state.url,
                onValueChange = { viewModel.onUrlChanged(it) },
                label = { Text(stringResource(R.string.link)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            TextField(
                value = state.timeString,
                onValueChange = { viewModel.onTimeChanged(it) },
                label = { Text("Время любимой пары") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                isError = state.timeError != null,
                trailingIcon = {
                    Icon(
                        painterResource(id = R.drawable.time),
                        null,
                        modifier = Modifier.clickable { viewModel.onTimeInputClicked() })
                }
            )
            state.timeError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (state.isNeedToShowTimePicker) {
                DialWithDialogExample(
                    onConfirm = { h, m -> viewModel.onTimeConfirmed(h, m) },
                    onDismiss = { viewModel.onTimeDialogDismiss() },
                    time = state.time
                )
            }
        }
    }

    if (state.isNeedToShowPermission) {
        LaunchedEffect(Unit) {
            val permissions = mutableListOf<String>()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    fun onCameraSelected() {
        val baseDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val pictureFile = File(baseDir, "picture_${Date().time}.jpg")
        imageUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            pictureFile
        )
        imageUri?.let { mGetContent.launch(it) }
    }

    if (state.isNeedToShowSelect) {
        Dialog(onDismissRequest = { viewModel.onSelectDismiss() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.choose_source),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCameraSelected()
                                viewModel.onSelectDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = stringResource(R.string.camera),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.camera),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                viewModel.onSelectDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image_search),
                            contentDescription = stringResource(R.string.photo),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.photo),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialWithDialogExample(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    time: LocalTime
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState.hour, timePickerState.minute) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Отмена")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}

