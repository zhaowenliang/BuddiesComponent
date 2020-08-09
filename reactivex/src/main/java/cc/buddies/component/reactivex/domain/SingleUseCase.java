package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Single;

public interface SingleUseCase<T> {
    Single<T> execute();
}
