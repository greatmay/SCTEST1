package com.example.javasb

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import android.util.Base64


class WebAppInterface(private val context: Context) {
    @JavascriptInterface
    // register name to javascript
    fun get_name(): String {
        return "Thitipong Sornjan"
    }

    //call handleScreenshot callback
    @JavascriptInterface
    fun take_screenshot(callback: String) {

    }

    //simulate json from webpage
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
private external fun processJsonFromCPlusPlus(jsonString: String,key: String): String
private external fun processJsonFromCPlusPlusbyPath(jsonString: String,pathObject: String): String
private external fun helloFromCpp(): String // for test JNI

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
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        val device_info = findViewById<Button>(R.id.button1)
        val screenshot = findViewById<Button>(R.id.button2)
        webView.addJavascriptInterface(WebAppInterface(this), "SC_INTERFACE")

        fun captureWebViewScreenshot() {
            val bitmap = Bitmap.createBitmap(webView.width, webView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            webView.draw(canvas)

            // Convert bitmap to JPEG
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)  // 90 is the quality (0-100)

            // Convert the JPEG to Base64
            val base64Image = "data:image/jpeg;base64," + Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            // Pass the base64 image to JavaScript for handling
            webView.evaluateJavascript( "SC_INTERFACE.take_screenshot(handleScreenshot('$base64Image'));",null)
        }

        device_info.setOnClickListener {
            // Show an alert dialog with just an OK button
            val builder = AlertDialog.Builder(this)
            webView.evaluateJavascript("SC_INTERFACE.device_info()") { jsonString ->
                Log.d("WebView", "Received JSON: $jsonString")

                val cleanJsonString = jsonString
                    .replace("\\\"", "\"") // Remove backslashes
                    .trim('"')  // Remove outer quotes

                val appver = processJsonFromCPlusPlus(cleanJsonString, "app_version")
                val pakname = processJsonFromCPlusPlus(cleanJsonString, "package_name")
                val srcwidth = processJsonFromCPlusPlus(cleanJsonString, "screen_width")
                val srcheight = processJsonFromCPlusPlus(cleanJsonString, "screen_height")
                val manufac = processJsonFromCPlusPlus(cleanJsonString, "device_manufacturer")
                val modeldev = processJsonFromCPlusPlus(cleanJsonString, "device_model")
                val kerver =
                    processJsonFromCPlusPlusbyPath(cleanJsonString, "native_info.kernel_version")
                val coreval = processJsonFromCPlusPlusbyPath(
                    cleanJsonString,
                    "native_info.cpu_info.cpu_cores"
                )
                val corefrq = processJsonFromCPlusPlusbyPath(
                    cleanJsonString,
                    "native_info.cpu_info.cpu_freq"
                )
                val ramusage = processJsonFromCPlusPlusbyPath(cleanJsonString, "native_info.ram_total")
                val apphead = Html.fromHtml("<b>App info:</b>", Html.FROM_HTML_MODE_LEGACY)
                val pakhead = Html.fromHtml("<b>Package:</b>", Html.FROM_HTML_MODE_LEGACY)
                val srchead = Html.fromHtml("<b> Screen Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                val wdhead = Html.fromHtml("<b> Width:</b>", Html.FROM_HTML_MODE_LEGACY)
                val hihead = Html.fromHtml("<b> Height:</b>", Html.FROM_HTML_MODE_LEGACY)
                val devhead = Html.fromHtml("<b> Device Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                val manuhead = Html.fromHtml("<b>Manufacturer:</b>", Html.FROM_HTML_MODE_LEGACY)
                val modelhead = Html.fromHtml("<b>Model:</b>", Html.FROM_HTML_MODE_LEGACY)
                val cpuinfohd = Html.fromHtml("<b>CPU Info:</b>", Html.FROM_HTML_MODE_LEGACY)
                val corehd = Html.fromHtml("<b>Cores:</b>", Html.FROM_HTML_MODE_LEGACY)
                val frqhd = Html.fromHtml("<b>Frequency:</b>", Html.FROM_HTML_MODE_LEGACY)
                val sysinhd = Html.fromHtml("<b>System info:</b>", Html.FROM_HTML_MODE_LEGACY)
                val ramtotal = Html.fromHtml("<b>RAM Total:</b>", Html.FROM_HTML_MODE_LEGACY)
                val kerlverhd = Html.fromHtml("<b>Kernel Version:</b>", Html.FROM_HTML_MODE_LEGACY)
                val alldata = Html.fromHtml(
                    "$apphead<br>Version $appver<br>$pakhead$pakname<br><br>$srchead<br>$wdhead $srcwidth px" +
                            "<br>$hihead $srcheight px<br><br>$devhead <br>$manuhead $manufac<br>$modelhead $modeldev" +
                            "<br><br>$cpuinfohd<br>$corehd $coreval <br>$frqhd $corefrq MHz <br><br>$sysinhd" +
                            "<br>$ramtotal $ramusage byte<br> $kerlverhd $kerver",
                    Html.FROM_HTML_MODE_LEGACY
                )
                builder.setMessage(alldata)
                    .setTitle("Device Information")
                    .setPositiveButton("CLOSE") { dialog,_ ->
                        // Handle the OK button click if needed
                        dialog.dismiss() // Close the dialog
                    }
                builder.create().show()
            }
        }

        screenshot.setOnClickListener{

            captureWebViewScreenshot()

        }

        webView.loadUrl("file:///android_asset/home.html")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

}
