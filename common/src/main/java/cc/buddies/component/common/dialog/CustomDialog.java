package cc.buddies.component.common.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * 弹窗AlertDialog
 * 继承自V7包下AlertDialog，重写
 * abc_alert_dialog_material.xml
 * abc_alert_dialog_title_material.xml
 * abc_alert_dialog_button_bar_material.xml
 * 三个文件， 自定义布局，不修改代码。
 * 后续如果需要修改弹窗，可修改此类，尽量保持原本AlertDialog的API。
 */
public class CustomDialog extends AlertDialog {

    protected CustomDialog(@NonNull Context context) {
        super(context);
    }

    protected CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

}
