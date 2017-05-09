package rxfamily.mvp;

import com.trello.rxlifecycle.LifecycleTransformer;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rxfamily.bean.BaseBean;
import rxfamily.net.HttpResult;
import rxfamily.net.HttpService;
import rxfamily.net.ResponseCallback;
import rxfamily.net.ResponseHandler;
import rxfamily.net.RetryWithDelay;


public class MvpBaseModel {

    public HttpService httpService;

    public MvpBaseModel(String base_url, boolean has_cache) {
        httpService = HttpService.getInstance(base_url,has_cache);
    }

    public <T> T getApiService(Class<T> api){
        return httpService.getApiService(api);
    }

    public Subscription getData(Observable observable,final ResponseCallback callback){
        Subscription sub = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 3000))//总共重试3次，重试间隔3秒
                .subscribe(new Action1<BaseBean>() {
                    @Override
                    public void call(BaseBean result) {
                        callback.onSuccess(result);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFaild(ResponseHandler.get(throwable));
                    }
                });
        return  sub;
    }

    public Subscription getData(Observable observable, LifecycleTransformer transformer,final ResponseCallback callback){
        Subscription sub = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .compose(transformer)
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 3000))//总共重试3次，重试间隔3秒
                .subscribe(new Action1<HttpResult>() {
                    @Override
                    public void call(HttpResult result) {
                        callback.onSuccess(result);
                        /*if(result.code == 200){
                            callback.onSuccess(result);
                        }else{
                            callback.onFaild(ResponseHandler.apiError(result.code));
                        }*/
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFaild(ResponseHandler.get(throwable));
                    }
                });
        return  sub;
    }

    public Subscription getDataWithMap(Observable observable, Func1 map, final ResponseCallback callback){
        Subscription sub = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 3000))//总共重试3次，重试间隔3秒
                .map(map)
                .subscribe(new Action1<HttpResult>() {
                    @Override
                    public void call(HttpResult result) {
                        callback.onSuccess(result);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFaild(ResponseHandler.get(throwable));
                    }
                });
        return  sub;
    }
/*
    public Subscription upload(Observable observable,File file,final ResponseCallback callback){
        if (file != null && file.exists()) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Subscription sub = observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new RetryWithDelay(3, 3000))//总共重试3次，重试间隔3秒
                    .subscribe(new Action1<HttpResult>() {
                        @Override
                        public void call(HttpResult result) {
                            callback.onSuccess(result);

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            callback.onFaild(ResponseHandler.get(throwable));
                        }
                    });

            return sub;
        }

        return null;
    }
    */
}
