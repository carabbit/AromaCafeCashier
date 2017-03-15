package bunny.project.aromacafecashier;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by bunny on 17-3-14.
 */

public class ProductListItem extends LinearLayout {
    private TextView mNameView;
    private ImageView mImgView;

    public ProductListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameView = (TextView) findViewById(R.id.product_name);
        mImgView = (ImageView) findViewById(R.id.product_img);
    }

    public TextView getNameView() {
        return mNameView;
    }

    public ImageView getImgView() {
        return mImgView;
    }
}
