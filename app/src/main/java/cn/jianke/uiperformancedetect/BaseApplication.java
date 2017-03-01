package cn.jianke.uiperformancedetect;

import android.app.Application;
import cn.jianke.uiperformancedetect.looper.LooperLogsDetectByPrinter;
import cn.jianke.uiperformancedetect.looper.LooperTheoryDetectByPrinter;

/**
 * @className: BaseApplication
 * @classDescription: 应用实例
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // 通过looper日志打印监视ui性能
//        LooperLogsDetectByPrinter.start();
        // 通过Looper原理检测ui性能
        LooperTheoryDetectByPrinter.start();
        // 通过android系统每隔16ms发出VSYNC(帧同步)信号，触发对UI进行渲染回调方法，监视ui性能
//        ChoreographerDetectByPrinter.start();
    }
}
