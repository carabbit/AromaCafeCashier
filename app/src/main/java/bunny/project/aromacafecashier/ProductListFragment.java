package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import bunny.project.aromacafecashier.model.Product;
import bunny.project.aromacafecashier.provider.AccsTables;
import bunny.project.aromacafecashier.utility.BitmapTool;
import bunny.project.aromacafecashier.view.MyButton;
import bunny.project.aromacafecashier.view.ProductListItemView;
import bunny.project.aromacafecashier.view.TabGroup;

import static bunny.project.aromacafecashier.QueryManager.PROJECTION_TYPE;

/**
 * Created by bunny on 2017/3/12.
 */

public class ProductListFragment extends Fragment {
    private static final int TOKEN_QUERY_ALL_TYPE = 1;
    private static final int TOKEN_QUERY_PRODUCT = 2;
    private static final int TOKEN_QUERY_PRODUCT_WITH_NO_TYPE = 3;
    private static final int TOKEN_DELETE_PRODUCT = 4;
    private static final int TOKEN_DELETE_TYPE = 5;

    private AsyncQueryHandler mQqueryHandle;
    //    private View mTabScrollView;
    private TabGroup mTabContainer;
    private GridView mGridView;
    private View mEmptyView;

    private CursorAdapter mProductAdapter;

    private ProductItemClickListener mListener;
    private int mCurrentTypeId;

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

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            View tabView = radioGroup.findViewById(i);
            MyLog.i("onCheckedChanged", "tabView:" + tabView);
            if (tabView != null && tabView.getTag() != null) {
                Object obj = tabView.getTag();
                if (obj instanceof Integer) {
                    mCurrentTypeId = (Integer) obj;
                    queryProductByType(mCurrentTypeId);
                }
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
                case TOKEN_QUERY_PRODUCT_WITH_NO_TYPE:
                    handleProdcutWithNoType(cursor);
                    break;
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
            } else if (token == TOKEN_DELETE_TYPE) {
                if (result > 0) {
//                    MyLog.i("", "mTabContainer size:" + mTabContainer.getChildCount());
//                    for (int i = 0; i < mTabContainer.getChildCount(); i++) {
//                        View childView = mTabContainer.getChildAt(i);
//                        int typeId = (Integer) childView.getTag();
//                        if (mCurrentTypeId == typeId) {
//                            MyLog.i("", "mTabContainer.removeView(childView);" + typeId);
//                            mTabContainer.removeView(childView);
//                            if (mTabContainer.getChildCount() > 0) {
//                                ((RadioButton) mTabContainer.getChildAt(0)).setChecked(true);
//                            }
//                            break;
//                        }
//                    }

                    queryAllType();
                }
            }
        }
    }

    private void handleProdcutWithNoType(Cursor cursor) {
        MyLog.i("[handleProdcutWithNoType]", "cursor size:" + (cursor == null ? "null" : cursor.getCount()));
        if (cursor != null && cursor.getCount() > 0) {
            mTabContainer.addTab(-1, getResources().getString(R.string.no_type));
            MyLog.i("[handleProdcutWithNoType]", "mTabContainer.getChildCount()-->" + mTabContainer.getChildCount());
            if (mTabContainer.getChildCount() == 1) {
                mTabContainer.check(mTabContainer.getChildAt(0).getId());
                queryProductByType(-1);
                mTabContainer.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }


        }
//        else {
//            if (mProductAdapter.getCount() == 0) {
//                mEmptyView.setVisibility(View.VISIBLE);
//            }
//        }

    }

    public int getCurrentTypeId() {
        return mCurrentTypeId;
    }

    public void deleteCurrentType() {
        String[] args = new String[]{String.valueOf(mCurrentTypeId)};
        String seletion = AccsTables.Type._ID + " = ?";
        mQqueryHandle.startDelete(TOKEN_DELETE_TYPE, mCurrentTypeId, QueryManager.URI_TYPE, seletion, args);
    }

    public void deleteItem(int productId) {
        String[] args = new String[]{String.valueOf(productId)};
        String seletion = AccsTables.Product._ID + " = ?";
        mQqueryHandle.startDelete(TOKEN_DELETE_PRODUCT, productId, QueryManager.URI_PRODUCT, seletion, args);
    }

    private void handleProductQuery(int type, Cursor cursor) {
        MyLog.i("handleProductQuery", "type:" + type + "  cursor size:" + (cursor == null ? "null" : cursor.getCount()));
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
//            mTabScrollView.setVisibility(View.GONE);
            mTabContainer.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
//            mEmptyView.setVisibility(View.VISIBLE);
        } else {
//            mTabScrollView.setVisibility(View.VISIBLE);
            mTabContainer.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
//                Button btn = new Button(getActivity());
//                btn.setTag(new Integer(cursor.getInt(QueryManager.INDEX_TYPE_ID)));
//                btn.setText(cursor.getString(QueryManager.INDEX_TYPE_NAME));
//                btn.setOnClickListener(mTypeOnClickListener);
//                mTabContainer.addView(btn);
                mTabContainer.addTab(cursor.getInt(QueryManager.INDEX_TYPE_ID), cursor.getString(QueryManager.INDEX_TYPE_NAME));
            }
            mTabContainer.setOnCheckedChangeListener(mOnCheckedChangeListener);


        }

        if (cursor != null) {
            cursor.close();
        }

        if (mTabContainer.getChildCount() > 0) {
            ((RadioButton) mTabContainer.getChildAt(0)).setChecked(true);
        }

        queryProductWithNoType();

//        View firstTypeView = mTabContainer.getChildAt(0);
//        if (firstTypeView != null) {
//            mCurrentTypeId = (Integer) firstTypeView.getTag();
//            mGridView.setTag(Integer.valueOf(mCurrentTypeId));
//            queryProductByType(mCurrentTypeId);
//        }
    }

    private void queryProductWithNoType() {
        String selection = AccsTables.Product.COL_TYPE_ID + " NOT IN ( SELECT " + AccsTables.Type._ID + " FROM " + AccsTables.Type.TABLE_NAME + ")";
        mQqueryHandle.startQuery(TOKEN_QUERY_PRODUCT_WITH_NO_TYPE, null, QueryManager.URI_PRODUCT, new String[]{AccsTables.Product._ID}, selection, null, null);
    }

    private void queryProductByType(int type) {
        String selection = null;
        String[] args = null;
        if (type < 0) {
            selection = AccsTables.Product.COL_TYPE_ID + " NOT IN ( SELECT " + AccsTables.Type._ID + " FROM " + AccsTables.Type.TABLE_NAME + ")";
        } else {
            selection = AccsTables.Product.COL_TYPE_ID + " = ?";
            args = new String[]{String.valueOf(type)};
        }

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
        mTabContainer = (TabGroup) view.findViewById(R.id.tabContainer);
//        mTabScrollView = view.findViewById(R.id.tabContainerContainer);
        mGridView = (GridView) view.findViewById(R.id.product_gridview);
        mEmptyView = view.findViewById(R.id.empty_view);

        mGridView.setOnItemClickListener(new GridViewOnItemClickListener());
//        mTabContainer.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQqueryHandle = new QueryProductHandler(getActivity().getContentResolver());
    }

    @Override
    public void onResume() {
        super.onResume();
        queryAllType();
    }

    private void queryAllType() {
        mQqueryHandle.startQuery(TOKEN_QUERY_ALL_TYPE, null, QueryManager.URI_TYPE, PROJECTION_TYPE, null, null, null);
    }

    public void setProductItemClickListener(ProductItemClickListener listener) {
        this.mListener = listener;
    }
}
