package rxfamily.download.util;

import android.content.Context;

/**
 * Created by hly on 16/8/26.
 * email hly910206@gmail.com
 */
public class NetErrStringUtils {

    public static final int ERR_404 = 404;

    public static final int ERR_500 = 500;

    public static final int ERR_502 = 502;

    public static String getErrString(Context context, int code) {
        String result;
        switch (code) {
            case ERR_404:
                result = "404";
                break;
            case ERR_500:
                result = "500";
                break;
            case ERR_502:
                result = "502";
                break;
            default:
                result = "error";
                break;
        }
        return result;
    }

    public static String getErrString(Context context, Throwable t) {
        String result;
        if (t instanceof java.net.SocketTimeoutException) {
            result = "网络连接超时";
        } else if (t != null && t.getMessage() != null && t.getMessage().equalsIgnoreCase("canceled")) {
            result = "";
        } else {
            result = "网络错误";
        }
        return result;
    }
}
