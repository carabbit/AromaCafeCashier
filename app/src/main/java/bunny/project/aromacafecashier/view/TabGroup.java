package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bunny.project.aromacafecashier.R;

/**
 * Created by bunny on 2017/3/19.
 */

public class TabGroup extends RadioGroup {
    private android.content.Context mContext;

    public TabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void addTab(int typeId, String tabName) {
        RadioButton rbtn = new RadioButton(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 50, 0);

        layoutParams.rightMargin = 10;

        rbtn.setLayoutParams(layoutParams);
        rbtn.setId(View.NO_ID);
        rbtn.setText(tabName);
        rbtn.setButtonDrawable(0);
        rbtn.setTag(typeId);
        rbtn.setBackgroundResource(R.drawable.product_tab_bg);
        rbtn.setPadding(10, 10, 10, 10);
        addView(rbtn);
    }
}
