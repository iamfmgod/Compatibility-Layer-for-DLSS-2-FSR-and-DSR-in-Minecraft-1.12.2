#ifndef FSR_BRIDGE_H
#define FSR_BRIDGE_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_invoke
  (JNIEnv *, jclass, jint, jint, jint, jint, jint);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_setRenderScale
  (JNIEnv *, jclass, jfloat);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_reinitializeFSR
  (JNIEnv *, jclass, jint, jint, jint, jint);

#ifdef __cplusplus
}
#endif

#endif