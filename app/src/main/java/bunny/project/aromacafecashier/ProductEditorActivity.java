package bunny.project.aromacafecashier;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 商品编辑界面，用于商品新增，或编辑。
 * Created by bunny on 2017/3/12.
 */

public class ProductEditorActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_product);
    }
}
