package sn.nuc.com.braveman;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lenovo on 2019/6/29.
 */

public class MyClickListener implements View.OnTouchListener {
    private static int timeout=600;//三击间六百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private Handler handler;
    private MyClickCallBack myClickCallBack;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            clickCount++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickCount == 1) {
                        myClickCallBack.oneClick();
                    }else if(clickCount==2){
                        myClickCallBack.doubleClick();
                    }else if(clickCount==3){
                        myClickCallBack.thirdClick();
                    }
                    handler.removeCallbacksAndMessages(null);
                    //清空handler延时，并防内存泄漏
                    clickCount = 0;//计数清零
                }
            },timeout);//延时timeout后执行run方法中的代码
        }
        return false;//让点击事件继续传播，方便再给View添加其他事件监听
    }

    public interface MyClickCallBack{
        void oneClick();//点击一次的回调
        void doubleClick();//连续点击两次的回调
        void thirdClick();//连续点击三次的回调
    }
    public MyClickListener(MyClickCallBack myClickCallBack) {
        this.myClickCallBack = myClickCallBack;
        handler = new Handler();
    }

}