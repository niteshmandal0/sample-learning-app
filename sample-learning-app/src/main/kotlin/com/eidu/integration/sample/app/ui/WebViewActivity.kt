package com.eidu.integration.sample.app.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.IOException


class WebViewActivity : ComponentActivity() {

    private var mediaPlayer = MediaPlayer()



    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        // ✅ Set Full-Screen Mode
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                )
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        val lessonId = intent.getStringExtra("lesson_id")
        val url = getUrlForLesson(lessonId)

        if (url == null) {
            finish()
            return
        }

        setContent {
            Toast.makeText(this, TEXT_ASSET, Toast.LENGTH_SHORT).show()

            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(AUDIO_ASSET) // Replace with your URL
                mediaPlayer.prepare() // Use prepareAsync() for streaming
                mediaPlayer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }


            FullScreenWebView(url)
        }
    }

    private fun getUrlForLesson(lessonId: String?): String? {
        return when (lessonId) {
            "en0000" -> "https://chimple.cc/microlink?courseid=en&chapterid=en00&lessonid=en0000&mlPartnerId=rocket&lang=en&end=blank"
            "en0001" -> "https://chimple.cc/microlink?courseid=en&chapterid=en00&lessonid=en0001&mlPartnerId=rocket&lang=en&end=blank"
            "en0002" -> "https://chimple.cc/microlink?courseid=en&chapterid=en00&lessonid=en0002&mlPartnerId=rocket&lang=en&end=blank"
            "ambulance" -> "https://chimple.cc/microlink?courseid=en&chapterid=en00&lessonid=ambulance&mlPartnerId=rocket&lang=en&end=blank"
            else -> null
        }
    }



    companion object {
        private const val TEXT_ASSET = "text.txt"
        private const val AUDIO_ASSET = "subfolder/audio.mp3"
        private const val IMAGE_ASSET = "subfolder/image.jpg"
        private val TAG = MainActivity::class.simpleName
    }
}

@Composable
fun FullScreenWebView(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize() // ✅ Make WebView take full screen
    )
}
