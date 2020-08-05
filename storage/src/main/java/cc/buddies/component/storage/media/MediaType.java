package cc.buddies.component.storage.media;

public final class MediaType {

    // prefix 前缀
    // suffix 后缀

    // 关于文件后缀的常量
    public static final class FileSuffix {

        public static final String AMR = ".amr";
        public static final String AAC = ".aac";
        public static final String MP3 = ".mp3";

        public static final String MP4 = ".mp4";
        public static final String M4V = ".m4v";
        public static final String WMV = ".wmv";
        public static final String AVI = ".avi";
        public static final String _3GP = ".3gp";

        public static final String JPEG = ".jpeg";
        public static final String JPG = ".jpg";
        public static final String PNG = ".png";
        public static final String GIF = ".gif";

    }

    // 关于MIME的常量
    public static final class MimeType {

        public static final String IMAGE_WEBP = "image/webp";
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_GIF = "image/gif";
        public static final String IMAGE_SVG = "image/svg+xml";

        public static final String AUDIO_MP3 = "audio/mpeg";
        public static final String AUDIO_AAC = "audio/aac";
        public static final String AUDIO_OGG = "audio/ogg";
        public static final String AUDIO_WAV = "audio/wav";
        public static final String AUDIO_3GP = "audio/3gpp";
        public static final String AUDIO_3G2 = "audio/3gpp2";
        public static final String AUDIO_WEBA = "audio/webm";

        public static final String VIDEO_MPEG = "video/mpeg";
        public static final String VIDEO_WEBM = "video/webm";
        public static final String VIDEO_MP4 = "video/mp4";
        public static final String VIDEO_M4V = "video/mp4";
        public static final String VIDEO_OGV = "video/ogg";
        public static final String VIDEO_3GP = "video/3gpp";
        public static final String VIDEO_3G2 = "video/3gpp2";

        public static final String IMAGE_ALL = "image/*";
        public static final String AUDIO_ALL = "audio/*";
        public static final String VIDEO_ALL = "video/*";

    }

}
