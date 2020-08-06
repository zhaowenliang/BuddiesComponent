package cc.buddies.component.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cc.buddies.component.common.R;
import cc.buddies.component.common.utils.ContextUtils;

/**
 * 加载中Dialog
 */
public class CustomLoadingDialog extends Dialog {

    protected TextView messageTv;

    public CustomLoadingDialog(@NonNull Context context) {
        super(context, R.style.CustomLoadingDialog);
        initView();
    }

    protected void initView() {
        setContentView(R.layout.default_dialog_progress);

        messageTv = findViewById(R.id.text);
        messageTv.setVisibility(View.GONE);
    }

    /**
     * 重写setTitle，使其成为Loading提示文案。
     * @param title 提示文案
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        // super.setTitle(title);
        if (messageTv != null) {
            if (TextUtils.isEmpty(title)) {
                messageTv.setVisibility(View.GONE);
            } else {
                messageTv.setText(title);
                messageTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void show() {
        if (isContextExisted()) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 判断上下文是否还存在
     * @return boolean
     */
    private boolean isContextExisted() {
        return ContextUtils.isContextExist(getContext());
    }

}
