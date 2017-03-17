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

    Button mBtnCreate;
    Button mBtnDelete;
    //    ImageView mBtnBack;
    //    Button mBtnEdit;
    ImageView mProductImgView;
    TextView mProductNameView;
    TextView mProudctPriceView;
    TextView mProductTypeView;
    private ProductListFragment mProductListFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_manager, null);
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


        mBtnDelete = (Button) view.findViewById(R.id.btn_delete);
//        mBtnEdit = (Button) findViewById(R.id.btn_edit);
        mBtnCreate = (Button) view.findViewById(R.id.btn_create);

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProductEditorActivity.class));
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deteteProduct(v);
            }
        });

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

            mBtnDelete.setTag(null);
//            mBtnEdit.setTag(null);
        } else {
            mProductImgView.setImageDrawable(BitmapTool.bytes2RoundDrawable(getResources(), product.getImage()));
            mProductNameView.setText(product.getName());
            mProudctPriceView.setText(String.valueOf(product.getPrice()));
            mProductTypeView.setText(product.getType());

            mBtnDelete.setTag(Integer.valueOf(product.getId()));
//            mBtnEdit.setTag(Integer.valueOf(product.getId()));
        }
    }


    @Override
    public void onItemClick(Product product) {
        setPrudoctInfo(product);
    }
}
