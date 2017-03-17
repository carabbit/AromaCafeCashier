package bunny.project.aromacafecashier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * 显示订单详情和订单列表
 * Created by bunny on 17-3-16.
 */
public class OrderHistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_history, null);

        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IntentKeys.HISTORY_MODE,true);
        orderDetailFragment.setArguments(bundle);

        OrderListFragment orderListFragment = new OrderListFragment();
        orderListFragment.setOrderItemClickListener(orderDetailFragment.mOrderItemClickListener);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.order_detail_fragment_container, orderDetailFragment)
                .add(R.id.order_list_fragment_container, orderListFragment)
                .commitAllowingStateLoss();

        return view;
    }
}
