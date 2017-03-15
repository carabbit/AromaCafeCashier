package bunny.project.aromacafecashier;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.provider.AccsTables;
import bunny.project.aromacafecashier.utility.BitmapTool;
import bunny.project.aromacafecashier.view.ProductListItemView;

import static bunny.project.aromacafecashier.QueryManager.PROJECTION_TYPE;

/**
 * Created by bunny on 2017/3/12.
 */

public class ProductListFragment extends Fragment {
    private static final int TOKEN_QUERY_ALL_TYPE = 1;
    private static final int TOKEN_QUERY_PRODUCT = 2;
    private static final int TOKEN_DELETE_PRODUCT = 3;

    private AsyncQueryHandler mQqueryHandle;
    private View mTabScrollView;
    private ViewGroup mTabContainer;
    private GridView mGridView;
    private View mEmptyView;

    private CursorAdapter mProductAdapter;

    private ProductItemClickListener mListener;

    public static interface ProductItemClickListener {
        void onItemClick(Product product);
    }

    private View.OnClickListener mTypeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj != null && obj instanceof Integer) {
                int typeId = (Integer) obj;
                queryProductByType(typeId);
            }
        }
    };

    private class ProudctAdapter extends CursorAdapter {

        public ProudctAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.product_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (!(view instanceof ProductListItemView)) {
                return;
            }

            Product product = Product.fromCursor(cursor);

            ProductListItemView item = (ProductListItemView) view;

            item.setTag(product);

            item.getNameView().setText(product.getName());
            item.getImgView().setImageDrawable(BitmapTool.bytes2RoundDrawable(getResources(), product.getImage()));
        }
    }

    private class GridViewOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Object tag = view.getTag();
            if (tag != null && tag instanceof Product && mListener != null) {
                mListener.onItemClick((Product) tag);
            } else {

            }

        }
    }

    //    private AsyncQueryHandler mQqueryHandle = new ;
    private class QueryProductHandler extends AsyncQueryHandler {

        public QueryProductHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TOKEN_QUERY_ALL_TYPE: {
                    handleAllTypyQuery(cursor);
                    break;
                }
                case TOKEN_QUERY_PRODUCT: {
                    handleProductQuery((Integer) cookie, cursor);
                    break;
                }
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (token == TOKEN_DELETE_PRODUCT) {
                if (result > 0) {
                    Object obj = mGridView.getTag();
                    if (obj != null && obj instanceof Integer) {
                        int typeId = (Integer) obj;
                        queryProductByType(typeId);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.delete_product_fail, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void deleteItem(int productId) {
        String[] args = new String[]{String.valueOf(productId)};
        String seletion = AccsTables.Product._ID + " = ?";
        mQqueryHandle.startDelete(TOKEN_DELETE_PRODUCT, productId, QueryManager.URI_PRODUCT, seletion, args);
    }

    private void handleProductQuery(int type, Cursor cursor) {
        mGridView.setTag(type);

        if (cursor == null || cursor.getCount() == 0) {
            mGridView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mGridView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

        }

        if (mProductAdapter == null) {
            mProductAdapter = new ProudctAdapter(getActivity(), null);
            mGridView.setAdapter(mProductAdapter);
        }

        mProductAdapter.changeCursor(cursor);
    }

    private void handleAllTypyQuery(Cursor cursor) {
        mTabContainer.removeAllViews();

        if (cursor == null || cursor.getCount() == 0) {
            mTabScrollView.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mTabScrollView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                Button btn = new Button(getActivity());
                btn.setTag(new Integer(cursor.getInt(QueryManager.INDEX_TYPE_ID)));
                btn.setText(cursor.getString(QueryManager.INDEX_TYPE_NAME));
                btn.setOnClickListener(mTypeOnClickListener);
                mTabContainer.addView(btn);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        View firstTypeView = mTabContainer.getChildAt(0);
        if (firstTypeView != null) {
            int typeId = (Integer) firstTypeView.getTag();
            mGridView.setTag(Integer.valueOf(typeId));
            queryProductByType(typeId);
        }
    }

    private void queryProductByType(int type) {
        String selection = AccsTables.Product.COL_TYPE_ID + " = ?";
        String[] args = new String[]{String.valueOf(type)};
        String order = AccsTables.Product.COL_NAME;
        mQqueryHandle.startQuery(TOKEN_QUERY_PRODUCT, type, QueryManager.URI_PRODUCT, QueryManager.PROJECTION_PRODUCT, selection, args, order);
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
        mGridView = (GridView) view.findViewById(R.id.product_gridview);
        mEmptyView = view.findViewById(R.id.empty_view);

        mGridView.setOnItemClickListener(new GridViewOnItemClickListener());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQqueryHandle = new QueryProductHandler(getActivity().getContentResolver());
    }

    @Override
    public void onResume() {
        super.onResume();
        mQqueryHandle.startQuery(TOKEN_QUERY_ALL_TYPE, null, QueryManager.URI_TYPE, PROJECTION_TYPE, null, null, null);
    }

    public void setProductItemClickListener(ProductItemClickListener listener) {
        this.mListener = listener;
    }
}
