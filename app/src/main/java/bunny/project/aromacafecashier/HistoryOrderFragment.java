package bunny.project.aromacafecashier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.model.OrderItemInfo;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * 显示订单详情和订单列表
 * Created by bunny on 17-3-16.
 */
public class HistoryOrderFragment extends Fragment implements OrderListFragment.OrderItemClickListener {

    private static final int TAG_ORDER_ID = 1;
    private Button mBtnFinishOrder;
    private Button mBtnOrderStatus;
    private OrderDetailFragment mOrderDetailFragment;
    private OrderListFragment mOrderListFragment;

    private OrderDeleteDialogFragment.OnClickListener mOnDeleteClickListener = new OrderDeleteDialogFragment.OnClickListener() {
        @Override
        public void onConfirmClicked() {
            Object tag = mBtnOrderStatus.getTag();
            if (tag != null || tag instanceof OrderInfo) {
                OrderInfo info = (OrderInfo) tag;
                mOrderListFragment.updateOrderStatus(info);
            }

            mBtnOrderStatus.setTag(null);
            mBtnOrderStatus.setVisibility(View.INVISIBLE);

            mBtnFinishOrder.setTag(null);
            mBtnFinishOrder.setVisibility(View.INVISIBLE);

            mOrderDetailFragment.resetListView();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_order_fragment, null);

        mOrderDetailFragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IntentKeys.HISTORY_MODE, true);
        mOrderDetailFragment.setArguments(bundle);

        mOrderListFragment = new OrderListFragment();
//        orderListFragment.setOrderItemClickListener(mOrderDetailFragment.mOrderItemClickListener);
        mOrderListFragment.setOrderItemClickListener(this);


        getFragmentManager()
                .beginTransaction()
                .add(R.id.order_detail_fragment_container, mOrderDetailFragment)
                .add(R.id.order_list_fragment_container, mOrderListFragment)
                .commitAllowingStateLoss();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnFinishOrder = (Button) view.findViewById(R.id.btn_finish_order);
        mBtnOrderStatus = (Button) view.findViewById(R.id.btn_order_status);


        mBtnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<OrderItemInfo> orderItems = mOrderDetailFragment.getOrderItems();
                int orderId = view.getTag() == null ? -1 : (int) view.getTag();
                if (orderItems == null || orderId <= 0) {
                    return;
                } else {
                    MyLog.i("onViewCreated", "order_fragment:" + orderId);
                    ((MainActivity) getActivity()).finishOrder(orderId, orderItems);
                }
            }
        });

        mBtnOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = mBtnOrderStatus.getTag();
                if (tag != null || tag instanceof OrderInfo) {
                    OrderDeleteDialogFragment fragment = new OrderDeleteDialogFragment();
                    fragment.setOnClickListener(mOnDeleteClickListener);
                    OrderInfo info = (OrderInfo) tag;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(IntentKeys.DELETE, info.getOrderStatus() == 0);
                    fragment.setArguments(bundle);
                    showDialog(fragment);
                }
            }
        });
    }

    @Override
    public void onOrderItemClick(OrderInfo order) {
        mBtnFinishOrder.setVisibility(order.getPayed() == 1 ? View.INVISIBLE : View.VISIBLE);
        mBtnFinishOrder.setTag(Integer.valueOf(order.getId()));

        mBtnOrderStatus.setVisibility(View.VISIBLE);
        mBtnOrderStatus.setTag(order);
        if (order.getOrderStatus() == 0) {
            mBtnOrderStatus.setText(R.string.delete_order);
        } else {
            mBtnOrderStatus.setText(R.string.recover_order);
        }


        mOrderDetailFragment.queryOrder(order.getId());
    }

    public void showDialog(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_dialog_container, fragment)
                .commitAllowingStateLoss();
    }
}