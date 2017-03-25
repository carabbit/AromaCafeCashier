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

import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.utility.BitmapTool;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * Created by bunny on 17-3-16.
 */

public class ProductManagerFragment extends Fragment implements ProductListFragment.ProductItemClickListener {

    Button mBtnCreateProduct;
    Button mBtnDeleteProduct;
    Button mBtnDeleteType;
    //    ImageView mBtnBack;
    // TODO 商品编辑功能（待做）
    //    Button mBtnEdit;
    ImageView mProductImgView;
    TextView mProductNameView;
    TextView mProudctPriceView;
    TextView mProductTypeView;
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
                    deteteProduct(v);
                    break;
                case R.id.btn_create:
                    startActivity(new Intent(getActivity(), ProductEditorActivity.class));
                    break;
            }
        }
    };

    private void deleteType() {
        mProductListFragment.deleteCurrentType();
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
//        mBtnEdit = (Button) findViewById(R.id.btn_edit);
        mBtnCreateProduct = (Button) view.findViewById(R.id.btn_create);

        mBtnCreateProduct.setOnClickListener(mOnClickListener);
        mBtnDeleteProduct.setOnClickListener(mOnClickListener);
        mBtnDeleteType.setOnClickListener(mOnClickListener);

//        mBtnEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editProduct(v);
//            }
//        });

//        mBtnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    private void editProduct(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int productId = (Integer) tag;
            MyLog.i("", "edit id:" + productId);
            Intent intent = new Intent(getActivity(), ProductEditorActivity.class);
            intent.putExtra(IntentKeys.ID, productId);
            startActivity(intent);
        }
    }

    private void deteteProduct(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int productId = (Integer) tag;
            MyLog.i("", "delete id:" + productId);
            mProductListFragment.deleteItem(productId);

            setPrudoctInfo(null);
        }
    }

    private void setPrudoctInfo(Product product) {
        if (product == null) {
            mProductImgView.setImageDrawable(null);
            mProductNameView.setText(null);
            mProudctPriceView.setText(null);
            mProductTypeView.setText(null);

            mBtnDeleteProduct.setTag(null);
//            mBtnEdit.setTag(null);
        } else {
            mProductImgView.setImageBitmap(BitmapTool.bytes2Bimap(product.getImage()));
            mProductNameView.setText(product.getName());
            mProudctPriceView.setText(String.valueOf(product.getPrice()));
            mProductTypeView.setText(product.getType());
            mBtnDeleteProduct.setTag(Integer.valueOf(product.getId()));
        }
    }


    @Override
    public void onItemClick(Product product) {
        setPrudoctInfo(product);
    }
}
