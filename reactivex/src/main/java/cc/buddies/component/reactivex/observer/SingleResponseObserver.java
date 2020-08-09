package cc.buddies.component.reactivex.observer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class SingleResponseObserver<T> extends DisposableSingleObserver<T> {

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(@NonNull T t) {

    }

    @Override
    public void onError(@NonNull Throwable e) {

    }
}
