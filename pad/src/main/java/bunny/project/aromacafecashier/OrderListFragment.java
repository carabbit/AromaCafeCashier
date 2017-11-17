package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bunny.project.aromacafecashier.common.AccsUris;
import bunny.project.aromacafecashier.common.QueryManager;
import bunny.project.aromacafecashier.common.model.OrderInfo;
import bunny.project.aromacafecashier.common.provider.AccsTables;
import bunny.project.aromacafecashier.report.SendReportTask;
import bunny.project.aromacafecashier.view.HistoryOrderItemView;

/**
 * 显示挂单和历史订单
 * Created by bunny on 17-3-16.
 */

public class OrderListFragment extends Fragment {
    private static final int TOKEN_QUEREY_ALL_TEMP_ORDER = 1;
    private static final int TOKEN_QUEREY_ALL_ORDER = 2;
    private static final int TOKEN_QUEREY_TODAY_ORDER = 3;
    private static final int TOKEN_QUEREY_TODAY_PRODUCTS = 4;
    private static final int TOKEN_UPDATE_ORDER_STATUS = 5;

    //    private ListView mListTempOrder;
    private ListView mOrderListView;
    //    private List<OrderInfo> mTempOrders = new ArrayList<OrderInfo>();
    private List<OrderInfo> mHistoryOrders = new ArrayList<OrderInfo>();

    //    private CursorAdapter mTempOrderAdapter;
    private CursorAdapter mHistoryOrderAdapter;

    private AsyncQueryHandler mQueryHandler;
    private OrderItemClickListener mOrderItemClickListener;

    private Button mBtnTodayOrder;
    private Button mBtnAllTempOrder;
    private Button mBtnAllOrder;
    private Button mBtnSendReport;

    private TextView mTitleView;
    private TextView mTodayCashView;
    private TextView mTodayReportFinishView;
    private TextView mTodayReportTempView;

    private View mTodayReprotContainer;

    private int mCurrentClickItemId = -1;

    private long mLastClickTime;
    private static final int CLICK_INTERVAL = 60 * 1000;

    public void setOrderItemClickListener(OrderItemClickListener listener) {
        this.mOrderItemClickListener = listener;
    }


    public static interface OrderItemClickListener {
        void onOrderItemClick(OrderInfo order);
    }

    private class OrderQueryHandler extends AsyncQueryHandler {

        public OrderQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case TOKEN_UPDATE_ORDER_STATUS:
                    MyLog.i("xxx", "ret:" + result);
                    if (result > 0) {
                        if (getString(R.string.all_order).equals(mTitleView.getText())) {
                            queryAllOrders();
                        } else if (getString(R.string.all_temp_order).equals(mTitleView.getText())) {
                            queryTempOrders();
                        } else if (getString(R.string.today_order).equals(mTitleView.getText())) {
                            queryTodayData();
                        }
                    }
                    break;
            }
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            int resId = 0;
            switch (token) {
                case TOKEN_QUEREY_ALL_ORDER:
                    resId = R.string.all_order;
                    break;
                case TOKEN_QUEREY_ALL_TEMP_ORDER:
                    resId = R.string.all_temp_order;
                    break;
                case TOKEN_QUEREY_TODAY_ORDER:
                    resId = R.string.today_order;
                    setTodayReport(cursor);
                    break;
                case TOKEN_QUEREY_TODAY_PRODUCTS:
                    float todayCash = countTodayCash(cursor);
                    mTodayCashView.setText(getString(R.string.today_cash, todayCash));
                    mTodayReprotContainer.setVisibility(View.VISIBLE);
                    return;
            }

