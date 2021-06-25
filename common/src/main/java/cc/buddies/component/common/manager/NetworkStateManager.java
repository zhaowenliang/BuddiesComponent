package cc.buddies.component.common.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * 网络状态变化监听管理器
 * <pre>
 *     1. 初始使用需要在Application中初始化：
 *          NetworkStateManager.getInstance().register(context);
 *     2. 页面观察：
 *          NetworkStateManager.getInstance().getNetStateLiveData().observe(this, netState -> Log.w("网络状态监听器", "NetState: " + netState));
 * </pre>
 */
public class NetworkStateManager {

    private static final String TAG = "NetworkStateManager";

    @NonNull
    private final MutableLiveData<NetState> mNetStateLiveData = new MutableLiveData<>();

    private NetState mNetState;

    private ConnectivityManager.NetworkCallback mNetworkCallback;

    private NetworkStateManager() {
    }

    public static NetworkStateManager getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    private static class SingleTonHolder {
        private static final NetworkStateManager INSTANCE = new NetworkStateManager();
    }

    /**
     * 网络状态枚举
     */
    public enum NetState {
        NONE(-1),
        CELLULAR(NetworkCapabilities.TRANSPORT_CELLULAR),
        WIFI(NetworkCapabilities.TRANSPORT_WIFI),
        OTHER(8),       // 这里是因为NetworkCapabilities#MAX_TRANSPORT为7
        ;

        private final int value;

        NetState(int value) {
            this.value = value;
        }

        public boolean hasNetwork() {
            return CELLULAR.value <= value && value <= OTHER.value;
        }
    }

    @NonNull
    public LiveData<NetState> getNetStateLiveData() {
        return mNetStateLiveData;
    }

    /**
     * 注册网络状态监听
     *
     * @param context ApplicationContext
     */
    public void register(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) return;

        if (mNetworkCallback == null) {
            mNetworkCallback = new InnerNetworkCallback();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivity.registerDefaultNetworkCallback(mNetworkCallback);
        } else {
            connectivity.registerNetworkCallback(createNetworkRequest(), mNetworkCallback);
        }
    }

    /**
     * 取消网络状态监听
     *
     * @param context ApplicationContext
     */
    public void unregister(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) return;

        if (mNetworkCallback != null) {
            connectivity.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    /**
     * 创建网络状态监听请求（配置监听网络类型等）
     *
     * @return NetworkRequest
     */
    @NonNull
    private NetworkRequest createNetworkRequest() {
        return new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    /**
     * 管理器内部注册的网络状态回调
     */
    private class InnerNetworkCallback extends ConnectivityManager.NetworkCallback {

        /**
         * 获取到可用网络
         *
         * @param network Network
         */
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.i(TAG, "获取到可用网络");
        }

        /**
         * 网络丢失（直接断开网络不会回调，应该是在网络链接的状态，触发网络丢失才会调用）
         *
         * @param network     Network
         * @param maxMsToLive maxMsToLive
         */
        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.i(TAG, "网络丢失...");
        }

        /**
         * 丢失当前正在使用网络
         *
         * @param network Network
         */
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            Log.i(TAG, "网络断开");
            updateNetState(NetState.NONE);
        }

        /**
         * 网络不可用
         */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Log.i(TAG, "网络不可用");
        }

        /**
         * 网络状态改变
         *
         * @param network             network
         * @param networkCapabilities NetworkCapabilities
         */
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(TAG, "网络类型为WIFI");
                    updateNetState(NetState.WIFI);
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(TAG, "网络类型为蜂窝网络");
                    updateNetState(NetState.CELLULAR);
                } else {
                    Log.i(TAG, "网络类型为其它网络");
                    updateNetState(NetState.OTHER);
                }
            }
        }

        @Override
        public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }

        @Override
        public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
            super.onBlockedStatusChanged(network, blocked);
        }

        private void updateNetState(NetState state) {
            // 状态有修改才会更新（更新到主线程）
            if (mNetState == null || mNetState != state) {
                mNetState = state;
                mNetStateLiveData.postValue(mNetState);
            }
        }
    }

}
