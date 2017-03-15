package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import bunny.project.aromacafecashier.model.OrderItem;
import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.view.OrderItemView;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderListFragment extends Fragment {

    private ListView mOrderListView;
    private BaseAdapter mOrderAdpter;

    private ArrayList<OrderItem> mOrderItems = new ArrayList<OrderItem>();

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
            orderItemView.getCountView().setText(String.valueOf(orderItem.getCount()));
            orderItemView.getDeleteBtn().setTag(orderItem.getProductId());
            orderItemView.getDeleteBtn().setOnClickListener(this);

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

            notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mOrderListView = (ListView) view.findViewById(R.id.list);
        mOrderAdpter = new OrderListAdapter();
        mOrderListView.setAdapter(mOrderAdpter);
    }

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

            mOrderAdpter.notifyDataSetChanged();
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
}
