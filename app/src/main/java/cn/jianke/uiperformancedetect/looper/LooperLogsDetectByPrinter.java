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

    /**
     * 开始监测ui线程
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public static void start(){
        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String s) {
                if (s.startsWith(START)){
                    LogMonitor.getInstance().startMonitor();
                }
                if (s.startsWith(END)){
                    LogMonitor.getInstance().removeMonitor();
                }
            }
        });
    }
}
