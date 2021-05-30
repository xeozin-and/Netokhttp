package kr.co.duolabs.netokhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 동기 방식
        execute_use()

        // 콜백 방식
        // enqueue()
    }

    // execute
    private fun execute_use() {
        // Dispatchers.Main (UI Thread), Dispatchers.Default (Thread)
        // launch(sync), async

        CoroutineScope(Dispatchers.Main).launch {
            val html = CoroutineScope(Dispatchers.Default).async {
                val client = OkHttpClient.Builder().build()
                val req = Request.Builder().url("https://www.google.com").build()
                client.newCall(req).execute().use { response ->
                    return@async if (response.body != null) {
                        response.body!!.string()
                    } else {
                        "no response"
                    }
                }
            }.await() // 비동기 방식은 await()

            val mTextMain = findViewById<TextView>(R.id.myText)
            mTextMain.text = html
        }
    }

    // enqueue
    private fun enqueue() {
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url("https://www.google.com").build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("asdf", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    val mTextMain = findViewById<TextView>(R.id.myText)
                    mTextMain.text = response.body!!.string()
                }
            }
        })
    }
}