package cc.buddies.component.common.utils;

public class ParseUtils {

    public static int parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ignore) {
            return 0;
        }
    }

    public static long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (Exception ignore) {
            return 0L;
        }
    }

}
