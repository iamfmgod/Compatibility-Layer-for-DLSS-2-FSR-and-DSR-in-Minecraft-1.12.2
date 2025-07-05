#ifndef DLSS_BRIDGE_H
#define DLSS_BRIDGE_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DLSSBridge_invoke
  (JNIEnv *, jclass, jint, jint, jint, jint, jint);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DLSSBridge_setQualityMode
  (JNIEnv *, jclass, jstring);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DLSSBridge_setRenderScale
  (JNIEnv *, jclass, jfloat);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DLSSBridge_reinitializeDLSS
  (JNIEnv *, jclass, jint, jint, jint, jint);

#ifdef __cplusplus
}
#endif

#endif