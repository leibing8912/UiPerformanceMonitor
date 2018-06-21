package cn.jianke.uiperformancedetect.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @className: LogMonitor
 * @classDescription: 日志监视器，用于监测ui性能
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class LogMonitor {
    // 日志标识
    private final static String TAG = "LogMonitor";
    // 检测ui性能间隔时间
    private static final long DETECT_PERFORMANCE_TIME = 53L;
    // sington
    private static LogMonitor instance;
    // 带looper的thread
    private HandlerThread mLogThread;
    // handler
    private Handler mLogHandler;
    // 是否监视中
    private boolean isMonitoring = false;
    // 堆栈哈希值列表
    private List<Integer> mStackHashList;
    // 堆栈列表
    private List<String> mStackTraceList;
    // log打印runnable
    private Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring) {
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
                for (StackTraceElement s : stackTrace) {
                    sb.append(s.toString() + "\n");
                }

                if (mStackHashList == null) {
                    mStackHashList = new CopyOnWriteArrayList();
                }
                if (mStackTraceList == null) {
                    mStackTraceList = new CopyOnWriteArrayList();
                }

                mStackHashList.add(sb.toString().hashCode());
                mStackTraceList.add(sb.toString());

                mLogHandler.postDelayed(mLogRunnable, DETECT_PERFORMANCE_TIME);
            }
        }
    };

    /**
     * Constructor
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    private LogMonitor() {
        mLogThread = new HandlerThread("looperLogs");
        mLogThread.start();
        mLogHandler = new Handler(mLogThread.getLooper());
    }

    /**
     * get sington
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    public static LogMonitor getInstance() {
        if (instance == null) {
            synchronized (LogMonitor.class) {
                instance = new LogMonitor();
            }
        }
        return instance;
    }

    /**
     * 是否监视中
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    public synchronized boolean isMonitor() {
        return isMonitoring;
    }

    /**
     * 开启监视器
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    public synchronized void startMonitor() {
        if (mLogHandler != null && mLogRunnable != null) {
            isMonitoring = true;
            mLogHandler.postDelayed(mLogRunnable, DETECT_PERFORMANCE_TIME);
        }
    }

    /**
     * 移除监视器
     *
     * @param
     * @return
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     */
    public synchronized void removeMonitor() {
        if (mLogHandler != null && mLogRunnable != null) {
            isMonitoring = false;
            mLogHandler.removeCallbacks(mLogRunnable);
        }
    }

    /**
     * 记录堆栈日志到本地
     */
    public synchronized void logStackTraceRecord() {
        if (mStackHashList == null || mStackTraceList == null) {
            return;
        }
        // 重复最多的堆栈索引
        int repeatAtMostIndex = 0;
        // 重复次数
        int repeatCount = 0;
        int size = mStackHashList.size();
        for (int i = 0; i < size; i++) {
            if (Collections.frequency(mStackHashList, mStackHashList.get(i)) > repeatCount) {
                repeatCount = Collections.frequency(mStackHashList, mStackHashList.get(i));
                repeatAtMostIndex = i;
            }
        }
        if (mStackTraceList.size() != 0 && repeatAtMostIndex < mStackTraceList.size()) {
            String repeatAtMostStackTrace = mStackTraceList.get(repeatAtMostIndex);
            Log.v(TAG, "#logStackTraceRecord repeatCount : " + repeatCount
                    + " repeatAtMostStackTrace : " + repeatAtMostStackTrace);
        }
        // 清空列表
        mStackTraceList.clear();
        mStackHashList.clear();
    }
}
