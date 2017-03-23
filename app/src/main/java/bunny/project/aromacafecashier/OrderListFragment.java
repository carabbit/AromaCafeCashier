package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.provider.AccsTables;
import bunny.project.aromacafecashier.view.HistoryOrderItemView;

/**
 * 显示挂单和历史订单
 * Created by bunny on 17-3-16.
 */

public class OrderListFragment extends Fragment {
    //TODO 订单废弃功能（待做）
    private static final int TOKEN_QUEREY_ALL_TEMP_ORDER = 1;
    private static final int TOKEN_QUEREY_ALL_ORDER = 2;
    private static final int TOKEN_QUEREY_TODAY_ORDER = 3;
    private static final int TOKEN_QUEREY_TODAY_PRODUCTS = 4;

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
    private TextView mTitleView;
    private TextView mTodayCashView;

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
                    break;
                case TOKEN_QUEREY_TODAY_PRODUCTS:
                    float todayCash = countTodayCash(cursor);
                    mTodayCashView.setText(getString(R.string.today_cash, todayCash));
                    return;
            }

            mTitleView.setText(resId);
            mHistoryOrderAdapter.changeCursor(cursor);
        }

        private float countTodayCash(Cursor cursor) {
            if (cursor == null) {
                return 0f;
            }

            float cash = 0f;
            while (cursor.moveToNext()) {
                int count = cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_COUNT);
                float price = cursor.getFloat(QueryManager.INDEX_ORDER_DETAIL_PRODUCT_PRICE);
                cash += count * price;
            }

            return cash;
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOrderItemClickListener != null) {
                mOrderItemClickListener.onOrderItemClick((OrderInfo) view.getTag());
            }

            // TODO 点击后，该行显示选中标记。（待做）
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_all_order:
                    mTodayCashView.setVisibility(View.GONE);
                    queryAllOrders();
                    break;
                case R.id.btn_all_temp_order:
                    mTodayCashView.setVisibility(View.GONE);
                    queryTempOrders();
                    break;
                case R.id.btn_today_order:
                    mTodayCashView.setVisibility(View.VISIBLE);
                    queryTodayData();
                    break;
            }
        }
    };

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
        mTodayCashView.setText(getResources().getString(R.string.total_cash, 0f));

        mBtnAllOrder = (Button) view.findViewById(R.id.btn_all_order);
        mBtnAllTempOrder = (Button) view.findViewById(R.id.btn_all_temp_order);
        mBtnTodayOrder = (Button) view.findViewById(R.id.btn_today_order);

        mBtnAllOrder.setOnClickListener(mOnClickListener);
        mBtnAllTempOrder.setOnClickListener(mOnClickListener);
        mBtnTodayOrder.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mQueryHandler = new OrderQueryHandler(getActivity().getContentResolver());

        queryTodayData();
    }

    private void queryTempOrders() {
        String selection = AccsTables.Order.COL_PAYED + " = ?";
        String[] args = new String[]{"0"};
        String orderBy = AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_ALL_TEMP_ORDER, null, QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
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
        String selection = AccsTables.Order.COL_DATE + " BETWEEN ? AND ? ";
        String orderBy = AccsTables.Order.COL_PAYED + " ASC , " + AccsTables.Order.COL_DATE + " DESC";
        String[] args = new String[]{String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        mQueryHandler.startQuery(TOKEN_QUEREY_TODAY_ORDER, null, QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }

    private void queryTodayProducts(long todayZeroTime, long nextDayZeroTime) {
        String selection = AccsTables.OrderDetail.COL_ORDER_ID + " IN ("
                + " SELECT " + AccsTables.Order._ID + " FROM " + AccsTables.Order.TABLE_NAME + " WHERE " + AccsTables.Order.COL_DATE + " BETWEEN ? AND ? "
                + " AND " + AccsTables.Order.COL_PAYED + " = 1"
                + ")";
        String[] args = new String[]{String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        mQueryHandler.startQuery(TOKEN_QUEREY_TODAY_PRODUCTS, null, QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
    }

    private void queryAllOrders() {
        String orderBy = AccsTables.Order.COL_PAYED + " ASC , " + AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_ALL_ORDER, null, QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, null, null, orderBy);
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
            MyLog.i("xxx", "bindView");
            HistoryOrderItemView itemView = (HistoryOrderItemView) view;
            OrderInfo order = OrderInfo.fromCusor(cursor);
            itemView.getOrderIdView().setText(String.valueOf(order.getId()));
            itemView.getViewOrderPayStatus().setText(order.getPayed() == 1 ? getString(R.string.has_payed) : getString(R.string.unpayed));

            MyLog.i("xxx", "pay_time:" + order.getPay_time() + "  order_time:" + order.getDate());

            String payTimeStr = "";
            if (order.getPay_time() <= 0) {
                payTimeStr = "-";
            } else {
                payTimeStr = mDateFormat.format(new Date(order.getPay_time()));
            }
            itemView.getViewOrderPayTime().setText(payTimeStr);

            itemView.getViewOrderTime().setText(mDateFormat.format(new Date(order.getDate())));

            itemView.setTag(order);

            if (order.getPayed() < 1) {
                itemView.setBackgroundColor(getResources().getColor(R.color.list_item_highlight));
            } else {
                itemView.setBackgroundResource(0);
            }

        }
    }
}