
/**********************************************************************
* @file SystemEventJni
*
* @brief the implementation of systemEvent jni
*
* Code History:
*      [2015-05-27] hank, Initial version.
*
* Code Review:
*
************************************************************************/

#include <jni.h>
#include <GetEvent.h>


#define EVENT_JNIREG_CLASS 	"com/smallzhi/systemrestore/SystemEvent"
#define EVENT_TAG            "SystemEvent"    // log tag



//
// callback
//

static JavaVM* g_JavaVm = XNULL;
static jclass  g_clazz = XNULL;


/**
 * @brief  event monitor start
 * @return false means failure
 * @return true means success
 */
static jboolean   MonitorStartJni(JNIEnv* env, jclass clazz)
{
	int result = EVENT_ERROR;
	result	=	event_monitor_start(EventReportCallbackJni);
	return result;
}

/**
 * @brief  event monitor start
 * @return false means failure
 * @return true means success
 */
static jboolean   MonitorStopJni(JNIEnv* env, jclass clazz)
{
	int result = EVENT_ERROR;
	result	=	event_monitor_stop();
	return result;

}

/**
 * @brief system event report callback
 * @param eventType is the value of system event
 */

static void EventReportCallbackJni(int eventType)
{

}


static JNINativeMethod gMethods[] = {
    { "MonitorStart",   "()Z",       (void*)MonitorStartJni },      // monitor start
    { "MonitorStop", 	"()Z",       (void*)MonitorStopJni}     	// monitor end
};


static int registerNativeMethods(JNIEnv* env, const char* className,
        JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
//         XOS_TRACE(YKB_TAG, "===> registerNativeMethods FindClass failure.");
        return JNI_FALSE;
    }

    // create global ref
    g_clazz = (jclass)(env->NewGlobalRef(clazz));

//    XOS_TRACE(YKB_TAG, "===> registerNativeMethods FindClass success.");

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}



static int registerNatives(JNIEnv* env)
{
    if (!registerNativeMethods(env,
        YKB_JNIREG_CLASS,
        gMethods,
        sizeof(gMethods) / sizeof(gMethods[0]))) {

        return JNI_FALSE;
    }

    return JNI_TRUE;
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    g_JavaVm = vm;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (NULL == env) {
        return -1;
    }

    if (!registerNatives(env)) { // register method
        return -1;
    }

    return JNI_VERSION_1_4;
}
