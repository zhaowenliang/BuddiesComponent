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
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * 网络状态变化监听管理器
 * <pre>
 * 1. 初始使用需要在Application中初始化：
 *     NetworkStateManager.getInstance().register(context);
 * 2. 观察网络状态数据：
 *     NetworkStateManager.getInstance().getNetStateLiveData().observe(this, state -> Log.w("网络状态监听器", "NetState: " + state));
 * 3. 观察网络是否可用：
 *     NetworkStateManager.getInstance().getNetAvailableLiveData().observe(this, available -> Log.w("网络可用监听器", "NetAvailable: " + available));
 * </pre>
 */
public class NetworkStateManager {

    private static final String TAG = "NetworkStateManager";

    private ConnectivityManager mConnectivityManager;

    /**
     * 网络状态数据
     */
    @NonNull
    private final MutableLiveData<NetState> mNetStateLiveData = new MutableLiveData<>();

    /**
     * 网络是否可用
     */
    @NonNull
    private final MutableLiveData<Boolean> mNetAvailableLiveData = new MutableLiveData<>();

    /**
     * 网络状态回调
     */
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
        NONE,
        CELLULAR,
        WIFI,
        OTHER,
        ;

        public boolean hasNetwork() {
            return compareTo(NONE) > 0 && compareTo(OTHER) <= 0;
        }
    }

    @NonNull
    public LiveData<NetState> getNetStateLiveData() {
        return mNetStateLiveData;
    }

    public LiveData<Boolean> getNetAvailableLiveData() {
        return mNetAvailableLiveData;
    }

    /**
     * 注册网络状态监听
     *
     * @param context ApplicationContext
     */
    public void register(@NonNull Context context) {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (mConnectivityManager == null) {
            return;
        }

        if (mNetworkCallback == null) {
            mNetworkCallback = new InnerNetworkCallback();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        } else {
            mConnectivityManager.registerNetworkCallback(createNetworkRequest(), mNetworkCallback);
        }
    }

    /**
     * 取消网络状态监听
     */
    public void unregister() {
        if (mConnectivityManager != null && mNetworkCallback != null) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
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

        /**
         * 更新网络状态
         *
         * @param state 网络状态
         */
        @WorkerThread
        private void updateNetState(NetState state) {
            final NetState oldNetState = mNetStateLiveData.getValue();
            final boolean oldNetAvailable = oldNetState != null && oldNetState.hasNetwork();
            final boolean newNetAvailable = state != null && state.hasNetwork();

            // 状态有修改才会更新（更新到主线程）
            if (oldNetState != state) {
                mNetStateLiveData.postValue(state);
            }

            // “网络可用状态变化”需要同时判断前后状态是否相同
            if (newNetAvailable != oldNetAvailable) {
                mNetAvailableLiveData.postValue(newNetAvailable);
            }
        }
    }
}
