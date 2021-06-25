package cc.buddies.component.common.utils;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EncodeUtils {

    @NonNull
    public static String encodeUri(@Nullable String str) {
        if (str == null) return "";

        String encode = Uri.encode(str);
        return encode == null ? "" : encode;
    }

}
