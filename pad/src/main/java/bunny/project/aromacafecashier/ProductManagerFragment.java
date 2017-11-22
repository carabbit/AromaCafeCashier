package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.utility.BitmapTool;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * Created by bunny on 17-3-16.
 */

public class ProductManagerFragment extends Fragment implements ProductListFragment.ProductItemClickListener {

    private Button mBtnCreateProduct;
    private Button mBtnDeleteProduct;
    private Button mBtnDeleteType;
    private Button mBtnSync;
    //    ImageView mBtnBack;
    private Button mBtnEditProduct;
    private ImageView mProductImgView;
    private TextView mProductNameView;
    private TextView mProudctPriceView;
    private TextView mProductTypeView;
    private ProductListFragment mProductListFragment;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_detele_type:
                    // TODO 使用弹窗提示：如果当前分类下有产品列表，则提示该分类下产品将归类于“未分类”（待做）
                    deleteType();
                    break;
                case R.id.btn_delete:
                    deleteProduct(v);
                    break;
                case R.id.btn_edit:
                    editProduct(v);
                    break;
                case R.id.btn_create:
                    startActivity(new Intent(getActivity(), ProductEditorActivity.class));
                    break;
                case R.id.btn_sync:
                    showDialog(new SyncDialogFragment());
                    break;
            }
        }
    };

    private void deleteType() {
        mProductListFragment.deleteCurrentType();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog(new PasswordDialogFragment());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_manager_fragment, null);
        mProductListFragment = new ProductListFragment();
        mProductListFragment.setProductItemClickListener(this);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.product_list_fragment_container, mProductListFragment)
                .commitAllowingStateLoss();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        mProductImgView = (ImageView) view.findViewById(R.id.product_img);
        mProductNameView = (TextView) view.findViewById(R.id.product_name);
        mProudctPriceView = (TextView) view.findViewById(R.id.product_price);
        mProductTypeView = (TextView) view.findViewById(R.id.product_type);


        mBtnDeleteType = (Button) view.findViewById(R.id.btn_detele_type);
        mBtnDeleteProduct = (Button) view.findViewById(R.id.btn_delete);
        mBtnEditProduct = (Button) view.findViewById(R.id.btn_edit);
        mBtnCreateProduct = (Button) view.findViewById(R.id.btn_create);
        mBtnSync = (Button) view.findViewById(R.id.btn_sync);

        mBtnCreateProduct.setOnClickListener(mOnClickListener);
        mBtnDeleteProduct.setOnClickListener(mOnClickListener);
        mBtnEditProduct.setOnClickListener(mOnClickListener);
        mBtnDeleteType.setOnClickListener(mOnClickListener);
        mBtnSync.setOnClickListener(mOnClickListener);
    }

    private void editProduct(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int productId = (Integer) tag;
            MyLog.i("", "edit id:" + productId);
            Intent intent = new Intent(getActivity(), ProductEditorActivity.class);
            intent.putExtra(IntentKeys.ID, productId);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.choose_product, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int productId = (Integer) tag;
            MyLog.i("", "delete id:" + productId);
            mProductListFragment.deleteItem(productId);

            setPrudoctInfo(null);
        } else {
            Toast.makeText(getActivity(), R.string.choose_product, Toast.LENGTH_SHORT).show();
        }

    }

    private void setPrudoctInfo(Product product) {
        if (product == null) {
            mProductImgView.setImageDrawable(null);
            mProductNameView.setText(null);
            mProudctPriceView.setText(null);
            mProductTypeView.setText(null);

            mBtnDeleteProduct.setTag(null);
            mBtnEditProduct.setTag(null);
        } else {
            mProductImgView.setImageBitmap(BitmapTool.bytes2Bimap(product.getImage()));
            mProductNameView.setText(product.getName());
            mProudctPriceView.setText(String.valueOf(product.getPrice()));
            mProductTypeView.setText(product.getType());
            mBtnDeleteProduct.setTag(product.getId());
            mBtnEditProduct.setTag(product.getId());
        }
    }


    @Override
    public void onItemClick(Product product) {
        MyLog.i("", "onItemClick");
        setPrudoctInfo(product);
    }

    public void showDialog(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_dialog_container, fragment)
                .commitAllowingStateLoss();
    }
}
