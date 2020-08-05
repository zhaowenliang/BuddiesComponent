package cc.buddies.component.test0.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class Test2ViewModel extends ViewModel {

    private MutableLiveData<Integer> mNumLiveData = new MutableLiveData<>();

    // switchMap变换
    private LiveData<String> mNameLiveData = Transformations.switchMap(mNumLiveData, this::getName);

    private LiveData<String> getName(Integer num) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        liveData.setValue(num + " Name");
        return liveData;
    }

    public MutableLiveData<Integer> getNumLiveData() {
        return mNumLiveData;
    }

    public LiveData<String> getNameLiveData() {
        return mNameLiveData;
    }

}
