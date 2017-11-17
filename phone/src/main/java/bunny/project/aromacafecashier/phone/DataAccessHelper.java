package bunny.project.aromacafecashier.phone;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import java.util.Calendar;

import bunny.project.aromacafecashier.common.AccsUris;
import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.common.QueryManager;
import bunny.project.aromacafecashier.common.model.OrderInfo;
import bunny.project.aromacafecashier.common.provider.AccsTables;

/**
 * Created by bunny on 17-11-15.
 */

public class DataAccessHelper {
    private ContentResolver mResolver;
    private AccessCallback mCallback;
    private AsyncQueryHandler mQueryHandler;
    public static final int TOKEN_QUERY_TODAY_PRODUCTS = 0;
    public static final int TOKEN_QUERY_TODAY_ORDERS = 1;

    public DataAccessHelper(ContentResolver resolver, AccessCallback callback) {
        mCallback = callback;
        mQueryHandler = new AccsQueryHandler(resolver);
    }

    public Cursor getAllOrders() {
        return mResolver.query(AccsUris.URI_ORDER, QueryManager.PROJECTION_ORDER, null, null, null);
    }

    public void getTodaySalesSummary() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayZeroTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long nextDayZeroTime = cal.getTimeInMillis();

        queryOrdersByTime(todayZeroTime, nextDayZeroTime);
        queryProductsByTime(todayZeroTime, nextDayZeroTime);
    }

    public void getMonthSalesSummary() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startTime = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        long endTime = cal.getTimeInMillis();

        queryOrdersByTime(startTime, endTime);
        queryProductsByTime(startTime, endTime);
    }

    private void queryOrdersByTime(long startTime, long endTime) {
        String selection = AccsTables.Order.COL_DATE + " BETWEEN ? AND ?  ";
        String[] args = new String[]{String.valueOf(startTime), String.valueOf(endTime)};
        String orderBy = AccsTables.Order.COL_STATUS + " ASC , " + AccsTables.Order.COL_PAYED + " ASC , " + AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUERY_TODAY_ORDERS, null, AccsUris.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }

    private void queryProductsByTime(long startTime, long endTime) {
        String selection = AccsTables.OrderDetail.COL_ORDER_ID + " IN ("
                + " SELECT " + AccsTables.Order._ID + " FROM " + AccsTables.Order.TABLE_NAME + " WHERE " + AccsTables.Order.COL_DATE
                + " BETWEEN ? AND ? "
                + " AND " + AccsTables.Order.COL_PAYED + " = 1"
                + " AND " + AccsTables.Order.COL_STATUS + " = 0"
                + ")";
        String[] args = new String[]{String.valueOf(startTime), String.valueOf(endTime)};
        mQueryHandler.startQuery(TOKEN_QUERY_TODAY_PRODUCTS, null, AccsUris.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
    }

    private class AccsQueryHandler extends AsyncQueryHandler {

        public AccsQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            mCallback.onQueryComplete(token, cursor);
        }
    }

    public interface AccessCallback {
        void onQueryComplete(int token, Cursor cursor);
    }
}
