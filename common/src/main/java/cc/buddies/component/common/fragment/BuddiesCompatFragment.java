package cc.buddies.component.common.fragment;

import android.app.Dialog;

import androidx.fragment.app.Fragment;

import cc.buddies.component.common.R;
import cc.buddies.component.common.dialog.CustomLoadingDialog;

public class BuddiesCompatFragment extends Fragment {

    protected Dialog mLoadingDialog;

    public BuddiesCompatFragment() {
    }

    public BuddiesCompatFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    // 显示加载中提示框
    protected void showLoadingDialog() {
        showLoadingDialog(getString(R.string.common_loading));
    }

    // 显示加载中提示框
    protected void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new CustomLoadingDialog(requireContext());
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

}
