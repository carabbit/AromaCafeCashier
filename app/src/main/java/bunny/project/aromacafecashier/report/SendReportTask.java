package bunny.project.aromacafecashier.report;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.QueryManager;
import bunny.project.aromacafecashier.R;
import bunny.project.aromacafecashier.backup.BackupDatabase;
import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.model.OrderItemInfo;
import bunny.project.aromacafecashier.provider.AccsTables;

/**
 * Created by bunny on 17-3-30.
 */
public class SendReportTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = SendReportTask.class.getSimpleName();
    private ContentResolver mResolver;
    private Context mContext;
    private OnSendFinishListener mListener;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat mContentDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
    private File mDbFile;

    public static interface OnSendFinishListener {
        void onSendFinish(boolean success);
    }

    public SendReportTask(Context context, OnSendFinishListener listener) {
        mResolver = context.getContentResolver();
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        MyLog.i(TAG, "[doInBackground]");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayZeroTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long nextDayZeroTime = cal.getTimeInMillis();


        Cursor todayOrderCursor = queryTodayOrders(todayZeroTime, nextDayZeroTime);
        Cursor todayProductCursor = queryTodayProducts(todayZeroTime, nextDayZeroTime);

        String content = createReport(todayOrderCursor, todayProductCursor);
        String formatDate = mDateFormat.format(new Date(System.currentTimeMillis()));
        String title = mContext.getResources().getString(R.string.today_repot, formatDate);


        mDbFile = BackupDatabase.pullDatabase();

        return MailHelper.sendTodaySheet(title, content, mDbFile);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mListener != null) {
            mListener.onSendFinish(result);
        }

        if (mDbFile!=null){
            boolean ret = mDbFile.delete();
            MyLog.i("", "delete tmp db file result: " + ret);
        }
    }

    @NonNull
    private String createReport(Cursor todayOrderCursor, Cursor todayProductCursor) {
        SparseArray<ArrayList<OrderItemInfo>> orderItemInfos = getOrderItemInfos(todayProductCursor);
        SparseArray<OrderInfo> orderInfos = getOrderInfos(todayOrderCursor);

//        MyLog.i("xxx", "mOrderItemInfos.size()-->" + orderItemInfos.size());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("今日订单：<br>");

        if (todayOrderCursor != null && todayOrderCursor.getCount() > 0) {
            stringBuilder.append("<table border=\"1px\"  cellpadding=\"0\" cellspacing=\"0\">");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>" + mContext.getString(R.string.order_id) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.confirm_order_time) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.pay_status) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.pay_time) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.order_status) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.order_detail) + "</td>");
            stringBuilder.append("<td>" + mContext.getString(R.string.order_discount) + "</td>");
            stringBuilder.append("</tr>");

            todayOrderCursor.moveToPosition(-1);

            while (todayOrderCursor.moveToNext()) {
                OrderInfo info = OrderInfo.fromCusor(todayOrderCursor);
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>" + info.getId() + "</td>");
                stringBuilder.append("<td>" + getFormatDate(info.getDate()) + "</td>");
                stringBuilder.append("<td>" + getPayStatus(info.getPayed()) + "</td>");
                stringBuilder.append("<td>" + getPayTime(info.getPayTime()) + "</td>");
                stringBuilder.append("<td>" + getOrderStatus(info.getOrderStatus()) + "</td>");
                stringBuilder.append("<td>" + getOrderDetail(orderItemInfos.get(info.getId())) + "</td>");
                stringBuilder.append("<td>" + info.getDiscount() + "</td>");
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("</table>");
        }

        SumReport sumReport = createTodaySumReport(orderInfos, orderItemInfos);

        stringBuilder.append("<br><br>今日营业额：");
        stringBuilder.append(sumReport.totalCash);
        stringBuilder.append("&nbsp;&nbsp;完成订单：");
        stringBuilder.append(sumReport.finishOrderCount);
        stringBuilder.append("&nbsp;&nbsp;挂单：");
        stringBuilder.append(sumReport.tmpOrderCount);

        return stringBuilder.toString();
    }

    private SparseArray<ArrayList<OrderItemInfo>> getOrderItemInfos(Cursor todayProductCursor) {
        SparseArray<ArrayList<OrderItemInfo>> orderItemInfoMap = new SparseArray<ArrayList<OrderItemInfo>>();
        if (todayProductCursor != null && todayProductCursor.getCount() > 0) {
            while (todayProductCursor.moveToNext()) {
                OrderItemInfo orderInfo = OrderItemInfo.fromCursor(todayProductCursor);
                ArrayList<OrderItemInfo> orderItemInfoList = orderItemInfoMap.get(orderInfo.getOrderId());
                if (orderItemInfoList == null) {
                    orderItemInfoList = new ArrayList<OrderItemInfo>();
                    orderItemInfoMap.put(orderInfo.getOrderId(), orderItemInfoList);
                }
                orderItemInfoList.add(orderInfo);
            }
        }

        return orderItemInfoMap;
    }

    private SparseArray<OrderInfo> getOrderInfos(Cursor todayOrderCursor) {
        SparseArray<OrderInfo> orderInfoMap = new SparseArray<OrderInfo>();
        if (todayOrderCursor != null && todayOrderCursor.getCount() > 0) {
            while (todayOrderCursor.moveToNext()) {
                OrderInfo info = OrderInfo.fromCusor(todayOrderCursor);
                orderInfoMap.put(info.getId(), info);
            }
        }

        return orderInfoMap;
    }

    private String getFormatDate(long time) {
        return mContentDateFormat.format(new Date(time));
    }

    private String getPayStatus(int flag) {
        return flag == 1 ? mContext.getString(R.string.has_payed) : mContext.getString(R.string.unpayed);
    }

    private String getPayTime(long time) {
        if (time <= 0) {
            return "-";
        } else {
            return mContentDateFormat.format(new Date(time));
        }
    }

    private String getOrderDetail(ArrayList<OrderItemInfo> orderItemInfos) {
        if (orderItemInfos != null && orderItemInfos.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (OrderItemInfo orderItemInfo : orderItemInfos) {
                stringBuilder.append(orderItemInfo.getProductName());
                stringBuilder.append("\t\t");
                stringBuilder.append("x" + orderItemInfo.getCount());
                stringBuilder.append("\t\t");
                stringBuilder.append("合计:" + orderItemInfo.getCount() * orderItemInfo.getProductPrice());
                stringBuilder.append("<br>");
            }

            int i = stringBuilder.lastIndexOf("<br>");

            return stringBuilder.substring(0, i);
        }

        return "-";
    }

    private String getOrderStatus(int flag) {
        return flag > 0 ? mContext.getString(R.string.order_status_delete) : mContext.getString(R.string.order_status_normal);
    }

    private Cursor queryTodayOrders(long todayZeroTime, long nextDayZeroTime) {
        String selection = AccsTables.Order.COL_DATE + " BETWEEN ? AND ? ";
        String[] args = new String[]{String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        String orderBy = AccsTables.Order.COL_STATUS + " ASC , " + AccsTables.Order.COL_PAYED + " DESC , " + AccsTables.Order.COL_DATE + " DESC";
        return mResolver.query(QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }

    private Cursor queryTodayProducts(long todayZeroTime, long nextDayZeroTime) {
        String selection = AccsTables.OrderDetail.COL_ORDER_ID + " IN ("
                + " SELECT " + AccsTables.Order._ID + " FROM " + AccsTables.Order.TABLE_NAME + " WHERE " + AccsTables.Order.COL_DATE + " BETWEEN ? AND ? "
                + ")";
        String[] args = new String[]{String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        return mResolver.query(QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);

    }


    private SumReport createTodaySumReport(SparseArray<OrderInfo> orderInfoMap, SparseArray<ArrayList<OrderItemInfo>> orderItemInfoMap) {
        SumReport sumReport = new SumReport();

        if (orderItemInfoMap == null || orderItemInfoMap.size() == 0) {
            return sumReport;
        }


        int orderSize = orderItemInfoMap.size();
        for (int i = 0; i < orderSize; i++) {
            int orderId = orderItemInfoMap.keyAt(i);
            OrderInfo orderInfo = orderInfoMap.get(orderId);

            if (orderInfo.getOrderStatus() > 0 || orderInfo.getPayed() == 0) {
                if (orderInfo.getPayed() == 0 && orderInfo.getOrderStatus() == 0) {
                    sumReport.tmpOrderCount++;
                }
                continue;
            }


            sumReport.finishOrderCount++;

            ArrayList<OrderItemInfo> itemInfos = orderItemInfoMap.valueAt(i);


            for (OrderItemInfo itemInfo : itemInfos) {
                sumReport.totalCash += itemInfo.getCount() * itemInfo.getProductPrice() * itemInfo.getDiscount();
            }
        }

        return sumReport;
    }

    private class SumReport {
        private float totalCash;
        private int finishOrderCount;
        private int tmpOrderCount;
    }
}
