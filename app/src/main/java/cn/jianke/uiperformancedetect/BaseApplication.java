package cn.jianke.uiperformancedetect;

import android.app.Application;
import cn.jianke.uiperformancedetect.looper.LooperDetectByPrinter;

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
        LooperDetectByPrinter.start();
    }
}
