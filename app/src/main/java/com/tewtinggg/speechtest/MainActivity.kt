package com.tewtinggg.speechtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var txtResult: TextView
    private val REQ_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtResult = findViewById(R.id.txtResult)
        val btnListen = findViewById<Button>(R.id.btnListen)

        // Diagnóstico: ¿el dispositivo tiene el servicio de reconocimiento?
        val disponible = SpeechRecognizer.isRecognitionAvailable(this)
        txtResult.text = "Reconocimiento disponible: $disponible"

        if (!disponible) {
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val texto = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                txtResult.text = "Resultado: ${texto?.getOrNull(0) ?: "vacío"}"
            }
            override fun onError(error: Int) {
                txtResult.text = "Error código: $error"
            }
            override fun onReadyForSpeech(params: Bundle?) {
                txtResult.text = "Listo, hablá..."
            }
            override fun onBeginningOfSpeech() {
                txtResult.text = "Escuchando..."
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                txtResult.text = "Procesando..."
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        btnListen.setOnClickListener {
            val tienePermiso = ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (!tienePermiso) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO), REQ_CODE
                )
                Toast.makeText(this, "Concedé el permiso y tocá de nuevo", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR")
            }
            speechRecognizer.startListening(intent)
        }
    }

    override fun onDestroy() {
        if (::speechRecognizer.isInitialized) speechRecognizer.destroy()
        super.onDestroy()
    }
}
