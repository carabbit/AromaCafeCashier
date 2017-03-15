package bunny.project.aromacafecashier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bunny.project.aromacafecashier.model.OrderItem;
import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.utility.IntentKeys;
import bunny.project.aromacafecashier.view.OrderItemView;

/**
 * Created by bunny on 2017/3/11.
 */
public class OrderActivity extends FullScreenActivity implements OrderConfirmDialogFragment.OrderListener {


    private Button mBtnOrder;
    private Button mBtnTempOrder;
    private Button mBtnProductManager;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_menu_manager) {
                Intent intent = new Intent(OrderActivity.this, ProductManagerActivity.class);
                startActivity(intent);
                return;
            } else if (v.getId() == R.id.btn_temp_order) {
                return;
            } else if (v.getId() == R.id.btn_comfirm_order) {
                ArrayList<OrderItem> orderItems = mOrderListFragment.getOrderItems();

                if (orderItems.size() == 0) {
                    Toast.makeText(OrderActivity.this, R.string.create_order_fail, Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(orderItems);

                return;
            }
        }

        private void showConfirmDialog(ArrayList<OrderItem> orderItems) {
            showDialog(0);
//            OrderConfirmDialogFragment fragment = new OrderConfirmDialogFragment();
//            fragment.setOrderListener(OrderActivity.this);
//
//            Bundle data = new Bundle();
//            data.putParcelableArrayList(IntentKeys.ORDER_ITEM, orderItems);
//            fragment.setArguments(data);
//            fragment.show(getFragmentManager(), "OrderConfirmDialogFragment");
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(this, R.style.mydialog);
        dialog.setTitle(R.string.add);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.mydialog);
//        builder.setTitle(R.string.add);
//
//        AlertDialog dialog = builder.create();

//        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        return dialog;
    }

    private OrderListFragment mOrderListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);


        mBtnOrder = (Button) findViewById(R.id.btn_comfirm_order);
        mBtnTempOrder = (Button) findViewById(R.id.btn_temp_order);
        mBtnProductManager = (Button) findViewById(R.id.btn_menu_manager);

        mBtnOrder.setOnClickListener(mOnClickListener);
        mBtnTempOrder.setOnClickListener(mOnClickListener);
        mBtnProductManager.setOnClickListener(mOnClickListener);

        mOrderListFragment = (OrderListFragment) getFragmentManager().findFragmentById(R.id.order_list_fragment);
        ProductListFragment productListFragment = (ProductListFragment) getFragmentManager().findFragmentById(R.id.product_list_fragment);

        productListFragment.setProductItemClickListener(mOrderListFragment.mProductItemClickListener);
    }

    @Override
    public void onOrderFinish() {
//        hideStatusBar();
    }
}
