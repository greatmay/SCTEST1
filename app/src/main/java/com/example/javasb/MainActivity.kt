package com.example.javasb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONException
import org.json.JSONObject

// register name to javascript
class WebAppInterface(private val context: Context) {
    @JavascriptInterface
    fun get_name(): String {
        return "Thitipong Sornjan"
    }

    @JavascriptInterface
    fun take_screenshot(callback: String) {

    }

    @JavascriptInterface
    fun device_info():String{

      return "{\"app_version\":1}"
    }

}


class MainActivity : AppCompatActivity() {
    /*
Declare the native method to call C++ code*/
external fun processJsonFromCPlusPlus(jsonString: String): String
external fun helloFromCpp(): String

    // Load the native library
   companion object {
        init {
            System.loadLibrary("javasb") // Ensure "native-lib" matches the C++ library name
     }
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
         val webView = findViewById<WebView>(R.id.webView1)
        true.also { webView.settings.javaScriptEnabled = it }
        val device_info = findViewById<Button>(R.id.button1)
        val screenshot = findViewById<Button>(R.id.button2)
        webView.addJavascriptInterface(WebAppInterface(this), "SC_INTERFACE")

        device_info.setOnClickListener {
            // Show an alert dialog with just an OK button
            val builder = AlertDialog.Builder(this)
            webView.evaluateJavascript("SC_INTERFACE.device_info()") { jsonString ->
              Log.d("WebView", "Received JSON: $jsonString")

                val cleanJsonString = jsonString
                    .replace("\\\"", "\"") // Remove backslashes
                    .trim('"')  // Remove outer quotes

                // Parsing the JSON
                try {
                    val jsonObject = JSONObject(cleanJsonString)
                    val message = jsonObject.getString("app_version")
                    builder.setMessage(message)
                        .setPositiveButton("CLOSE") { dialog, id ->
                            // Handle the OK button click if needed
                        }
                    builder.create().show()

                  Toast.makeText(this,  helloFromCpp(), Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_SHORT).show()
                }
            }

        }
        screenshot.setOnClickListener{
            webView.evaluateJavascript(/* script = */ "SC_INTERFACE.take_screenshot('handleScreenshot')"){result ->
                println("JS Result: $result")
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }

        }

        webView.loadUrl("file:///android_asset/home.html")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}
