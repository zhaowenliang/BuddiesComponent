package cc.buddies.component.test0.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class Test1ViewModel extends ViewModel {

    private MutableLiveData<Integer> mNumLiveData = new MutableLiveData<>();

    // 通过Transformations.map()将Integer类型的值转换为String类型
    private LiveData<String> mStrLiveData = Transformations.map(mNumLiveData, String::valueOf);

    public MutableLiveData<Integer> getNumLiveData() {
        return mNumLiveData;
    }

    public LiveData<String> getStrLiveData() {
        return mStrLiveData;
    }

}
