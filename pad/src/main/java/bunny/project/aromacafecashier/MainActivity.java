package bunny.project.aromacafecashier;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bunny.project.aromacafecashier.lantransport.LanTransportHelper;
import bunny.project.aromacafecashier.lantransport.TransportCallback;
import bunny.project.aromacafecashier.lantransport.Utils;
import bunny.project.aromacafecashier.model.OrderItemInfo;
import bunny.project.aromacafecashier.utility.IntentKeys;

// TODO 数据上传云服务器功能（待做）
// TODO 数据备份导入导出功能（待做）
public class MainActivity extends FullScreenActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String FRAGMENT_ORDER = "FRAGMENT_ORDER";
    public static final String FRAGMENT_ORDER_HISTORY = "FRAGMENT_ORDER_HISTORY";
    public static final String FRAGMENT_PRODUCT_MANAGER = "FRAGMENT_PRODUCT_MANAGER";
    private RadioGroup mTabGroup;
    private Fragment mOrderFragment;
    private Fragment mOrderHistoryFragment;
    private Fragment mProductManagerFragment;

    private RadioButton mRbtnOrder;

//    private FrameLayout mDialogContainer;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.version) {
//                ((TextView) v).setText(Utils.getLocalHostIp());
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setMessage(Utils.getLocalHostIp()).create().show();
                String localIp = Utils.getLocalHostIp();
                String wifiName = Utils.getWifiName(MainActivity.this);
                Toast.makeText(MainActivity.this, wifiName + "   " + localIp, Toast.LENGTH_LONG).show();

//                LanTransportHelper.getInstance().acquireMultiCastLock(MainActivity.this);
//                LanTransportHelper.getInstance().sendUdpMulticast();
//                LanTransportHelper.getInstance().releaseMultiCastLock();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        swtichToFragment(FRAGMENT_ORDER, null);
//            }
//        }, 500);


        mTabGroup = (RadioGroup) findViewById(R.id.tabGroup);
        mTabGroup.setOnCheckedChangeListener(this);

        mRbtnOrder = (RadioButton) findViewById(R.id.tab_order);

//        mDialogContainer = (FrameLayout) findViewById(R.id.fragment_dialog_container);

        TextView versionView = (TextView) findViewById(R.id.version);
        versionView.setText(getVersion());
        versionView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.tab_order:
//                Toast.makeText(MainActivity.this, R.string.tab_order, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_ORDER);
                Object tag = mTabGroup.getTag();
                if (tag != null) {
                    mTabGroup.setTag(null);
                }
                swtichToFragment(FRAGMENT_ORDER, (Bundle) tag);
                break;
            case R.id.tab_order_history:
//                Toast.makeText(MainActivity.this, R.string.tab_order_history, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_ORDER_HISTORY);
                swtichToFragment(FRAGMENT_ORDER_HISTORY, null);
                break;
            case R.id.tab_product_manager:
//                Toast.makeText(MainActivity.this, R.string.tab_product_manager, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_PRODUCT_MANAGER);
                swtichToFragment(FRAGMENT_PRODUCT_MANAGER, null);
                break;
        }
    }

    private void swtichToFragment(String fragmentTag, Bundle args) {
//        MyLog.i("xxx", "[swtichToFragment] tag:" + fragmentTag);
        Fragment fragment = null;
        if (FRAGMENT_ORDER.equals(fragmentTag)) {
            fragment = new OrderFragment();
        } else if (FRAGMENT_ORDER_HISTORY.equals(fragmentTag)) {
            fragment = new HistoryOrderFragment();
        } else if (FRAGMENT_PRODUCT_MANAGER.equals(fragmentTag)) {
            fragment = new ProductManagerFragment();
        }

        if (fragment == null) {
            return;
        }

        fragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_order, fragment, fragmentTag);
        transaction.commit();

//        MyLog.i("xxx", "[swtichToFragment] finish");
    }

    public void finishOrder(int orderId, ArrayList<OrderItemInfo> orderItems) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(IntentKeys.ORDER_ITEM_LIST, orderItems);
        bundle.putInt(IntentKeys.ORDER_ID, orderId);
        mTabGroup.setTag(bundle);
        MyLog.i("finishOrder", "orderId:" + orderId);
        mRbtnOrder.setChecked(true);
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = "v" + info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

//    private void show(String fragmentTag) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        if (FRAGMENT_ORDER.equals(fragmentTag)) {
//            showFragment(transaction, FRAGMENT_ORDER);
//            hideFragment(transaction, mOrderHistoryFragment);
//            hideFragment(transaction, mProductManagerFragment);
//        } else if (FRAGMENT_ORDER_HISTORY.equals(fragmentTag)) {
//            showFragment(transaction, FRAGMENT_ORDER_HISTORY);
//            hideFragment(transaction, mOrderFragment);
//            hideFragment(transaction, mProductManagerFragment);
//        } else if (FRAGMENT_PRODUCT_MANAGER.equals(fragmentTag)) {
//            showFragment(transaction, FRAGMENT_PRODUCT_MANAGER);
//            hideFragment(transaction, mOrderFragment);
//            hideFragment(transaction, mOrderHistoryFragment);
//        }
//        transaction.commit();
//    }
//
//    private void showFragment(FragmentTransaction transaction, String fragmentTag) {
//        Fragment fragment = getFragment(fragmentTag);
//        if (fragment != null) {
//            transaction.show(fragment);
//        }
//    }
//
//    private void hideFragment(FragmentTransaction transaction, Fragment fragment) {
//        if (fragment != null) {
//            transaction.hide(fragment);
//        }
//    }


//    private Fragment getFragment(String fragmentTag) {
//        if (FRAGMENT_ORDER.equals(fragmentTag)) {
//            if (mOrderFragment == null) {
//                mOrderFragment = new OrderFragment();
//                getFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.fragment_container_order, mOrderFragment, FRAGMENT_ORDER)
//                        .commitAllowingStateLoss();
//            }
//            return mOrderFragment;
//        } else if (FRAGMENT_ORDER_HISTORY.equals(fragmentTag)) {
//            if (mOrderHistoryFragment == null) {
//                mOrderHistoryFragment = new HistoryOrderFragment();
//                getFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.fragment_container_order_history, mOrderHistoryFragment, FRAGMENT_ORDER_HISTORY)
//                        .commitAllowingStateLoss();
//            }
//            return mOrderHistoryFragment;
//        } else if (FRAGMENT_PRODUCT_MANAGER.equals(fragmentTag)) {
//            if (mProductManagerFragment == null) {
//                mProductManagerFragment = new ProductManagerFragment();
//                getFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.fragment_container_product_manager, mProductManagerFragment, FRAGMENT_PRODUCT_MANAGER)
//                        .commitAllowingStateLoss();
//            }
//            return mProductManagerFragment;
//        }
//
//        return null;
//    }
}
