package bunny.project.aromacafecashier.common.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import bunny.project.aromacafecashier.common.QueryManager;
import bunny.project.aromacafecashier.common.AccsUris;
import bunny.project.aromacafecashier.common.provider.AccsTables;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderInfo {
    public static final int UNPAYED = 0;
    public static final int PAYED = 1;
    public static final int STATUS_NOMAL = 0;
    public static final int SATATUS_DELETE = 1;
    private int id;
    private int payed;
    private long date;
    private long pay_time;
    private int orderStatus = 0;
    private float discount = 1.0f;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    public long getPayTime() {
        return pay_time;
    }

    public void setPay_time(long pay_time) {
        this.pay_time = pay_time;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static OrderInfo fromCusor(Cursor cursor) {
        OrderInfo order = new OrderInfo();
        order.setId(cursor.getInt(QueryManager.INDEX_ORDER_ID));
        order.setPayed(cursor.getInt(QueryManager.INDEX_ORDER_PAYED));
        order.setPay_time(cursor.getLong(QueryManager.INDEX_ORDER_PAY_TIME));
        order.setDate(cursor.getLong(QueryManager.INDEX_ORDER_DATE));
        order.setOrderStatus(cursor.getInt(QueryManager.INDEX_ORDER_STATUS));
        order.setDiscount(cursor.getFloat(QueryManager.INDEX_ORDER_DISCOUNT));
//        MyLog.i("xxx", order.toString());
        return order;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AccsTables.Order.COL_PAYED, getPayed());
        values.put(AccsTables.Order.COL_DATE, getDate());
        values.put(AccsTables.Order.COL_PAY_TIME, getPayTime());
        values.put(AccsTables.Order.COL_STATUS, getOrderStatus());
        values.put(AccsTables.Order.COL_DISCOUNT, getDiscount());
//        MyLog.i("xxx", "[toContentValues] date:" + getDate() + " payTime:" + getPayTime());
        return values;
    }

    public ContentProviderOperation toInsertContentProviderOperation() {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(AccsUris.URI_ORDER);
        builder.withValues(toContentValues());
        builder.withYieldAllowed(true);
        return builder.build();
    }

    public static ContentProviderOperation getNewInsertOperation(boolean payed, float discount) {
        OrderInfo order = new OrderInfo();
        long now = System.currentTimeMillis();
        order.setDate(now);
        if (payed) {
            order.setPay_time(now);
            order.setPayed(OrderInfo.PAYED);
            order.setDiscount(discount);
        } else {
            order.setPay_time(-1);
            order.setPayed(OrderInfo.UNPAYED);
            order.setDiscount(1.0f);
        }

        return order.toInsertContentProviderOperation();
    }

    @Override
    public String toString() {
        return "[ id:" + getId() + " payed:" + getPayed() + " time:" + getDate() + " pay_time:" + getPayTime() + " status:" + getOrderStatus() + " ]";
    }


}
