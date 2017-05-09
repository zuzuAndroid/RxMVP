package rxfamily.net;


public interface ResponseCallback<T> {

    void onSuccess(T t);

    void onFaild(String msg);
}
