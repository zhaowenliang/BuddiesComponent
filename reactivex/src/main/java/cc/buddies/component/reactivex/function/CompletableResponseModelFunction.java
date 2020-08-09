package cc.buddies.component.reactivex.function;

import cc.buddies.component.reactivex.exception.ResponseException;
import cc.buddies.component.reactivex.model.ResponseModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.functions.Function;

/**
 * 转换RxJava观察者Function
 * <p>该方法使用Completable.error抛出了自定义异常，所以在订阅的时候需要订阅error处理方法。
 * <p>如果未手动处理异常，则需要在全局配置RxJavaPlugins.setErrorHandler(handler)，以捕获未处理异常。
 *
 * @param <T> 观察数据泛型
 */
public class CompletableResponseModelFunction<T> implements Function<ResponseModel<T>, CompletableSource> {

    @Override
    public CompletableSource apply(ResponseModel<T> responseModel) throws Throwable {
        if (responseModel == null) {
            return Completable.error(new Throwable("数据格式不正确"));
        }

        if (responseModel.getCode() != 0) {
            int code = responseModel.getCode();
            String message = responseModel.getMessage();
            return Completable.error(new ResponseException(code, message));
        }

        return Completable.complete();
    }

}
