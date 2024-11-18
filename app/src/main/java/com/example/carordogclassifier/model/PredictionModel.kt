package com.example.carordogclassifier.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PredictionModel(
    private val context: Context
) {
    private val interpreter: Interpreter

    init {
        interpreter = createInterpreter()
    }

    fun predict(originalBitmap: Bitmap): FloatArray {
        // Scale bitmap to size [128, 128]
        val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(originalBitmap, BITMAP_SIZE, BITMAP_SIZE, true)

        val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)

        // Make TensorBuffer with size [1, 128, 128, 3]
        val input = TensorBuffer.createFixedSize(intArrayOf(1, BITMAP_SIZE, BITMAP_SIZE, 3), DataType.FLOAT32)
        input.loadBuffer(byteBuffer)

        val outputArr = Array(1) { FloatArray(1) }

        // Predict
        interpreter.run(input.buffer, outputArr)

        return outputArr[0]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * BITMAP_SIZE * BITMAP_SIZE * 3) // 128x128 RGB image
        byteBuffer.order(ByteOrder.nativeOrder())

        // normalize to [0, 1]
        for (y in 0 until BITMAP_SIZE) {
            for (x in 0 until BITMAP_SIZE) {
                val pixel = bitmap.getPixel(x, y)
                byteBuffer.putFloat(Color.red(pixel) / 255.0f)  // Red
                byteBuffer.putFloat(Color.green(pixel) / 255.0f)  // Green
                byteBuffer.putFloat(Color.blue(pixel) / 255.0f)  // Blue
            }
        }
        return byteBuffer
    }


    private fun createInterpreter(): Interpreter {
        val tfLiteOptions = Interpreter.Options() //can be configure to use GPUDelegate
        return Interpreter(FileUtil.loadMappedFile(context, MODEL_FILENAME), tfLiteOptions)
    }

    companion object {
        const val BITMAP_SIZE = 128
        const val MODEL_FILENAME = "model.tflite"
    }
}
