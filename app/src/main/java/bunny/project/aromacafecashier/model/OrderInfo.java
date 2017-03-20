package bunny.project.aromacafecashier.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.QueryManager;
import bunny.project.aromacafecashier.provider.AccsTables;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderInfo {
    public static final int UNPAYED = 0;
    public static final int PAYED = 1;
    private int id;
    private int payed;
    private long date;
    private long pay_time;

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


    public long getPay_time() {
        return pay_time;
    }

    public void setPay_time(long pay_time) {
        this.pay_time = pay_time;
    }

    public static OrderInfo fromCusor(Cursor cursor) {
        OrderInfo order = new OrderInfo();
        order.setId(cursor.getInt(QueryManager.INDEX_ORDER_ID));
        order.setPayed(cursor.getInt(QueryManager.INDEX_ORDER_PAYED));
        order.setPay_time(cursor.getLong(QueryManager.INDEX_ORDER_PAY_TIME));
        order.setDate(cursor.getLong(QueryManager.INDEX_ORDER_DATE));
        MyLog.i("xxx", order.toString());
        return order;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AccsTables.Order.COL_PAYED, getPayed());
        values.put(AccsTables.Order.COL_DATE, getDate());
        values.put(AccsTables.Order.COL_PAY_TIME, getPay_time());
        MyLog.i("xxx", "[toContentValues] date:" + getDate() + " payTime:" + getPay_time());
        return values;
    }

    public ContentProviderOperation toInsertContentProviderOperation() {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QueryManager.URI_ORDER);
        builder.withValues(toContentValues());
        builder.withYieldAllowed(true);
        return builder.build();
    }

    public static ContentProviderOperation getNewInsertOperation(boolean payed) {
        OrderInfo order = new OrderInfo();
        long now = System.currentTimeMillis();
        order.setDate(now);
        if (payed) {
            order.setPay_time(now);
            order.setPayed(OrderInfo.PAYED);
        } else {
            order.setPay_time(-1);
            order.setPayed(OrderInfo.UNPAYED);
        }

        return order.toInsertContentProviderOperation();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[ id:" + getId() + " payed:" + getPayed() + " time:" + getDate() + " pay_time:" + getPay_time() + " ]";
    }
}