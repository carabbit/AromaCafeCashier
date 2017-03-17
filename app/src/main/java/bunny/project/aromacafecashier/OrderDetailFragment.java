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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.model.OrderItem;
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

    private ArrayList<OrderItem> mOrderItems = new ArrayList<OrderItem>();

    private AsyncQueryHandler mQueryOrderHandler;

    private boolean mIsHistoryMode;
    private TextView mTotalCashView;

    //TODO 考虑使用子类实现历史订单详情的展示
    private class QueryOrderHandler extends AsyncQueryHandler {

        public QueryOrderHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    OrderItem item = OrderItem.fromCursor(cursor);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.order_item, null);
            }

            if (!(convertView instanceof OrderItemView)) {
                return new TextView(getActivity());
            }

            OrderItem orderItem = mOrderItems.get(position);

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
                orderItemView.getDeleteBtn().setVisibility(View.GONE);
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

            OrderItem item = getOrderItem(productId);

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
        for (OrderItem item : mOrderItems) {
            totalCash += item.getCount() * item.getProductPrice();
        }
        String totalCashStr = getResources().getString(R.string.total_cash, totalCash);
//        MyLog.i("xxx", "totalCashStr:" + totalCashStr);
//        mTotalCashView.setText(String.format(totalCashStr, totalCash));
        mTotalCashView.setText(totalCashStr);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_detail_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTotalCashView = (TextView) view.findViewById(R.id.total_cash);
        mOrderListView = (ListView) view.findViewById(R.id.list);
        mOrderAdpter = new OrderListAdapter();
        mOrderListView.setAdapter(mOrderAdpter);

        countTotalCash();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsHistoryMode = bundle.getBoolean(IntentKeys.HISTORY_MODE, false);
        }

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_item, null);
        mOrderListView.addHeaderView(headerView);

        if (mIsHistoryMode) {
            mQueryOrderHandler = new QueryOrderHandler(getActivity().getContentResolver());
        }
    }

    public OrderListFragment.OrderItemClickListener mOrderItemClickListener = new OrderListFragment.OrderItemClickListener() {
        @Override
        public void onItemClick(OrderInfo order) {
            mOrderItems.clear();
            String selection = AccsTables.OrderDetail.COL_ORDER_ID + "=?";
            String[] args = new String[]{String.valueOf(order.getId())};
            mQueryOrderHandler.startQuery(0, null, QueryManager.URI_ORDER_DETAIL, QueryManager.PROJECTION_ORDER_DETAIL, selection, args, null);
        }
    };

    public ProductListFragment.ProductItemClickListener mProductItemClickListener = new ProductListFragment.ProductItemClickListener() {

        @Override
        public void onItemClick(Product product) {
            OrderItem item = getOrderItem(product.getId());

            if (item == null) {
                item = new OrderItem();
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

    private OrderItem getOrderItem(int productId) {
        for (OrderItem item : mOrderItems) {
            if (item.getProductId() == productId) {
                return item;
            }
        }

        return null;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return mOrderItems;
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
//                    OrderItem item = OrderItem.fromCursor(cursor);
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
