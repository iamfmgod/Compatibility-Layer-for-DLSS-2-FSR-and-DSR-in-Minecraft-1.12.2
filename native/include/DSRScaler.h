#ifndef DSR_SCALER_H
#define DSR_SCALER_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DSRScaler_scale
  (JNIEnv *, jclass, jint, jint, jint);

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DSRScaler_setScaleFactor
  (JNIEnv *, jclass, jfloat);

#ifdef __cplusplus
}
#endif

#endif