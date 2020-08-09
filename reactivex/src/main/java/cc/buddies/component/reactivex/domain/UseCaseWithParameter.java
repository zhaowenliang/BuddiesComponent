package cc.buddies.component.reactivex.domain;

import io.reactivex.rxjava3.core.Observable;

public interface UseCaseWithParameter<P, R> {
    Observable<R> execute(P parameter);
}
