package com.example.psalmify

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Locale


private const val ARG_PARAM1 = "psalm_number"
private const val ARG_PARAM2 = "psalm_content"

class DetailsFragment : Fragment(), TextToSpeech.OnInitListener {

    private var psalm_number: Int = 0
    private var psalm_content: String? = null

    private lateinit var tts: TextToSpeech
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonPause: ImageButton
    private lateinit var buttonStop: ImageButton

    private var isPaused = false
    private var spokenTextLength = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            psalm_number = it.getInt(ARG_PARAM1)
            psalm_content = it.getString(ARG_PARAM2)
        }
        tts = TextToSpeech(context, this)
        checkTTSData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        val textViewPsalmNumber = view.findViewById<TextView>(R.id.textViewPsalmNumberDetails)
        val textViewPsalmContent = view.findViewById<TextView>(R.id.textViewPsalmContent)

        buttonPlay = view.findViewById(R.id.imageButtonPlay)
        buttonPause = view.findViewById(R.id.imageButtonPause)
        buttonStop = view.findViewById(R.id.imageButtonStop)

        textViewPsalmNumber.text = psalm_number.toString()
        textViewPsalmContent.text = psalm_content

        buttonPlay.setOnClickListener {
            psalm_content?.let { content ->
                tts.setLanguage(Locale("ro", "RO"))

                val remainingText = content.substring(spokenTextLength)
                isPaused = false
                speak(remainingText)

                buttonPlay.isEnabled = false
            }
        }

        buttonPause.setOnClickListener {
            if (!isPaused) {
                tts.stop()
                isPaused = true
                buttonPlay.isEnabled = true
            }
        }

        buttonStop.setOnClickListener {
            tts.stop()
            isPaused = false
            spokenTextLength = 0
            buttonPlay.isEnabled = true
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        private const val CHECK_TTS_DATA_CODE = 1234
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("ro", "RO"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show()
                promptInstallTTSData()
            }
            val availableLocales = tts.availableLanguages
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}
                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    spokenTextLength = start
                }
            })
        } else {
            Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speak(text: String) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "utteranceId")
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
    private fun checkTTSData() {
        val checkIntent = Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        try {
            startActivityForResult(checkIntent, CHECK_TTS_DATA_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Text-to-Speech not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun promptInstallTTSData() {
        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
        try {
            startActivity(installIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Unable to install Text-to-Speech data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHECK_TTS_DATA_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                val ttsIntent = Intent()
                ttsIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
                ttsIntent.setPackage("com.google.android.tts")
                tts = TextToSpeech(context, this, "com.google.android.tts")
            } else {
                promptInstallTTSData()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}