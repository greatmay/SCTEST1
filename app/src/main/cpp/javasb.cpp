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
#include "json/json.h"  // You can use JSON library like JsonCpp



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
                                                              jstring json_string,jstring key) {

    // Convert jstring (JSON) to std::string
    const char *jsonChars = env->GetStringUTFChars(json_string, nullptr);
    std::string jsonStr(jsonChars);
    env->ReleaseStringUTFChars(json_string, jsonChars);

    // Convert jstring (key) to std::string
    const char *keyChars = env->GetStringUTFChars(key, nullptr);
    std::string keyStr(keyChars);
    env->ReleaseStringUTFChars(key, keyChars);

    // Parse JSON using jsoncpp
    Json::CharReaderBuilder reader;
    Json::Value root;
    std::string errs;
    std::istringstream s(jsonStr);

    if (!Json::parseFromStream(reader, s, &root, &errs)) {
        return env->NewStringUTF("Error: Invalid JSON");
    }

    // Extract value dynamically based on key
    if (!root.isMember(keyStr)) {
        return env->NewStringUTF("Error: Key not found");
    }

    std::string value = root[keyStr].asString();

    // Return the extracted value as jstring
    return env->NewStringUTF(value.c_str());
}



// Utility function to split string by delimiter (dot notation)
std::vector<std::string> splitString(const std::string &s, char delimiter) {
    std::vector<std::string> tokens;
    std::stringstream ss(s);
    std::string token;
    while (std::getline(ss, token, delimiter)) {
        tokens.push_back(token);
    }
    return tokens;
}

// Function to traverse the JSON based on dot notation path
std::string getValueByPath(const Json::Value &root, const std::vector<std::string> &path) {
    const Json::Value *current = &root;

    for (const std::string &key : path) {
        if (current->isMember(key)) {
            current = &(*current)[key];
        } else {
            return "";  // Return empty string if any part of the path is not found
        }
    }

    // If the value is found, return it as a string
    if (current->isString()) {
        return current->asString();
    } else if (current->isInt()) {
        return std::to_string(current->asInt());
    } else if (current->isDouble()) {
        return std::to_string(current->asDouble());
    }

    return "";  // Return empty string if value is not a supported type
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_javasb_MainActivity_processJsonFromCPlusPlus_1byPath(JNIEnv *env, jobject thiz,
                                                                      jstring json_string,
                                                                      jstring path_object) {

    // Convert jstring (JSON) to std::string
    const char *jsonChars = env->GetStringUTFChars(json_string, nullptr);
    std::string jsonStr(jsonChars);
    env->ReleaseStringUTFChars(json_string, jsonChars);

    // Convert jstring (path) to std::string
    const char *pathChars = env->GetStringUTFChars(path_object, nullptr);
    std::string pathStr(pathChars);
    env->ReleaseStringUTFChars(path_object, pathChars);

    // Parse JSON using jsoncpp
    Json::CharReaderBuilder reader;
    Json::Value root;
    std::string errs;
    std::istringstream s(jsonStr);

    if (!Json::parseFromStream(reader, s, &root, &errs)) {
        return env->NewStringUTF("Error: Invalid JSON");
    }

    // Split the path string by dot (.) to get individual keys
    std::vector<std::string> path = splitString(pathStr, '.');

    // Get the value at the specified path
    std::string value = getValueByPath(root, path);

    // Return the value or an error message if not found
    if (value.empty()) {
        return env->NewStringUTF("Error: Path not found");
    } else {
        return env->NewStringUTF(value.c_str());
    }

}