package cn.jianke.uiperformancedetect.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

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
    private static final long DETECT_PERFORMANCE_TIME = 1000L;
    // sington
    private static LogMonitor instance;
    // 带looper的thread
    private HandlerThread mLogThread;
    // handler
    private Handler mLogHandler;
    // log打印runnable
    private static Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s: stackTrace){
                sb.append(s.toString() + "\n");
            }
            Log.e(TAG, sb.toString());
        }
    };

    /**
     * Constructor
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    private LogMonitor(){
        mLogThread = new HandlerThread("looperLogs");
        mLogThread.start();
        mLogHandler = new Handler(mLogThread.getLooper());
    }

    /**
     * get sington
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public static LogMonitor getInstance(){
        if (instance == null){
            synchronized (LogMonitor.class){
                instance = new LogMonitor();
            }
        }
        return instance;
    }

    /**
     * 开启监视器
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public void startMonitor(){
        removeMonitor();
        if (mLogHandler != null && mLogRunnable != null)
            mLogHandler.postDelayed(mLogRunnable, DETECT_PERFORMANCE_TIME);
    }

    /**
     * 移除监视器
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public void removeMonitor(){
        if (mLogHandler != null && mLogRunnable != null)
            mLogHandler.removeCallbacks(mLogRunnable);
    }
}
