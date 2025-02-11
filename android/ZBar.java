package org.cloudsky.cordovaPlugins;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;

public class ZBar extends CordovaPlugin {

    // Configuration ---------------------------------------------------

    private static int SCAN_CODE = 1;

    // State -----------------------------------------------------------

    private boolean isInProgress = false;
    private CallbackContext scanCallbackContext;
    public static boolean CONTINUE_SCAN = true;
    // Plugin API ------------------------------------------------------

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        this.scanCallbackContext = callbackContext;
        // 把CallbackContext存到单例工具类
        CordovaUtil.getInstance().setCallbackContext(callbackContext);
        if (action.equals("scan")) {
            if (isInProgress) {
                // 连续扫描，不能中断
//                callbackContext.error("A scan is already in progress!");
                // 再次调用扫描，放行解析
                CONTINUE_SCAN = true;
            } else {
                isInProgress = true;
                JSONObject params = args.optJSONObject(0);
                Context appCtx = cordova.getActivity().getApplicationContext();
                Intent scanIntent = new Intent(appCtx, ZBarScannerActivity.class);
                scanIntent.putExtra(ZBarScannerActivity.EXTRA_PARAMS, params.toString());
                cordova.startActivityForResult(this, scanIntent, SCAN_CODE);
            }
            return true;
        } else {
            return false;
        }
    }


    // External results handler ----------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == SCAN_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String barcodeValue = result.getStringExtra(ZBarScannerActivity.EXTRA_QRVALUE);
                    scanCallbackContext.success(barcodeValue);
                    break;
                case Activity.RESULT_CANCELED:
                    scanCallbackContext.error("cancelled");
                    break;
                case ZBarScannerActivity.RESULT_ERROR:
                    scanCallbackContext.error("Scan failed due to an error");
                    break;
                default:
                    scanCallbackContext.error("Unknown error");
            }
            isInProgress = false;
            scanCallbackContext = null;
        }
    }
}
