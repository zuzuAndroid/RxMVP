package rxfamily.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.MainThread;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;
import rxfamily.view.BaseActivity;

public class PermissionsUtils {

    public static Observable<Boolean> getCallPhoneGrant(BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(
            Manifest.permission.CALL_PHONE//拨打电话
        );
        return request;
    }

    public static Observable<Boolean> getCameraGrant(BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        );
        return request;
    }

    //存储
    public static Observable<Boolean> getStorageGrant(BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        return request;
    }

    //位置
    public static Observable<Boolean> getLocationGrant(BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
        return request;
    }

    //日历
    public static Observable<Boolean> getCalenderGrant(BaseActivity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        );
        return request;
    }

    @MainThread
    public static void showPermissionDeniedDialog(final Activity activity, final boolean isFinish) {
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new SlideBottomExit();

        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.titleTextSize(16);
        dialog.setCancelable(false);
        dialog.content("请设置应用程序的使用权限")
                .btnText("取消", "设置")
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        if (isFinish) activity.finish();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        Intent intent =  new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        activity.startActivity(intent);
                        if (isFinish) activity.finish();
                    }
                }
        );

    }
}
