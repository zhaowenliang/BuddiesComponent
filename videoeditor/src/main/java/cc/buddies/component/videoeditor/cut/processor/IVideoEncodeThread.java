package cc.buddies.component.videoeditor.cut.processor;

import android.view.Surface;

import java.util.concurrent.CountDownLatch;

public interface IVideoEncodeThread {
    Surface getSurface();

    CountDownLatch getEglContextLatch();
}
