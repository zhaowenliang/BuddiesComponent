package cc.buddies.component.reactivex.usecase;

import io.reactivex.rxjava3.core.Completable;

public interface CompletableUseCaseWithParameter<P> {
    Completable execute(P parameter);
}
