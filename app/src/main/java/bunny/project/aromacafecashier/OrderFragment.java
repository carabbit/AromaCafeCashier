package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import bunny.project.aromacafecashier.model.OrderItem;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * Created by bunny on 17-3-16.
 */

public class OrderFragment extends Fragment implements OrderConfirmDialogFragment.OrderListener {
    private Button mBtnOrder;
    private Button mBtnTempOrder;
    //    private Button mBtnProductManager;
    private OrderDetailFragment mOrderListFragment;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (v.getId() == R.id.btn_menu_manager) {
//                Intent intent = new Intent(getActivity(), ProductManagerActivity.class);
//                startActivity(intent);
//                return;
//            } else
            if (v.getId() == R.id.btn_temp_order) {
                return;
            } else if (v.getId() == R.id.btn_comfirm_order) {
                ArrayList<OrderItem> orderItems = mOrderListFragment.getOrderItems();

                if (orderItems.size() == 0) {
                    Toast.makeText(getActivity(), R.string.create_order_fail, Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(orderItems);

                return;
            }
        }

        private void showConfirmDialog(ArrayList<OrderItem> orderItems) {
//            showDialog(0);
            OrderConfirmDialogFragment fragment = new OrderConfirmDialogFragment();
            fragment.setOrderListener(OrderFragment.this);

            Bundle data = new Bundle();
            data.putParcelableArrayList(IntentKeys.ORDER_ITEM, orderItems);
            fragment.setArguments(data);
//            fragment.show(getFragmentManager(), "OrderConfirmDialogFragment");

            getFragmentManager().beginTransaction()
                    .replace(R.id.dialog_container, fragment)
                    .commitAllowingStateLoss();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order, null);
        mOrderListFragment = new OrderDetailFragment();
        ProductListFragment productListFragment = new ProductListFragment();
        productListFragment.setProductItemClickListener(mOrderListFragment.mProductItemClickListener);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.order_list_fragment_container, mOrderListFragment)
                .add(R.id.product_list_fragment_container, productListFragment)
                .commitAllowingStateLoss();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnOrder = (Button) view.findViewById(R.id.btn_comfirm_order);
        mBtnTempOrder = (Button) view.findViewById(R.id.btn_temp_order);
//        mBtnProductManager = (Button) view.findViewById(R.id.btn_menu_manager);

        mBtnOrder.setOnClickListener(mOnClickListener);
        mBtnTempOrder.setOnClickListener(mOnClickListener);
//        mBtnProductManager.setOnClickListener(mOnClickListener);


    }

    @Override
    public void onOrderFinish() {
//        hideStatusBar();
    }
}
