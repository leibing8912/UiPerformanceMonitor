package cn.jianke.uiperformancedetect.choreographer;

import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

import cn.jianke.uiperformancedetect.monitor.LogMonitor;

/**
 * @className: ChoreographerDetectByPrinter
 * @classDescription: 通过android系统每隔16ms发出VSYNC(帧同步)信号，触发对UI进行渲染回调方法，监视ui性能
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class ChoreographerDetectByPrinter {
    /**
     * 开始监测ui线程
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    public static void start() {
        // 要求minSdkVersion > 15
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            long lastFrameTimeNanos = 0;
            long curFrameTimeNanos = 0;

            @Override
            public void doFrame(long frameTimeNanos) {
                if (lastFrameTimeNanos == 0) {
                    lastFrameTimeNanos = frameTimeNanos;
                }
                curFrameTimeNanos = frameTimeNanos;
                long diffMs = TimeUnit.MILLISECONDS
                        .convert(curFrameTimeNanos - lastFrameTimeNanos,
                                TimeUnit.NANOSECONDS);
                long droppedCount = 0;
                if (diffMs > 16.6f) {
                    droppedCount = (int) (diffMs / 16.6);
                }
                if (droppedCount >= 5) {
                    // 视为卡顿，将堆栈记录到日志
                    LogMonitor.getInstance().logStackTraceRecord(diffMs);
                }
                if (LogMonitor.getInstance().isMonitor()) {
                    LogMonitor.getInstance().removeMonitor();
                }
                LogMonitor.getInstance().startMonitor();
                Choreographer.getInstance().postFrameCallback(this);
                lastFrameTimeNanos = curFrameTimeNanos;
            }
        });
    }
}
