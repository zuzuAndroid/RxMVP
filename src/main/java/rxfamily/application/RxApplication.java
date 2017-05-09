package rxfamily.application;


import android.app.Application;

import rxfamily.utils.NetWorkUtils;


public class RxApplication extends Application {

    private static RxApplication sInstance;
    public static int mNetWorkState;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mNetWorkState = NetWorkUtils.getNetworkState(this);
    }

    public synchronized static  RxApplication getInstance(){
        return sInstance;
    }
}
