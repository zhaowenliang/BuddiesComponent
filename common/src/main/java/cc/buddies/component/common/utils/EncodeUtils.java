package cc.buddies.component.common.utils;

import android.net.Uri;

public class EncodeUtils {

    public static String encodeUri(String str) {
        if (str == null) return "";
        return Uri.encode(str);
    }

}
