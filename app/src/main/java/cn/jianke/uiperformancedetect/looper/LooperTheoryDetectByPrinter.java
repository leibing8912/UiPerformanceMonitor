package cn.jianke.uiperformancedetect.looper;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import cn.jianke.uiperformancedetect.monitor.LogMonitor;

/**
 * @className: LooperTheoryDetectByPrinter
 * @classDescription: 通过Looper原理检测ui性能
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class LooperTheoryDetectByPrinter {
    // 日志标识
    private final static String TAG = "LooperTheoryDetect";
    // 消息队列属性
    private final static String FIELD_MQUEUE = "mQueue";
    // 下一个消息方法
    private final static String METHOD_NEXT = "next";

    /**
     * 开始监测ui线程
     * @author leibing
     * @createTime 2017/3/1
     * @lastModify 2017/3/1
     * @param
     * @return
     */
    public static void start(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取主线程looper
                    Looper mainLooper = Looper.getMainLooper();
                    // 将主线程looper赋值给自定义looper
                    final Looper me = mainLooper;
                    // 消息队列
                    final MessageQueue mQueue;
                    // 通过反射拿到消息队列属性
                    Field fieldQueue = me.getClass().getDeclaredField(FIELD_MQUEUE);
                    // 设置消息队列属性可访问
                    fieldQueue.setAccessible(true);
                    // 获取消息队列
                    mQueue = (MessageQueue) fieldQueue.get(me);
                    // 通过反射获取下一条消息方法
                    Method methodNext = mQueue.getClass().getDeclaredMethod(METHOD_NEXT);
                    // 设置下一条消息方法可访问
                    methodNext.setAccessible(true);
                    // 暂时获取系统权限
                    Binder.clearCallingIdentity();
                    // 遍历拿消息
                    for (;;){
                        Log.e(TAG ,"read msg from message queue");
                        // 消息队列取消息
                        Message msg = (Message) methodNext.invoke(mQueue);
                        if (msg == null)
                            return;
                        // 开启ui监视器
                        LogMonitor.getInstance().startMonitor();
                        // 消息分发
                        msg.getTarget().dispatchMessage(msg);
                        // 消息回收
                        msg.recycle();
                        // 移除ui监视器
                        LogMonitor.getInstance().removeMonitor();
                    }
                }catch (Exception ex){
                    Log.e(TAG ,ex.getMessage());
                }
            }
        });
    }
}
