package cn.jianke.uiperformancedetect.choreographer;

import android.view.Choreographer;
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
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public static void start(){
        // 要求minSdkVersion > 15
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long l) {
                if (LogMonitor.getInstance().isMonitor()){
                    LogMonitor.getInstance().removeMonitor();
                }
                LogMonitor.getInstance().startMonitor();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
