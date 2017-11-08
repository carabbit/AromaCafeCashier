package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import bunny.project.aromacafecashier.MyLog;

/**
 * Created by bunny on 2017/3/18.
 */

public class MyButton extends Button {

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        MyLog.i("xxx", "left" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
    }
}
