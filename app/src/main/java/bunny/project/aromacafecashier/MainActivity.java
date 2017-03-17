package bunny.project.aromacafecashier;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.widget.RadioGroup;

public class MainActivity extends FullScreenActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String FRAGMENT_ORDER = "FRAGMENT_ORDER";
    public static final String FRAGMENT_ORDER_HISTORY = "FRAGMENT_ORDER_HISTORY";
    public static final String FRAGMENT_PRODUCT_MANAGER = "FRAGMENT_PRODUCT_MANAGER";
    private RadioGroup mTabGroup;
    private Fragment mOrderFragment;
    private Fragment mOrderHistoryFragment;
    private Fragment mProductManagerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        getFragment(FRAGMENT_ORDER);
//        getFragment(FRAGMENT_ORDER_HISTORY);
        swtichToFragment(FRAGMENT_ORDER);

        mTabGroup = (RadioGroup) findViewById(R.id.tabGroup);
        mTabGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.tab_order:
//                Toast.makeText(MainActivity.this, R.string.tab_order, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_ORDER);
                swtichToFragment(FRAGMENT_ORDER);
                break;
            case R.id.tab_order_history:
//                Toast.makeText(MainActivity.this, R.string.tab_order_history, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_ORDER_HISTORY);
                swtichToFragment(FRAGMENT_ORDER_HISTORY);
                break;
            case R.id.tab_product_manager:
//                Toast.makeText(MainActivity.this, R.string.tab_product_manager, Toast.LENGTH_SHORT).show();
//                show(FRAGMENT_PRODUCT_MANAGER);
                swtichToFragment(FRAGMENT_PRODUCT_MANAGER);
                break;
        }
    }

    private void swtichToFragment(String fragmentTag) {
        MyLog.i("xxx", "[swtichToFragment] tag:" + fragmentTag);
        Fragment fragment = null;
        if (FRAGMENT_ORDER.equals(fragmentTag)) {
            fragment = new OrderFragment();
        } else if (FRAGMENT_ORDER_HISTORY.equals(fragmentTag)) {
            fragment = new OrderHistoryFragment();
        } else if (FRAGMENT_PRODUCT_MANAGER.equals(fragmentTag)) {
            fragment = new ProductManagerFragment();
        }

        if (fragment == null) {
            return;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_order, fragment, fragmentTag);
        transaction.commit();

        MyLog.i("xxx", "[swtichToFragment] finish");
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
//                mOrderHistoryFragment = new OrderHistoryFragment();
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
