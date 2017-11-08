package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bunny.project.aromacafecashier.R;

/**
 * 订单列表项控件
 * Created by bunny on 17-3-15.
 */
public class OrderItemView extends LinearLayout {
    private int productId;
    private TextView mNameView;
    private TextView mCountView;
    private TextView mProductPriceView;
    private TextView mSumPriceView;
    private ImageView mImgDeleteView;


    public OrderItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameView = (TextView) findViewById(R.id.product_name);
        mCountView = (TextView) findViewById(R.id.product_count);
        mProductPriceView = (TextView) findViewById(R.id.product_price);
        mSumPriceView = (TextView) findViewById(R.id.product_sum_price);
        mImgDeleteView = (ImageView) findViewById(R.id.btn_delete);

    }


    public ImageView getDeleteBtn() {
        return mImgDeleteView;
    }

    public TextView getNameView() {
        return mNameView;
    }

    public TextView getCountView() {
        return mCountView;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public TextView getProductPriceView() {
        return mProductPriceView;
    }

    public TextView getSumPriceView() {
        return mSumPriceView;
    }
}
