package rxfamily.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import rxfamily.application.RxApplication;
import rxfamily.utils.NetWorkUtils;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public static ArrayList<networkEventHandler> mListeners = new ArrayList<networkEventHandler>();
    private static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NET_CHANGE_ACTION)) {
            RxApplication.mNetWorkState = NetWorkUtils.getNetworkState(context);
            if (mListeners.size() > 0)// 通知接口完成加载
                for (networkEventHandler handler : mListeners) {
                    handler.onNetworkChange();
                }
        }
    }

    public static abstract interface networkEventHandler {
        public abstract void onNetworkChange();
    }
}
