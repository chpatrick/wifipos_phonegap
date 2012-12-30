package com.backrow.wifipos;

import java.util.List;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class Wifi extends CordovaPlugin {
  private static final IntentFilter RESULTS_AVAILABLE
    = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
  
  @Override
  public boolean execute(String action, JSONArray args,
      final CallbackContext callbackContext) throws JSONException {
    
    LOG.d("wifi", "received action " + action);
    
    if (action.equals("scan")) {
      final Context ctx = cordova.getActivity();
      final WifiManager wifiManager = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
      
      ctx.registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          ctx.unregisterReceiver(this);

          List<ScanResult> results = wifiManager.getScanResults();
          
          JSONObject jsonResults = new JSONObject();
          for (ScanResult result : results) {
            JSONObject jsonResult = new JSONObject();
            
            try {
              jsonResult.put("ssid", result.SSID);
              jsonResult.put("level", result.level);
              jsonResults.put(result.BSSID, jsonResult);
            } catch (JSONException e) {
            }
          }
          callbackContext.success(jsonResults);
        }
      }, RESULTS_AVAILABLE);
      
      wifiManager.startScan();
      return true;
    }
    return false;
  }
}
