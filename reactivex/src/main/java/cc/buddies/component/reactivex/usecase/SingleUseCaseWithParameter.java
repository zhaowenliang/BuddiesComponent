package cc.buddies.component.reactivex.usecase;

import io.reactivex.rxjava3.core.Single;

public interface SingleUseCaseWithParameter<P, R> {
    Single<R> execute(P parameter);
}
