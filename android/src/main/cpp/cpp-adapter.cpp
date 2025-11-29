#include <jni.h>
#include "ScheduledNotificationsOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::schedulednotifications::initialize(vm);
}
