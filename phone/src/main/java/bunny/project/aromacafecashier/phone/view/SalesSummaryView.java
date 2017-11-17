package bunny.project.aromacafecashier.phone.view;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.TextView;

import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.common.QueryManager;
import bunny.project.aromacafecashier.phone.R;

/**
 * Created by bunny on 17-11-16.
 */

public class SalesSummaryView extends LinearLayout {
    private TextView mSalesCashView;
    private TextView mFinishOrderView;
    private TextView mTempOrderView;
    private TextView mCanceledOrderView;


    public SalesSummaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSalesCashView = (TextView) findViewById(R.id.sales_cash);
        mFinishOrderView = (TextView) findViewById(R.id.finish_order_count);
        mTempOrderView = (TextView) findViewById(R.id.temp_order_count);
        mCanceledOrderView = (TextView) findViewById(R.id.canceled_order_count);

        mSalesCashView.setText(getContext().getString(R.string.sales_cash, 0f));
        mFinishOrderView.setText(getContext().getString(R.string.finish_order_count, 0));
        mTempOrderView.setText(getContext().getString(R.string.temp_order_count, 0));
        mCanceledOrderView.setText(getContext().getString(R.string.canceled_order_count, 0));
    }

    public void setSales(Cursor cursor) {
        if (getContext() == null) {
            return;
        }
        float totalCash = countTotalCash(cursor);
        mSalesCashView.setText(getContext().getString(R.string.sales_cash, totalCash));
    }

    private float countTotalCash(Cursor cursor) {
        if (cursor == null) {
            return 0f;
        }

        SparseArray<Float> cashPerOrderMap = new SparseArray<Float>();
        SparseArray<Float> discountPerOrderMap = new SparseArray<Float>();

        cursor.moveToPosition(-1);

        float cash = 0f;
        while (cursor.moveToNext()) {
            int orderId = cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_ORDER_ID);

            Float cashPerOrder = cashPerOrderMap.get(orderId);
            if (cashPerOrder == null) {
                cashPerOrder = 0f;
            }

            int count = cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_COUNT);
            float price = cursor.getFloat(QueryManager.INDEX_ORDER_DETAIL_PRODUCT_PRICE);


            cashPerOrderMap.put(orderId, cashPerOrder + count * price);


            float discount = cursor.getFloat(QueryManager.INDEX_ORDER_DETAIL_DISCOUNT);
            discountPerOrderMap.put(orderId, discount);
        }

        int orderCount = cashPerOrderMap.size();
        for (int i = 0; i < orderCount; i++) {
            float cashPerOrder = cashPerOrderMap.valueAt(i);
            int orderId = cashPerOrderMap.keyAt(i);
            float discountPerOrder = discountPerOrderMap.get(orderId);
            cash += Math.round(cashPerOrder * discountPerOrder);
        }

        return cash;
    }

    public void setOrderInfo(Cursor cursor) {
        if (getContext() == null) {
            return;
        }

        int finishOrderCount = 0;
        int tmpOrderCount = 0;
        int canceledOrderCount = 0;
        if (cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                int payed = cursor.getInt(QueryManager.INDEX_ORDER_PAYED);
                int status = cursor.getInt(QueryManager.INDEX_ORDER_STATUS);
                if (payed == 1) {
                    finishOrderCount++;
                } else {
                    tmpOrderCount++;
                }

                if (status == 1) {
                    canceledOrderCount++;
                }
            }
        }

        mFinishOrderView.setText(getContext().getString(R.string.finish_order_count, finishOrderCount));
        mTempOrderView.setText(getContext().getString(R.string.temp_order_count, tmpOrderCount));
        mCanceledOrderView.setText(getContext().getString(R.string.canceled_order_count, canceledOrderCount));
    }


}
