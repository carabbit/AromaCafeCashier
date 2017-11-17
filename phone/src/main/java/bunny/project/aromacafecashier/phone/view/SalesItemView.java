package bunny.project.aromacafecashier.phone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.phone.R;

/**
 * Created by bunny on 17-11-15.
 */

public class SalesItemView extends LinearLayout {
    private TextView mOrderIdView;
    private TextView mOrderTimeView;
    private TextView mPayTimeView;
    private ImageView mPayStatusView;
    private ImageView mOrderStatusView;

    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int mOrderStatus;
    private Context mContext;

    public SalesItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOrderIdView = (TextView) findViewById(R.id.order_id);
        mOrderTimeView = (TextView) findViewById(R.id.order_time);
        mPayTimeView = (TextView) findViewById(R.id.pay_time);
//        mPayStatusView = (ImageView) findViewById(R.id.pay_status);
//        mOrderStatusView = (ImageView) findViewById(R.id.order_status);
    }

    public void setOrderId(long id) {
        mOrderIdView.setText(String.valueOf(id));
    }

    public void setOrderTime(long time) {
        mOrderTimeView.setText(mDateFormat.format(new Date(time)));
    }

    public void setPayTime(long time) {
        mPayTimeView.setText(time > 0 ? mDateFormat.format(new Date(time)) : "-");
    }

    public void setOrderStatus(int flag) {
        mOrderStatus = flag;
        if (flag > 0) {
            setBackgroundColor(getContext().getColor(R.color.list_item_delete));
            setTextColor(R.color.white);
        } else {
            setBackgroundColor(0);
            setTextColor(android.R.color.black);
        }
    }

    public void setPayStatus(int flag) {
        // 作废订单不显示挂单颜色。
        if (mOrderStatus > 0) {
            return;
        }

        if (flag > 0) {
            setBackgroundColor(0);
            setTextColor(android.R.color.black);
        } else {
            setBackgroundColor(getContext().getColor(R.color.list_item_highlight));
            setTextColor(R.color.white);
        }
    }

    private void setTextColor(int color) {
        mOrderIdView.setTextColor(getContext().getColor(color));
        mOrderTimeView.setTextColor(getContext().getColor(color));
        mPayTimeView.setTextColor(getContext().getColor(color));
    }
}