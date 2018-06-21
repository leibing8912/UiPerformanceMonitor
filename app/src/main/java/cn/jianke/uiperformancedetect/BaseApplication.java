package cn.jianke.uiperformancedetect;

import android.app.Application;
import android.os.Build;

import cn.jianke.uiperformancedetect.choreographer.ChoreographerDetectByPrinter;
import cn.jianke.uiperformancedetect.looper.LooperLogsDetectByPrinter;

/**
 * @className: BaseApplication
 * @classDescription: 应用实例
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过android系统每隔16ms发出VSYNC(帧同步)信号，触发对UI进行渲染回调方法，监视ui性能
            ChoreographerDetectByPrinter.start();
        } else {
            // 通过looper日志打印监视ui性能
            LooperLogsDetectByPrinter.start();
        }
    }
}
