package cc.buddies.component.common.activity;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.buddies.component.common.R;
import cc.buddies.component.common.dialog.CustomLoadingDialog;
import cc.buddies.component.common.handler.WeakHandler;
import cc.buddies.component.common.handler.WeakHandlerCallback;

public abstract class BuddiesCompatActivity extends AppCompatActivity implements WeakHandlerCallback {

    // 实际实现为WeakHandler的弱引用Handler
    protected Handler mHandler;

    // 布局中自定义的Toolbar
    protected Toolbar mToolbar;

    // 加载中提示框
    protected Dialog mLoadingDialog;

    public BuddiesCompatActivity() {
    }

    public BuddiesCompatActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    // 公共页面布局
    @LayoutRes
    protected int getCommonLayout() {
        return R.layout.buddies_activity_compat;
    }

    // 公共标题布局
    @LayoutRes
    protected int getTitleBarLayout() {
        return 0;
    }

    // 公共布局中标题ViewStub
    @IdRes
    protected int getTitleBarViewStub() {
        return R.id.stub_title_bar;
    }

    // 公共布局中内容布局容器
    @IdRes
    protected int getLayoutContent() {
        return R.id.layout_content;
    }

    // 是否使用公共布局
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean useCommonLayout() {
        return true;
    }

    // 是否显示返回按钮
    protected boolean hasBackIcon() {
        return true;
    }

    protected boolean hasTitleElevation() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (!useCommonLayout()) {
            super.setContentView(layoutResID);
            return;
        }

        // 如果使用通用标题，则将通用带ToolBar的布局放入，再将自定义页面布局放入通用布局中。
        super.setContentView(getCommonLayout());
        loadTitleBar();

        if (layoutResID != 0) {
            ViewGroup layoutContent = findViewById(getLayoutContent());
            View.inflate(this, layoutResID, layoutContent);
        }
    }

    @Override
    public void setContentView(View view) {
        if (!useCommonLayout()) {
            super.setContentView(view);
            return;
        }

        super.setContentView(getCommonLayout());
        loadTitleBar();

        if (view != null) {
            ViewGroup layoutContent = findViewById(getLayoutContent());
            layoutContent.addView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (!useCommonLayout()) {
            super.setContentView(view, params);
            return;
        }

        super.setContentView(getCommonLayout());
        loadTitleBar();

        if (view != null) {
            ViewGroup layoutContent = findViewById(getLayoutContent());
            layoutContent.addView(view, params);
        }
    }

    // 动态加载标题栏布局。
    protected void loadTitleBar() {
        final ViewStub viewStub = findViewById(getTitleBarViewStub());
        if (viewStub != null) {
            viewStub.setLayoutResource(getTitleBarLayout());
            initTitleBarStub(viewStub);
        }
    }

    // 初始化基础布局标题栏
    protected void initTitleBarStub(ViewStub stub) {
        final View inflate = stub.inflate();
        if (inflate instanceof Toolbar) {
            initTitleBar((Toolbar) inflate);
        }
    }

    protected void initTitleBar(@IdRes int titleBarRes) {
        Toolbar toolbar = findViewById(titleBarRes);
        initTitleBar(toolbar);
    }

    protected void initTitleBar(Toolbar toolbar) {
        this.mToolbar = toolbar;
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        // 显示返回按钮
        final boolean hasBack = hasBackIcon();
        getSupportActionBar().setDisplayHomeAsUpEnabled(hasBack);
        getSupportActionBar().setHomeButtonEnabled(hasBack);
    }

    // 显示加载中提示框
    protected void showLoadingDialog() {
        showLoadingDialog(getString(R.string.common_loading));
    }

    // 显示加载中提示框
    protected void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new CustomLoadingDialog(this);
        }
        mLoadingDialog.setTitle(msg);
        mLoadingDialog.show();
    }

    // 隐藏加载中提示框
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 添加页面状态视图
     * <br/>如果未使用公共布局，也需要实现getLayoutContent()方法，用来获取页面布局容器。
     *
     * @param view 页面状态视图
     */
    protected void showStateView(View view) {
        final ViewGroup contentLayout = findViewById(getLayoutContent());
        if (contentLayout != null) {
            contentLayout.addView(view);
        }
    }

    /**
     * 移除页面状态视图
     *
     * @param view 页面状态视图
     */
    protected void removeStateView(View view) {
        final ViewGroup contentLayout = findViewById(getLayoutContent());
        if (contentLayout != null) {
            contentLayout.removeView(view);
        }
    }

    /**
     * 获取一个Handler，懒加载
     */
    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new WeakHandler<>(this);
        }
        return mHandler;
    }

    @Override
    public void dealMessage(Message message) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean isSupportNavigateUp = super.onSupportNavigateUp();
        if (!isSupportNavigateUp) {
            onBackPressed();
        }
        return isSupportNavigateUp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
