package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Completable;

public interface CompletableUseCase {
    Completable execute();
}
