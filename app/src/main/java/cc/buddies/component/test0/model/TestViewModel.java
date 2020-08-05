package cc.buddies.component.test0.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TestViewModel extends ViewModel {

    private MutableLiveData<String> status;

    public MutableLiveData<String> getStatus() {
        if (status == null)
            status = new MutableLiveData<>();
        return status;
    }

}
