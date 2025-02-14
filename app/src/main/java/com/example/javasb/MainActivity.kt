package com.example.javasb

import android.content.Context
import android.os.Bundle
import android.text.Html
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
      //simulate json string
      return buildString {
          append("{\"app_version\":\"1.0\",")
          append("\"package_name\": \"io.screencloud.assignment.android_sen\",")
          append("\"screen_width\": 2560,")
          append("\"screen_height\": 1688,")
          append("\"screen_density\": 2,")
          append("\"android_version\": 30,")
          append("\"device_manufacturer\": \"Genymobile\",")
          append("\"device_model\": \"Pixel C\",")
          append("\"native_info\": {")
          append("\"ram_total\": \"3148120064\",")
          append("\"cpu_info\": {")
          append("\"cpu_cores\": \"3\",")
          append("\"cpu_freq\": \"3072.000\" ")
          append(" },")
          append("\"kernel_version\":11,")
          append("\"build_fingerprint\": \"google/vbox86p/vbox86p:11/RQ\",")
          append("\"hardware_serial\": \"vbox86\"")
          append(" } }")
      }

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

                ///Parsing the JSON
                try {
                    val jsonObject = JSONObject(cleanJsonString)
                    val appver = jsonObject.getString("app_version")
                    val pakname = jsonObject.getString("package_name")
                    val srcwidth =jsonObject.getString("screen_width")
                    val srcheight =jsonObject.getString("screen_height")
                    val manufac =jsonObject.getString("device_manufacturer")
                    val modeldev =jsonObject.getString("device_model")
                    val natobj = jsonObject.getJSONObject("native_info")
                    val kerver = natobj.getString("kernel_version")
                    val coreobj = natobj.getJSONObject("cpu_info")
                    val coreval = coreobj.getString("cpu_cores")
                    val corefrq = coreobj.getString("cpu_freq")
                    val ramusage =natobj.getString("ram_total")
                    val apphead = Html.fromHtml("<b>App info:</b>",Html.FROM_HTML_MODE_LEGACY)
                    val pakhead = Html.fromHtml("<b>Package:</b>",Html.FROM_HTML_MODE_LEGACY)
                    val srchead = Html.fromHtml("<b> Screen Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val wdhead = Html.fromHtml("<b> Width:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val hihead = Html.fromHtml("<b> Height:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val devhead = Html.fromHtml("<b> Device Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val manuhead = Html.fromHtml("<b>Manufacturer:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val modelhead =Html.fromHtml("<b>Model:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val cpuinfohd =Html.fromHtml("<b>CPU Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val corehd =Html.fromHtml("<b>Cores:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val frqhd =Html.fromHtml("<b>Frequency:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val sysinhd =Html.fromHtml("<b>System info:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val ramtotal =Html.fromHtml("<b>RAM Total:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val kerlverhd =Html.fromHtml("<b>Kernel Version:</b>", Html.FROM_HTML_MODE_LEGACY)
                    val alldata = Html.fromHtml("$apphead<br>Version $appver<br>$pakhead$pakname<br><br>$srchead<br>$wdhead $srcwidth px" +
                            "<br>$hihead $srcheight px<br><br>$devhead <br>$manuhead $manufac<br>$modelhead $modeldev" +
                            "<br><br>$cpuinfohd<br>$corehd $coreval <br>$frqhd $corefrq MHz <br><br>$sysinhd" +
                            "<br>$ramtotal $ramusage byte<br> $kerlverhd $kerver",Html.FROM_HTML_MODE_LEGACY)
                    builder.setMessage(alldata)
                        .setTitle("Device Information")
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
