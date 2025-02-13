// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("javasb");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("javasb")
//      }
//    }om.example.javasb

#include <jni.h>
#include <string>
//#include <json/json.h>  // You can use JSON library like JsonCpp



  /*Java_com_example_myapp_MainActivity_helloFromNative(JNIEnv* env, jobject) {
//std::string message = "Hello from Native!";
//return env->NewStringUTF(message.c_str());
}*/

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_javasb_MainActivity_helloFromCpp(JNIEnv *env, jobject thiz) {
    // TODO: implement helloFromCpp()
    std::string message = "Hello from C++!";
    return env->NewStringUTF(message.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_javasb_MainActivity_processJsonFromCPlusPlus(JNIEnv *env, jobject thiz,
                                                              jstring json_string) {
    // TODO: implement processJsonFromCPlusPlus()

}