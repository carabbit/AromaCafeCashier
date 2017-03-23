package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.model.OrderItemInfo;
import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.provider.AccsTables;
import bunny.project.aromacafecashier.utility.IntentKeys;
import bunny.project.aromacafecashier.view.OrderItemView;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderDetailFragment extends Fragment {

    private ListView mOrderListView;
    private BaseAdapter mOrderAdpter;

    private ArrayList<OrderItemInfo> mOrderItems = new ArrayList<OrderItemInfo>();

    private AsyncQueryHandler mQueryOrderHandler;

    private boolean mIsHistoryMode;
    private TextView mTotalCashView;
    private TextView mOrderNumber;
    private int mOrderId = -1;

    //TODO 考虑使用子类实现历史订单详情的展示
    private class QueryOrderHandler extends AsyncQueryHandler {

        public QueryOrderHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    OrderItemInfo item = OrderItemInfo.fromCursor(cursor);
                    mOrderItems.add(item);
                }
                cursor.close();
            }
            refreshOrderList();
        }
    }

    private void refreshOrderList() {
        mOrderAdpter.notifyDataSetChanged();
        countTotalCash();
    }

    private class OrderListAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return mOrderItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mOrderItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.order_item_view, null);
            }

            if (!(convertView instanceof OrderItemView)) {
                return new TextView(getActivity());
            }

            OrderItemInfo orderItem = mOrderItems.get(position);

            OrderItemView orderItemView = (OrderItemView) convertView;

            orderItemView.setProductId(orderItem.getProductId());
            orderItemView.getNameView().setText(orderItem.getProductName());

//            String countStr = getResources().getString(R.string.item_count, orderItem.getCount());
            orderItemView.getCountView().setText(String.valueOf(orderItem.getCount()));

            orderItemView.getProductPriceView().setText(String.valueOf(orderItem.getProductPrice()));

            float sumPrice = orderItem.getProductPrice() * orderItem.getCount();
            String sumPriceStr = getResources().getString(R.string.sum_price, sumPrice);
            orderItemView.getSumPriceView().setText(sumPriceStr);

            if (mIsHistoryMode) {
                ((View) orderItemView.getDeleteBtn().getParent()).setVisibility(View.GONE);
            } else {
                orderItemView.getDeleteBtn().setVisibility(View.VISIBLE);
                orderItemView.getDeleteBtn().setTag(orderItem.getProductId());
                orderItemView.getDeleteBtn().setOnClickListener(this);
            }


            return convertView;
        }

        @Override
        public void onClick(View v) {

            if (v.getTag() == null || !(v.getTag() instanceof Integer)) {
                return;
            }

            int productId = (Integer) v.getTag();

            OrderItemInfo item = getOrderItem(productId);

            if (item == null) {
                return;
            }

            item.setCount(item.getCount() - 1);

            if (item.getCount() <= 0) {
                mOrderItems.remove(item);
            }

            refreshOrderList();
        }
    }

    private void countTotalCash() {
        float totalCash = 0f;
        for (OrderItemInfo item : mOrderItems) {
            totalCash += item.getCount() * item.getProductPrice();
        }
        String totalCashStr = getResources().getString(R.string.total_cash, totalCash);
//        MyLog.i("xxx", "totalCashStr:" + totalCashStr);
//        mTotalCashView.setText(String.format(totalCashStr, totalCash));
        mTotalCashView.setText(totalCashStr);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsHistoryMode = bundle.getBoolean(IntentKeys.HISTORY_MODE, false);
            ArrayList<OrderItemInfo> parcelableArrayList = bundle.getParcelableArrayList(IntentKeys.ORDER_ITEM_LIST);
            if (parcelableArrayList != null) {
                mOrderItems = parcelableArrayList;
            }
            mOrderId = bundle.getInt(IntentKeys.ORDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_detail_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTotalCashView = (TextView) view.findViewById(R.id.total_cash);
        mOrderNumber = (TextView) view.findViewById(R.id.order_number);
        mOrderListView = (ListView) view.findViewById(R.id.list);

        mOrderAdpter = new OrderListAdapter();
        mOrderListView.setAdapter(mOrderAdpter);
        if (mIsHistoryMode) {
            view.findViewById(R.id.btn_delete_container).setVisibility(View.GONE);
        }
        countTotalCash();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mIsHistoryMode) {
            mQueryOrderHandler = new QueryOrderHandler(getActivity().getContentResolver());
        }

        if (mOrderItems != null && mOrderItems.size() > 0) {
            mOrderAdpter.notifyDataSetChanged();
        }
    }

    public void queryOrder(int orderId) {
        mOrderItems.clear();
        String selection = AccsTables.OrderDetail.COL_ORDER_ID + "=?";
        String[] args = new String[]{String.valueOf(orderId)};
        mQueryOrderHandler.startQuery(0, null, QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
        mOrderNumber.setText(getString(R.string.order_number, orderId));
        mOrderNumber.setVisibility(View.VISIBLE);
    }

//    public OrderListFragment.OrderItemClickListener mOrderItemClickListener = new OrderListFragment.OrderItemClickListener() {
//        @Override
//        public void onOrderItemClick(OrderInfo order) {
//            MyLog.i("xxx", "OrderItemClickListener id:" + order.getId());
//            if (mQueryOrderHandler != null) {
//                mOrderItems.clear();
//                String selection = AccsTables.OrderDetail.COL_ORDER_ID + "=?";
//                String[] args = new String[]{String.valueOf(order.getId())};
//                mQueryOrderHandler.startQuery(0, null, QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
//            }
//        }
//    };


    public ProductListFragment.ProductItemClickListener mProductItemClickListener = new ProductListFragment.ProductItemClickListener() {

        @Override
        public void onItemClick(Product product) {
            OrderItemInfo item = getOrderItem(product.getId());

            if (item == null) {
                item = new OrderItemInfo();
                item.setCount(1);
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductPrice(product.getPrice());
                mOrderItems.add(item);
            } else {
                item.setCount(item.getCount() + 1);
            }

            refreshOrderList();
        }
    };

    private OrderItemInfo getOrderItem(int productId) {
        for (OrderItemInfo item : mOrderItems) {
            if (item.getProductId() == productId) {
                return item;
            }
        }

        return null;
    }

    public ArrayList<OrderItemInfo> getOrderItems() {
        return mOrderItems;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void resetListView() {
        mOrderItems.clear();
        mOrderAdpter.notifyDataSetChanged();
    }

//    private class QueryOrderTask extends AsyncTask<Integer, Void, Void> {
//        private ContentResolver mResolver;
//
//        public QueryOrderTask(ContentResolver resolver) {
//
//            mResolver = resolver;
//        }
//
//        @Override
//        protected Void doInBackground(Integer... params) {
//            String selection = AccsTables.OrderDetail.COL_ORDER_ID + "=?";
//            String[] args = new String[]{String.valueOf(params[0])};
//            Cursor cursor = mResolver.query(QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
//
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    OrderItemInfo item = OrderItemInfo.fromCursor(cursor);
//                    mOrderItems.add(item);
//                }
//                cursor.close();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            mOrderAdpter.notifyDataSetChanged();
//        }
//    }
}
