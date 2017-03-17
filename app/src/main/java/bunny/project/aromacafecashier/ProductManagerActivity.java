package bunny.project.aromacafecashier;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.utility.BitmapTool;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * 商品管理界面
 * Created by bunny on 2017/3/12.
 */

public class ProductManagerActivity extends FullScreenActivity implements ProductListFragment.ProductItemClickListener {

    Button mBtnCreate;
    Button mBtnDelete;
    ImageView mBtnBack;
    //    Button mBtnEdit;
    ImageView mProductImgView;
    TextView mProductNameView;
    TextView mProudctPriceView;
    TextView mProductTypeView;
    ProductListFragment mProductListFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_manager);

        initViews();

//        Fragment productListFragment = getFragmentManager().findFragmentById(R.id.product_list_fragment);
//        if (productListFragment != null && productListFragment instanceof ProductListFragment) {
//            mProductListFragment = ((ProductListFragment) productListFragment);
//            mProductListFragment.setProductItemClickListener(this);
//        }
    }


    private void initViews() {
        mProductImgView = (ImageView) findViewById(R.id.product_img);
        mProductNameView = (TextView) findViewById(R.id.product_name);
        mProudctPriceView = (TextView) findViewById(R.id.product_price);
        mProductTypeView = (TextView) findViewById(R.id.product_type);


        mBtnDelete = (Button) findViewById(R.id.btn_delete);
//        mBtnEdit = (Button) findViewById(R.id.btn_edit);
        mBtnCreate = (Button) findViewById(R.id.btn_create);

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductManagerActivity.this, ProductEditorActivity.class));
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

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void editProduct(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int productId = (Integer) tag;
            MyLog.i("", "edit id:" + productId);
            Intent intent = new Intent(ProductManagerActivity.this, ProductEditorActivity.class);
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
