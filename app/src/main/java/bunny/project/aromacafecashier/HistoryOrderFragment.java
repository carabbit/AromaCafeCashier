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
    private OrderDetailFragment mOrderDetailFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_order_fragment, null);

        mOrderDetailFragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IntentKeys.HISTORY_MODE, true);
        mOrderDetailFragment.setArguments(bundle);

        OrderListFragment orderListFragment = new OrderListFragment();
//        orderListFragment.setOrderItemClickListener(mOrderDetailFragment.mOrderItemClickListener);
        orderListFragment.setOrderItemClickListener(this);


        getFragmentManager()
                .beginTransaction()
                .add(R.id.order_detail_fragment_container, mOrderDetailFragment)
                .add(R.id.order_list_fragment_container, orderListFragment)
                .commitAllowingStateLoss();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnFinishOrder = (Button) view.findViewById(R.id.btn_finish_order);
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
    }

    @Override
    public void onOrderItemClick(OrderInfo order) {
        mBtnFinishOrder.setVisibility(order.getPayed() == 1 ? View.INVISIBLE : View.VISIBLE);
        mBtnFinishOrder.setTag(Integer.valueOf(order.getId()));
        mOrderDetailFragment.queryOrder(order.getId());
    }
}