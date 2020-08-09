package cc.buddies.component.reactivex.usecase;

import io.reactivex.rxjava3.core.Single;

public interface SingleUseCase<T> {
    Single<T> execute();
}
