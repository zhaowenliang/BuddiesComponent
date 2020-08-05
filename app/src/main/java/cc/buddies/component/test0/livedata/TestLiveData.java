package cc.buddies.component.test0.livedata;

import androidx.lifecycle.LiveData;

/**
 * 通过此方式，可以实时监听数据变化，并回调到观察者
 * <pre>
 *      mTestLiveData =TestLiveData.getInstance();
 *      mTestLiveData.observe(this, statusObserver);
 * </pre>
 */
public class TestLiveData extends LiveData<String> {

    private static TestLiveData sInstance;

    // 设计为单例模式
    public static TestLiveData getInstance() {
        if (sInstance == null) {
            sInstance = new TestLiveData();
        }
        return sInstance;
    }

    private TestLiveData() {
        // 创建一个获取位置的对象
    }


    // 当有一个处于活跃状态的观察者监听LiveData时会被调用，这表示开始获取位置信息。
    @Override
    protected void onActive() {
        super.onActive();
    }

    // 当没有任何处于活跃状态的观察者监听LiveData时会被调用。
    // 由于没有观察者在监听了，所以也没必要继续去获取位置信息了，这只会消耗更多的电量等等，因此就可以停止获取位置信息了。
    @Override
    protected void onInactive() {
        super.onInactive();
    }

//    //创建一个位置监听器
//    private LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onReceiveLocation(String location) {
//            //接受到位置信息后，更新数据
//            setValue(location);
//        }
//    };

}
