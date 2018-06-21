package cn.jianke.uiperformancedetect.looper;

import android.os.Looper;
import android.util.Printer;

import cn.jianke.uiperformancedetect.monitor.LogMonitor;

/**
 * @className: LooperLogsDetectByPrinter
 * @classDescription: 通过looper日志打印监视ui性能
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class LooperLogsDetectByPrinter {
    // before looper dispatch msg
    private final static String START = ">>>>> Dispatching to";
    // after looper finished msg
    private final static String END = "<<<<< Finished to";
    // 主线程处理任务耗时80ms以上就当卡顿
    private final static long DEFAULT_DIFF_MS = 80L;

    /**
     * 开始监测ui线程
     */
    public static void start() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            // 起始时间
            private long startTimeMills = 0;

            @Override
            public void println(String s) {
                if (s.startsWith(START)) {
                    if (LogMonitor.getInstance().isMonitor()) {
                        LogMonitor.getInstance().removeMonitor();
                    }
                    LogMonitor.getInstance().startMonitor();
                    startTimeMills = System.currentTimeMillis();
                }
                if (s.startsWith(END)) {
                    long diffMs = System.currentTimeMillis() - startTimeMills;
                    if (diffMs >= DEFAULT_DIFF_MS) {
                        LogMonitor.getInstance().logStackTraceRecord(diffMs);
                    }
                    LogMonitor.getInstance().removeMonitor();
                }
            }
        });
    }
}
