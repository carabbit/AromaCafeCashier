package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bunny on 2017/3/12.
 */

public class ProductListFragment extends Fragment {
    QueryManager mQueryManager;
    private View mTabContainer;
    private View mGridView;
    private View mEmptyView;

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
        mTabContainer = view.findViewById(R.id.tabContainer);
        mGridView = view.findViewById(R.id.product_gridview);
        mEmptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueryManager = QueryManager.getInstance(getActivity());
    }


}
