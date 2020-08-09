package cc.buddies.component.reactivex.usecase;

import io.reactivex.rxjava3.core.Observable;

public interface ObservableUseCase<T> {
    Observable<T> execute();
}
