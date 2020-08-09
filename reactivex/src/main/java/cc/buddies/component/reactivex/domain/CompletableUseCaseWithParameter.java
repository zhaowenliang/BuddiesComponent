package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Completable;

public interface CompletableUseCaseWithParameter<P> {
    Completable execute(P parameter);
}
