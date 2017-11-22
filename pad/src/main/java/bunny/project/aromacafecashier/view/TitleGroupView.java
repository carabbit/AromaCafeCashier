package bunny.project.aromacafecashier.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bunny.project.aromacafecashier.R;
import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by bunny on 17-11-20.
 */

public class TitleGroupView extends LinearLayout {

    private final TextView mTitleView;
    private final ViewGroup mContainerView;

    public TitleGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundResource(R.drawable.sync_text_bg);
        setOrientation(LinearLayout.VERTICAL);
        mTitleView = new TextView(context);
        mTitleView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mTitleView.setBackgroundResource(R.drawable.title_group_title_bg);
        mTitleView.setPadding(10, 10, 10, 10);
        mContainerView = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, -(int) getContext().getResources().getDimension(R.dimen.section_bg_stroke), 0, 0);
        mContainerView.setLayoutParams(params);
        mContainerView.setBackgroundResource(R.drawable.title_group_container_bg);
        mContainerView.setPadding(10, 10, 10, 10);
        mContainerView.setMinimumWidth(100);
        super.addView(mTitleView);
        super.addView(mContainerView);
    }

    public void setSectionTitle(@StringRes int res) {
        mTitleView.setText(res);
    }

    @Override
    public void addView(View child) {
        mContainerView.addView(child);
    }


    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        mContainerView.addView(child, params);
    }
}
