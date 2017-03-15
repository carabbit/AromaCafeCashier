package bunny.project.aromacafecashier.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import bunny.project.aromacafecashier.QueryManager;
import bunny.project.aromacafecashier.provider.AccsTables;

/**
 * Created by bunny on 17-3-15.
 */

public class Order {
    public static final int UNPAYED = 0;
    public static final int PAYED = 1;

    private int payed;
    private long date;

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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AccsTables.Order.COL_PAYED, getPayed());
        values.put(AccsTables.Order.COL_DATE, getDate());

        return values;
    }

    public ContentProviderOperation toInsertContentProviderOperation() {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QueryManager.URI_ORDER);
        builder.withValues(toContentValues());
        builder.withYieldAllowed(true);
        return builder.build();
    }

    public static ContentProviderOperation getNewInsertOperation() {
        Order order = new Order();
        order.setDate(System.currentTimeMillis());
        order.setPayed(Order.PAYED);
        return order.toInsertContentProviderOperation();
    }
}
