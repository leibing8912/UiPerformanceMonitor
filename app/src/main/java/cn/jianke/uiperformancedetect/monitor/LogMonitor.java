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
    // 单例实例
    private static LogMonitor instance;
    // 日志采集带looper线程
    private HandlerThread mCollectLogsThread;
    // 日志存储带looper线程
    private HandlerThread mStoreLogsThread;
    // 日志采集handler
    private Handler mCollectLogsHandler;
    // 日志存储handler
    private Handler mStoreLogsHandler;
    // 是否监视中
    private boolean isMonitoring = false;
    // 堆栈哈希值列表
    private List<Integer> mStackHashList;
    // 堆栈列表
    private List<String> mStackTraceList;
    // 卡顿耗时
    private long catonDiffMs = 0;
    // 日志采集Runnable
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

                mCollectLogsHandler.postDelayed(mLogRunnable, DETECT_PERFORMANCE_TIME);
            }
        }
    };
    // 日志存储Runnable
    private Runnable mStoreLogsRunnable = new Runnable() {
        @Override
        public void run() {
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
                Log.v(TAG, "#logStackTraceRecord " + "\n"
                        + "repeatCount : " + repeatCount + " time" + "\n"
                        + "cost : " + catonDiffMs + " ms" + "\n"
                        + "repeatAtMostStackTrace : " + repeatAtMostStackTrace);
            }
            // 清空列表
            mStackTraceList.clear();
            mStackHashList.clear();
        }
    };

    /**
     * 实例化
     */
    private LogMonitor() {
        mCollectLogsThread = new HandlerThread("LogsCollect");
        mStoreLogsThread = new HandlerThread("LogsStore");
        mCollectLogsThread.start();
        mStoreLogsThread.start();
        mCollectLogsHandler = new Handler(mCollectLogsThread.getLooper());
        mStoreLogsHandler = new Handler(mStoreLogsThread.getLooper());
    }

    /**
     * 获取单例实例
     *
     * @return
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
     * @return
     */
    public boolean isMonitor() {
        return isMonitoring;
    }

    /**
     * 开启监视器
     */
    public void startMonitor() {
        if (mCollectLogsHandler != null && mLogRunnable != null) {
            isMonitoring = true;
            mCollectLogsHandler.postDelayed(mLogRunnable, DETECT_PERFORMANCE_TIME);
        }
    }

    /**
     * 移除监视器
     */
    public void removeMonitor() {
        if (mCollectLogsHandler != null && mLogRunnable != null) {
            isMonitoring = false;
            mCollectLogsHandler.removeCallbacks(mLogRunnable);
        }
    }

    /**
     * 记录堆栈日志到本地
     */
    public void logStackTraceRecord(long diffMs) {
        if (mStoreLogsHandler != null && mStoreLogsRunnable != null) {
            catonDiffMs = diffMs;
            mStoreLogsHandler.post(mStoreLogsRunnable);
        }
    }
}
