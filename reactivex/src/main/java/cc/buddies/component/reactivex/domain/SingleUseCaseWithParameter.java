package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Single;

public interface SingleUseCaseWithParameter<P, R> {
    Single<R> execute(P parameter);
}
