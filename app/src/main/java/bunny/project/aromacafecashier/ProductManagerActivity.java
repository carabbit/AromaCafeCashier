package bunny.project.aromacafecashier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by bunny on 2017/3/12.
 */

public class ProductManagerActivity extends Activity {
    View mContentView;
    Button mBtnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_manager);
        mContentView = findViewById(R.id.content);
        mBtnCreate = (Button) findViewById(R.id.btn_create);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CreateProductFragment fragment = new CreateProductFragment();
//                fragment.show(getFragmentManager(), "CreateProductFragment");
                startActivity(new Intent(ProductManagerActivity.this, ProductEditorActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
