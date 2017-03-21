package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.R;

/**
 * Created by bunny on 2017/3/19.
 */

public class TabGroup extends LinearLayout {
    private android.content.Context mContext;
    private static final int MAX_COUNT_IN_LINE = 8;

    public TabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public void addTab(int typeId, String tabName) {
        int lineCount = getChildCount();
        if (lineCount == 0) {
            addNewLine();
        }

        ViewGroup lastChidViewGroup = (ViewGroup) getChildAt(getChildCount() - 1);


        if (lastChidViewGroup.getChildCount() == MAX_COUNT_IN_LINE) {
            addNewLine();
        }

        lastChidViewGroup = (ViewGroup) getChildAt(getChildCount() - 1);


        int groupWidth = getWidth();
        RadioButton rbtn = new RadioButton(mContext);
        int width = (int) getResources().getDimension(R.dimen.product_tab_width);

        MyLog.i("", "gwidth:" + groupWidth + " width:" + width);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(10, 10, 50, 0);
//        layoutParams.rightMargin = 10;

        rbtn.setLayoutParams(layoutParams);
        rbtn.setId(View.NO_ID);
        rbtn.setText(tabName);
        rbtn.setButtonDrawable(0);
        rbtn.setTag(typeId);
        rbtn.setBackgroundResource(R.drawable.product_tab_bg);
        rbtn.setPadding(10, 10, 10, 10);
        lastChidViewGroup.addView(rbtn);
    }

    private void addNewLine() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(linearLayout);
    }

    public void check(int pos) {

    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener) {

    }

    public View getTabViewAt(int pos) {
        int line = pos / MAX_COUNT_IN_LINE;
        int posInLine = pos % MAX_COUNT_IN_LINE - 1;
        ViewGroup lineGroupView = (ViewGroup) getChildAt(line);
        return lineGroupView.getChildAt(posInLine);
    }
}
