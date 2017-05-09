package rxfamily.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle.android.ActivityEvent;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class RxAppCompatActivity2 extends AppCompatActivity {

    protected final BehaviorSubject<ActivityEvent> lifeSubject = BehaviorSubject.create();

    public <T> Observable.Transformer<T, T> bindUntilEvent(final ActivityEvent bindEvent) {
        //被监视的Observable
        final Observable<ActivityEvent> observable = lifeSubject.takeFirst(new Func1<ActivityEvent, Boolean>() {
            @Override
            public Boolean call(ActivityEvent event) {
                return event.equals(bindEvent);
            }
        });

        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> sourceOb) {
                return sourceOb.takeUntil(observable);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeSubject.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifeSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifeSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifeSubject.onNext(ActivityEvent.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifeSubject.onNext(ActivityEvent.STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifeSubject.onNext(ActivityEvent.DESTROY);
    }
}