            mTitleView.setText(resId);
            mHistoryOrderAdapter.changeCursor(cursor);
        }

        private void setTodayReport(Cursor cursor) {
            int finishOrderCount = 0;
            int tmpOrderCount = 0;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int padyed = cursor.getInt(QueryManager.INDEX_ORDER_PAYED);
                    if (padyed == 1) {
                        finishOrderCount++;
                    } else {
                        tmpOrderCount++;
                    }

                }
            }

            mTodayReportFinishView.setText(getString(R.string.today_repot_finish_order, finishOrderCount));
            mTodayReportTempView.setText(getString(R.string.today_repot_temp_order, tmpOrderCount));
        }

        private float countTodayCash(Cursor cursor) {
            if (cursor == null) {
                return 0f;
            }

            SparseArray<Float> cashPerOrderMap = new SparseArray<Float>();
            SparseArray<Float> discountPerOrderMap = new SparseArray<Float>();

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
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOrderItemClickListener != null) {
                OrderInfo orderInfo = (OrderInfo) view.getTag();
                mOrderItemClickListener.onOrderItemClick(orderInfo);

                mCurrentClickItemId = orderInfo.getId();
            }


            mHistoryOrderAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_all_order:
                    mTodayReprotContainer.setVisibility(View.GONE);
                    queryAllOrders();
                    break;
                case R.id.btn_all_temp_order:
                    mTodayReprotContainer.setVisibility(View.GONE);
                    queryTempOrders();
                    break;
                case R.id.btn_today_order:
                    mTodayReprotContainer.setVisibility(View.VISIBLE);
                    queryTodayData();
                    break;
                case R.id.btn_send_report:
                    long currentTime = System.currentTimeMillis();
                    long interval = currentTime - mLastClickTime;
                    if (interval < 0) {
                        Toast.makeText(getActivity(), getString(R.string.sys_time_error), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (interval < CLICK_INTERVAL) {
                        Toast.makeText(getActivity(), getString(R.string.cool_time, (CLICK_INTERVAL - interval) / 1000), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        mLastClickTime = currentTime;
                        sendReport();
                        MyLog.i("", "sendReport()");
                    }

                    break;
            }
        }
    };

    private void sendReport() {
        new SendReportTask(getActivity(), null).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mListTempOrder = (ListView) view.findViewById(R.id.listTempOrder);
        mOrderListView = (ListView) view.findViewById(R.id.listHistoryOrder);

//        mTempOrderAdapter = new OrderListAdapter(getActivity());
        mHistoryOrderAdapter = new OrderListAdapter(getActivity());

//        mListTempOrder.setAdapter(mTempOrderAdapter);
        mOrderListView.setAdapter(mHistoryOrderAdapter);

//        mListTempOrder.setOnItemClickListener(mOnItemClickListener);
        mOrderListView.setOnItemClickListener(mOnItemClickListener);

        mTitleView = (TextView) view.findViewById(R.id.title);

        mTodayCashView = (TextView) view.findViewById(R.id.today_cash);
        mTodayReportFinishView = (TextView) view.findViewById(R.id.today_repot_finish_order);
        mTodayReportTempView = (TextView) view.findViewById(R.id.today_repot_temp_order);
        mTodayReprotContainer = view.findViewById(R.id.report_container);

        mTodayCashView.setText(getResources().getString(R.string.total_cash, 0f));
        mTodayReportFinishView.setText(getResources().getString(R.string.today_repot_finish_order, 0));
        mTodayReportTempView.setText(getResources().getString(R.string.today_repot_temp_order, 0));

        mBtnAllOrder = (Button) view.findViewById(R.id.btn_all_order);
        mBtnAllTempOrder = (Button) view.findViewById(R.id.btn_all_temp_order);
        mBtnTodayOrder = (Button) view.findViewById(R.id.btn_today_order);
        mBtnSendReport = (Button) view.findViewById(R.id.btn_send_report);

        mBtnAllOrder.setOnClickListener(mOnClickListener);
        mBtnAllTempOrder.setOnClickListener(mOnClickListener);
        mBtnTodayOrder.setOnClickListener(mOnClickListener);
        mBtnSendReport.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mQueryHandler = new OrderQueryHandler(getActivity().getContentResolver());

        queryTodayData();
    }

    private void queryTempOrders() {
        String selection = AccsTables.Order.COL_PAYED + " = ? AND " + AccsTables.Order.COL_STATUS + " = ?";
        String[] args = new String[]{"0", "0"};
        String orderBy = AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_ALL_TEMP_ORDER, null, AccsUris.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }


    private void queryTodayData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayZeroTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long nextDayZeroTime = cal.getTimeInMillis();

        queryTodayOrders(todayZeroTime, nextDayZeroTime);
        queryTodayProducts(todayZeroTime, nextDayZeroTime);
    }

    private void queryTodayOrders(long todayZeroTime, long nextDayZeroTime) {
        String selection = "(" + AccsTables.Order.COL_STATUS + " = ? ) AND (" + AccsTables.Order.COL_DATE + " BETWEEN ? AND ? ) ";
        String[] args = new String[]{String.valueOf(OrderInfo.STATUS_NOMAL), String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        String orderBy = AccsTables.Order.COL_STATUS + " ASC , " + AccsTables.Order.COL_PAYED + " ASC , " + AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_TODAY_ORDER, null, AccsUris.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }

    private void queryTodayProducts(long todayZeroTime, long nextDayZeroTime) {
        String selection = AccsTables.OrderDetail.COL_ORDER_ID + " IN ("
                + " SELECT " + AccsTables.Order._ID + " FROM " + AccsTables.Order.TABLE_NAME + " WHERE " + AccsTables.Order.COL_DATE + " BETWEEN ? AND ? "
                + " AND " + AccsTables.Order.COL_PAYED + " = 1"
                + " AND " + AccsTables.Order.COL_STATUS + " = 0"
                + ")";
        String[] args = new String[]{String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        mQueryHandler.startQuery(TOKEN_QUEREY_TODAY_PRODUCTS, null, AccsUris.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
    }

    private void queryAllOrders() {
        String orderBy = AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_ALL_ORDER, null, AccsUris.URI_ORDER, QueryManager.PROJECTION_ORDER, null, null, orderBy);
    }

    private class OrderListAdapter extends CursorAdapter {
        public OrderListAdapter(Context context) {
            super(context, null);
        }

        private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.histrory_order_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            HistoryOrderItemView itemView = (HistoryOrderItemView) view;
            OrderInfo order = OrderInfo.fromCusor(cursor);
            itemView.getOrderIdView().setText(String.valueOf(order.getId()));
            itemView.getViewOrderPayStatus().setText(order.getPayed() == 1 ? getString(R.string.has_payed) : getString(R.string.unpayed));

//            MyLog.i("xxx", "pay_time:" + order.getPayTime() + "  order_time:" + order.getDate());

            String payTimeStr = "";
            if (order.getPayTime() <= 0) {
                payTimeStr = "-";
            } else {
                payTimeStr = mDateFormat.format(new Date(order.getPayTime()));
            }
            itemView.getViewOrderPayTime().setText(payTimeStr);

            itemView.getViewOrderTime().setText(mDateFormat.format(new Date(order.getDate())));

            itemView.setTag(order);

            if (order.getPayed() < 1) {
                itemView.setBackgroundColor(getResources().getColor(R.color.list_item_highlight));
            } else {
                itemView.setBackgroundResource(0);
            }

            if (order.getOrderStatus() > 0) {
                itemView.getViewStatus().setText(R.string.order_status_delete);
                itemView.setBackgroundColor(getResources().getColor(R.color.list_item_delete));
            } else {
                itemView.getViewStatus().setText(R.string.order_status_normal);
            }

            if (order.getId() == mCurrentClickItemId) {
                itemView.getSelectIcon().setVisibility(View.VISIBLE);
            } else {
                itemView.getSelectIcon().setVisibility(View.INVISIBLE);
            }

            itemView.getDiscountIcon().setVisibility(order.getDiscount() < 1.0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void updateOrderStatus(OrderInfo info) {
        ContentValues values = new ContentValues();
        values.put(AccsTables.Order.COL_STATUS, info.getOrderStatus() == 0 ? 1 : 0);
        String selection = AccsTables.Order._ID + " = ?";
        String[] args = new String[]{String.valueOf(info.getId())};
        mQueryHandler.startUpdate(TOKEN_UPDATE_ORDER_STATUS, info, AccsUris.URI_ORDER, values, selection, args);
    }
}