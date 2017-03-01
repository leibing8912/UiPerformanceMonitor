package cn.jianke.uiperformancedetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @className: MainActivity
 * @classDescription: 测试ui性能页面
 * @author: leibing
 * @createTime: 2017/3/1
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // onClick
        findViewById(R.id.btn_looper_detect).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_looper_detect:
                // 通过looper日志检测ui性能
                // 模拟制造一个阻塞ui操作，在主线程中试图休眠2秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
