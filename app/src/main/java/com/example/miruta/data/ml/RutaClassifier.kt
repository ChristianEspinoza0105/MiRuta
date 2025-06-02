package com.example.miruta.data.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class RutaClassifier(context: Context) {

    private val interpreter: Interpreter
    private val tokenizer: Tokenizer
    private val maxSeqLen = 20

    init {
        interpreter = Interpreter(loadModelFile(context, "modelo_rutas.tflite"))
        tokenizer = loadTokenizer(context, "tokenizer.json")
    }

    fun esMensajeRelacionado(mensaje: String): Boolean {
        val tokens = tokenizer.textsToSequences(mensaje).toMutableList()

        while (tokens.size < maxSeqLen) tokens.add(0)
        if (tokens.size > maxSeqLen) tokens.subList(maxSeqLen, tokens.size).clear()

        val input = Array(1) { FloatArray(maxSeqLen) }
        for (i in tokens.indices) {
            input[0][i] = tokens[i].toFloat()
        }

        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)

        val prediccion = output[0][0]
        return prediccion >= 0.5
    }


    private fun loadModelFile(context: Context, fileName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadTokenizer(context: Context, fileName: String): Tokenizer {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return Tokenizer.fromJson(json)
    }
}
