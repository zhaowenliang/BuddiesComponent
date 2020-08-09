package cc.buddies.component.reactivex.utils;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.MaybeTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxUtils {

    private RxUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static <T> ThreadIo2MainTransformer<T> io2Main() {
        return new ThreadIo2MainTransformer<>();
    }

    public static <T> ThreadAllIoTransformer<T> allIo() {
        return new ThreadAllIoTransformer<>();
    }

    /**
     * RxJava订阅在io线程，观察在主线程
     *
     * @param <T>
     */
    public static class ThreadIo2MainTransformer<T> implements ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            SingleTransformer<T, T>,
            MaybeTransformer<T, T>,
            CompletableTransformer {

        @Override
        public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public @NonNull CompletableSource apply(@NonNull Completable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public @NonNull Publisher<T> apply(@NonNull Flowable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public @NonNull MaybeSource<T> apply(@NonNull Maybe<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public @NonNull SingleSource<T> apply(@NonNull Single<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    }

    /**
     * RxJava订阅和观察均在io线程
     *
     * @param <T>
     */
    public static class ThreadAllIoTransformer<T> implements ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            SingleTransformer<T, T>,
            MaybeTransformer<T, T>,
            CompletableTransformer {

        @Override
        public @NonNull CompletableSource apply(@NonNull Completable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        }

        @Override
        public @NonNull Publisher<T> apply(@NonNull Flowable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        }

        @Override
        public @NonNull MaybeSource<T> apply(@NonNull Maybe<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        }

        @Override
        public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        }

        @Override
        public @NonNull SingleSource<T> apply(@NonNull Single<T> upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        }
    }

}
