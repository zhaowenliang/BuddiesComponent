package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Observable;

public interface UseCase<T> {
    Observable<T> execute();
}
