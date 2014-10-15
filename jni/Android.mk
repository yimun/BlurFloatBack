LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PRELINK_MODULE := true

#LOCAL_SRC_FILES:= screenshot.c

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE := libscrcap
LOCAL_SHARED_LIBRARIES := libcutils libz liblog
LOCAL_STATIC_LIBRARIES := libpng
LOCAL_C_INCLUDES += external/zlib

include $(BUILD_SHARED_LIBRARY)
