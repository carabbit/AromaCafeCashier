package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import bunny.project.aromacafecashier.R;

/**
 * Created by bunny on 17-3-16.
 */

public class HistoryOrderItemView extends LinearLayout {
    private TextView mViewOrderId;
    private TextView mViewOrderTime;
    private TextView mViewOrderPayStatus;
    private TextView mViewOrderPayTime;

    public HistoryOrderItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mViewOrderId = (TextView) findViewById(R.id.order_id);
        mViewOrderTime = (TextView) findViewById(R.id.order_time);
        mViewOrderPayStatus = (TextView) findViewById(R.id.pay_status);
        mViewOrderPayTime = (TextView) findViewById(R.id.pay_time);
    }

    public TextView getOrderIdView() {
        return mViewOrderId;
    }

    public TextView getViewOrderTime() {
        return mViewOrderTime;
    }

    public TextView getViewOrderPayStatus() {
        return mViewOrderPayStatus;
    }

    public TextView getViewOrderPayTime() {
        return mViewOrderPayTime;
    }
}