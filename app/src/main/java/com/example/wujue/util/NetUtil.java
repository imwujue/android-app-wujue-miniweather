package com.example.wujue.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;

    public static int getNetworkState(Context context){
        ConnectivityManager myConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo myNetworkInfo = myConnManager.getActiveNetworkInfo();
        if(myNetworkInfo == null){
            return NETWORK_NONE;
        }
        else{
            int nType = myNetworkInfo.getType();
            if(nType == myConnManager.TYPE_WIFI){
                return NETWORK_WIFI;
            }
            else if(nType == myConnManager.TYPE_MOBILE){
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NONE;
    }

}