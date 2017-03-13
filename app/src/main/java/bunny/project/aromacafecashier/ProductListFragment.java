package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static bunny.project.aromacafecashier.QueryManager.TYPE_PROJECTION;

/**
 * Created by bunny on 2017/3/12.
 */

public class ProductListFragment extends Fragment {
    private static final int TOKEN_QUERY_TYPE = 1;
    private static final int TOKEN_QUERY_PRODUCT = 2;

    private AsyncQueryHandler mQqueryHandle;
    private View mTabScrollView;
    private ViewGroup mTabContainer;
    private View mGridView;
    private View mEmptyView;


    //    private AsyncQueryHandler mQqueryHandle = new ;
    private class QueryProductHandler extends AsyncQueryHandler {

        public QueryProductHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TOKEN_QUERY_TYPE: {
                    if (cursor == null || cursor.getCount() == 0) {
                        mTabScrollView.setVisibility(View.GONE);
                        mGridView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mTabScrollView.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.GONE);
                        mTabContainer.removeAllViews();
                        while (cursor.moveToNext()) {
                            Button btn = new Button(getActivity());
                            btn.setTag(new Integer(cursor.getInt(0)));
                            btn.setText(cursor.getString(1));
                            mTabContainer.addView(btn);
                        }
                        cursor.close();


                    }

                    break;
                }
                case TOKEN_QUERY_PRODUCT: {
                    break;
                }
            }
        }
    }

    public void queryProductByType(int Type){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTabContainer = (ViewGroup) view.findViewById(R.id.tabContainer);
        mTabScrollView = view.findViewById(R.id.tabContainerContainer);
        mGridView = view.findViewById(R.id.product_gridview);
        mEmptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQqueryHandle = new QueryProductHandler(getActivity().getContentResolver());

    }

    @Override
    public void onResume() {
        super.onResume();
        mQqueryHandle.startQuery(TOKEN_QUERY_TYPE, null, QueryManager.TYPE_URI, TYPE_PROJECTION, null, null, null);
    }
}
