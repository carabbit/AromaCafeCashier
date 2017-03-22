package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.R;

/**
 * Created by bunny on 2017/3/19.
 */

public class TabGroup extends LinearLayout implements View.OnClickListener {
    private android.content.Context mContext;
    private static final int MAX_COUNT_IN_LINE = 8;
    private int mChildCount;

    private OnCheckedChangeListener mOnCheckedListener;

    @Override
    public void onClick(View view) {
        checkTab(view);
    }


    public static interface OnCheckedChangeListener {
        void onChecked(View view);
    }

    public TabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public void addTab(int typeId, String tabName) {
        int lineCount = super.getChildCount();
        if (lineCount == 0) {
            addNewLine();
        }

        ViewGroup lastChidViewGroup = (ViewGroup) getChildAt(super.getChildCount() - 1);


        if (lastChidViewGroup.getChildCount() == MAX_COUNT_IN_LINE) {
            addNewLine();
        }

        lastChidViewGroup = (ViewGroup) getChildAt(super.getChildCount() - 1);


        int groupWidth = getWidth();
        RadioButton rbtn = new RadioButton(mContext);
        int width = (int) getResources().getDimension(R.dimen.product_tab_width);
        int height = (int) getResources().getDimension(R.dimen.product_tab_height);

        MyLog.i("", "gwidth:" + groupWidth + " width:" + width);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
//        layoutParams.setMargins(10, 10, 50, 0);
//        layoutParams.rightMargin = 10;

        rbtn.setLayoutParams(layoutParams);
        rbtn.setId(View.NO_ID);
        rbtn.setText(tabName);
        rbtn.setTextColor(getResources().getColor(R.color.text_color));
        rbtn.setButtonDrawable(0);
        rbtn.setTag(typeId);
        rbtn.setGravity(Gravity.CENTER);
        rbtn.setBackgroundResource(R.drawable.product_tab_bg);
        rbtn.setPadding(10, 10, 10, 10);
//        rbtn.setOnCheckedChangeListener(this);
        rbtn.setOnClickListener(this);
        lastChidViewGroup.addView(rbtn);


        mChildCount++;
    }

    public void removeTab(int pos) {

    }

    public void removeTab(View view) {

    }

    private void addNewLine() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(linearLayout);
    }

//    public void check(int pos) {
//        View tabView = getTabViewAt(pos);
//        if (tabView instanceof RadioButton) {
//            ((RadioButton) tabView).setChecked(true);
//        }
//    }

    public void checkTab(int pos) {
        for (int i = 0; i < mChildCount; i++) {
            RadioButton tabView = (RadioButton) getTabViewAt(i);
            boolean checked = pos == i;
            tabView.setChecked(checked);
            setTabChecked(tabView, checked);
        }
    }

    public void checkTab(View view) {
        MyLog.i("[checkTab]", "mChildCount:" + mChildCount);
        for (int i = 0; i < mChildCount; i++) {
            RadioButton tabView = (RadioButton) getTabViewAt(i);
            boolean checked = tabView == view;
            setTabChecked(tabView, checked);
        }
    }

    private void setTabChecked(RadioButton tabView, boolean checked) {
        tabView.setChecked(checked);
        if (checked) {
            mOnCheckedListener.onChecked(tabView);
        }
    }


    public int getTabCount() {
        return mChildCount;
    }

    public View getTabViewAt(int pos) {
        int line = pos / MAX_COUNT_IN_LINE;
        int posInLine = pos % MAX_COUNT_IN_LINE;

        ViewGroup lineGroupView = (ViewGroup) getChildAt(line);
        return lineGroupView.getChildAt(posInLine);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedListener = listener;
    }

    public void removeAllTabs() {
        removeAllViews();
        mChildCount = 0;
    }
}