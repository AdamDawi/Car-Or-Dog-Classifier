package com.example.carordogclassifier.presentation.camera_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import com.example.carordogclassifier.model.PredictionModel
import java.util.concurrent.Executors


@Composable
fun CameraScreen() {

    CameraContent()
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraContent() {

    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }
    remember { Executors.newSingleThreadExecutor() }
    var text = remember { mutableStateOf("") }
    var percentage = remember { mutableStateOf("") }

    val modelPredictor = remember { PredictionModel(context) }
    val isImageLoading = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .padding(bottom = 250.dp),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            }
        )
        val padding = WindowInsetsCompat.Type.systemBars().dp
        Column(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color.Black)
                .align(Alignment.BottomCenter)
                .padding(bottom = padding)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isImageLoading.value) {
                CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color.White
                )
            } else {
                Box(modifier = Modifier
                    .border(7.dp, androidx.compose.ui.graphics.Color.White, CircleShape)
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !isImageLoading.value) {
                        isImageLoading.value = true
                        cameraController.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    super.onCaptureSuccess(image)

                                    //when taking image is success
                                    val matrix = Matrix().apply {
                                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                                    }
                                    //rotate bitmap to good orientation
                                    val rotatedBitmap = Bitmap.createBitmap(
                                        image.toBitmap(),
                                        0,
                                        0,
                                        image.width,
                                        image.height,
                                        matrix,
                                        true
                                    )
                                    val prediction = rotatedBitmap.let { modelPredictor.predict(it) }
                                    prediction[0].let {
                                        text.value = if (it >= 0.5f) "dog" else "cat"
                                        percentage.value = if(text.value=="dog") "%.2f".format(it * 100) + "%" else "%.2f".format((1 - it) * 100) + "%"
                                    }
                                    isImageLoading.value = false
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    super.onError(exception)
                                    isImageLoading.value = false
                                    Log.e(
                                        "Taking photo with camera",
                                        "Couldn't take photo: ",
                                        exception
                                    )
                                }
                            }
                        )
                    }
                    //for custom ripple effect u must clip and set border shape
                    .indication(
                        interactionSource = remember { MutableInteractionSource() },
                        rememberRipple(radius = 50.dp)
                    )
                )
            }
            Text(
                text = text.value,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = percentage.value,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    }
}

@Preview
@Composable
private fun Preview_CameraContent() {
    CameraContent()
}
