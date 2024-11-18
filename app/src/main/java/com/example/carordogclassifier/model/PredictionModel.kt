package com.example.carordogclassifier.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.example.carordogclassifier.ml.CnnModelOptimized
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
        // Initialize the optimized model instance
        interpreter = createInterpreter()
    }

    // Predicts output based on the input Bitmap
    fun predict(originalBitmap: Bitmap): FloatArray {
        // Skalowanie bitmapy do rozmiaru [64, 64], jak oczekuje model
        val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(originalBitmap, 64, 64, true)

        // Tworzenie ByteBuffer do załadowania bitmapy
        val byteBuffer = ByteBuffer.allocateDirect(4 * 64 * 64 * 3) // 64x64 obraz RGB
        byteBuffer.order(ByteOrder.nativeOrder())

        // Konwersja pikseli bitmapy na ByteBuffer (normalizacja do [0, 1])
        for (y in 0 until 64) {
            for (x in 0 until 64) {
                val pixel = scaledBitmap.getPixel(x, y)
                byteBuffer.putFloat(Color.red(pixel) / 255.0f)  // Czerwony kanał
                byteBuffer.putFloat(Color.green(pixel) / 255.0f)  // Zielony kanał
                byteBuffer.putFloat(Color.blue(pixel) / 255.0f)  // Niebieski kanał
            }
        }

        // Tworzenie TensorBuffer o odpowiednim kształcie [1, 64, 64, 3]
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 64, 64, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Przygotowanie tablicy na wynik (output)
        val outputArr = Array(1) { FloatArray(1) }

        // Uruchomienie modelu (wykonanie predykcji)
        interpreter.run(inputFeature0.buffer, outputArr)

        // Zwrócenie wyniku predykcji jako FloatArray
        return outputArr[0]
    }


    private fun createInterpreter(): Interpreter {
        val tfLiteOptions = Interpreter.Options()//can be configure to use GPUDelegate
        return Interpreter(FileUtil.loadMappedFile(context, MODEL_FILENAME), tfLiteOptions)
    }

    companion object {
        const val MODEL_FILENAME = "model.tflite"
    }
}
