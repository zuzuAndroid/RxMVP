package rxfamily.net;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rxfamily.application.RxApplication;
import rxfamily.utils.NetWorkUtils;

public class HttpService {

    public final static int CONNECT_TIMEOUT = 5;
    public final static int READ_TIMEOUT = 10;
    public final static int WRITE_TIMEOUT = 10;

    private volatile static HttpService INSTANCE;
    private Retrofit httpService;

    public HttpService(String base_url,Boolean use_cache){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                  .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                  .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                  .addInterceptor(new LoggingInterceptor());//开启OKHttp的日志拦截

        if(use_cache){
            //设置缓存路径
            final File httpCacheDirectory = new File(RxApplication.getInstance().getCacheDir(), "okhttpCache");
            //Log.d("httpCacheDirectory", RxApplication.getInstance().getCacheDir().getAbsolutePath());
            Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);//缓存可用大小为10M

            httpClient.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                      .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                      .cache(cache);
        }

        httpService = new Retrofit.Builder().baseUrl(base_url)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                            .client(httpClient.build())
                                            .build();
    }

    public static HttpService getInstance(String base_url,Boolean use_cache) {
        if (INSTANCE == null) {
            synchronized (HttpService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpService(base_url,use_cache);
                }
            }
        }
        return INSTANCE;
    }

    public Retrofit getHttpService() {
        return httpService;
    }

    public <T> T getApiService(Class<T> api){
        return httpService.create(api);
    }

    /**
     * 拦截器，显示日志信息
     */
    public class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {

            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();

            long t1 = System.nanoTime();//请求发起的时间

            Log.i("HTTP",String.format("发送请求 %s on %s%n%s",request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();//收到响应的时间

            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);

            Log.i("HTTP",String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    (t2 - t1) / 1e6d,
                    response.headers()));

            return response;
        }
    }

    /**
     *  缓存
     */
    private final static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            //获取网络状态
            int netWorkState = NetWorkUtils.getNetworkState(RxApplication.getInstance());
            //无网络，请求强制使用缓存
            if (netWorkState == NetWorkUtils.NETWORK_NONE) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);

            switch (netWorkState) {
                case NetWorkUtils.NETWORK_MOBILE://mobile network 情况下缓存一分钟
                    int maxAge = 60;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();

                case NetWorkUtils.NETWORK_WIFI://wifi network 情况下不使用缓存
                    maxAge = 0;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();

                case NetWorkUtils.NETWORK_NONE://none network 情况下离线缓存4周
                    int maxStale = 60 * 60 * 24 * 4 * 7;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                default:
                    throw new IllegalStateException("network state is Erro!");
            }
        }
    };
}
