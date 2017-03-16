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
import android.widget.CursorAdapter;
import android.widget.ListView;

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

    private static final int TOKEN_QUEREY_TEMP_ORDER = 1;
    private static final int TOKEN_QUEREY_HISTORY_ORDER = 2;

    private ListView mListTempOrder;
    private ListView mListHistoryOrder;
    private List<OrderInfo> mTempOrders = new ArrayList<OrderInfo>();
    private List<OrderInfo> mHistoryOrders = new ArrayList<OrderInfo>();

    private CursorAdapter mTempOrderAdapter;
    private CursorAdapter mHistoryOrderAdapter;

    private AsyncQueryHandler mQueryHandler;
    private OrderItemClickListener mOrderItemClickListener;

    public void setOrderItemClickListener(OrderItemClickListener listener) {
        this.mOrderItemClickListener = listener;
    }


    public static interface OrderItemClickListener {
        void onItemClick(OrderInfo order);
    }

    private class OrderQueryHandler extends AsyncQueryHandler {

        public OrderQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == TOKEN_QUEREY_HISTORY_ORDER) {
                MyLog.i("", "history:" + (cursor == null ? "null" : cursor.getCount()));
                mHistoryOrderAdapter.changeCursor(cursor);
            } else if (token == TOKEN_QUEREY_TEMP_ORDER) {
                MyLog.i("", "temp:" + (cursor == null ? "null" : cursor.getCount()));
                mTempOrderAdapter.changeCursor(cursor);
            }
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOrderItemClickListener != null) {
                mOrderItemClickListener.onItemClick((OrderInfo) view.getTag());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListTempOrder = (ListView) view.findViewById(R.id.listTempOrder);
        mListHistoryOrder = (ListView) view.findViewById(R.id.listHistoryOrder);

        mTempOrderAdapter = new OrderListAdapter(getActivity());
        mHistoryOrderAdapter = new OrderListAdapter(getActivity());

        mListTempOrder.setAdapter(mTempOrderAdapter);
        mListHistoryOrder.setAdapter(mHistoryOrderAdapter);

        mListTempOrder.setOnItemClickListener(mOnItemClickListener);
        mListHistoryOrder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueryHandler = new OrderQueryHandler(getActivity().getContentResolver());

        queryTempOrders();
        queryHistoryOrders();
    }

    private void queryTempOrders() {
        String selection = AccsTables.Order.COL_PAYED + " = ?";
        String[] args = new String[]{"0"};
        String orderBy = AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_TEMP_ORDER, null, QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
    }

    private void queryHistoryOrders() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayZeroTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long nextDayZeroTime = cal.getTimeInMillis();

        String selection = AccsTables.Order.COL_PAYED + " = ? AND (" + AccsTables.Order.COL_DATE + " BETWEEN ? AND ?) ";
        String[] args = new String[]{"1", String.valueOf(todayZeroTime), String.valueOf(nextDayZeroTime)};
        String orderBy = AccsTables.Order.COL_DATE + " DESC";
        mQueryHandler.startQuery(TOKEN_QUEREY_HISTORY_ORDER, null, QueryManager.URI_ORDER, QueryManager.PROJECTION_ORDER, selection, args, orderBy);
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

            itemView.getViewOrderPayTime().setText(mDateFormat.format(new Date(order.getPay_time())));
            itemView.getViewOrderTime().setText(mDateFormat.format(new Date(order.getDate())));

            itemView.setTag(order);
        }
    }
}
