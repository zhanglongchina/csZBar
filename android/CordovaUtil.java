package org.cloudsky.cordovaPlugins;

import org.apache.cordova.CallbackContext;

public class CordovaUtil {
    private CallbackContext callbackContext;

    private static class CordovaUtilHolder {
        public final static CordovaUtil INSTANCE = new CordovaUtil();
    }

    private CordovaUtil() {

    }

    public static final CordovaUtil getInstance() {
        return CordovaUtilHolder.INSTANCE;
    }

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }
}
