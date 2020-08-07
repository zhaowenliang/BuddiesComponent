package cc.buddies.component.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cc.buddies.component.common.R;
import cc.buddies.component.common.utils.ContextUtils;
import cc.buddies.component.common.view.RoundProgressBar;

/**
 * 带进度Dialog
 */
public class CustomProgressDialog extends Dialog {

    private TextView messageTv;
    private RoundProgressBar roundProgressBar;

    public CustomProgressDialog(@NonNull Context context) {
        this(context, R.style.CustomLoadingDialog);
    }

    public CustomProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected void initView() {
        setContentView(R.layout.default_dialog_progress);

        messageTv = findViewById(R.id.text);
        messageTv.setVisibility(View.GONE);

        roundProgressBar = findViewById(R.id.round_progress_bar);
    }

    public void setMessage(String text) {
        if (messageTv != null) {
            messageTv.setText(text);
            messageTv.setVisibility(View.VISIBLE);
        }
    }

    public void setProgressMax(int max) {
        if (roundProgressBar != null) {
            roundProgressBar.setMax(max);
        }
    }

    public void setProgress(int progress) {
        if (roundProgressBar != null) {
            roundProgressBar.setProgress(progress);
        }
    }

    public void setProgressTextDisplayable(boolean isDisplayable) {
        if (roundProgressBar != null) {
            roundProgressBar.setTextIsDisplayable(isDisplayable);
        }
    }

    @Override
    public void show() {
        if (isContextExisted()) {
            super.show();
        }
    }

    /**
     * 判断上下文是否还存在
     *
     * @return boolean
     */
    protected boolean isContextExisted() {
        return ContextUtils.isContextExist(getContext());
    }
}
