package bunny.project.aromacafecashier;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import bunny.project.aromacafecashier.provider.AccsProvider;
import bunny.project.aromacafecashier.provider.AccsTables;

/**
 * Created by bunny on 2017/3/12.
 */

public class QueryManager {
    private volatile static QueryManager sInstance;
    private final Context mContext;
    private static final Uri URI_BASE = Uri.parse("content://" + AccsProvider.AUTHORITY);
    public static final Uri URI_PRODUCT = URI_BASE.buildUpon().appendPath("product").build();
    public static final Uri URI_TYPE = URI_BASE.buildUpon().appendPath("type").build();
    public static final Uri URI_ORDER = URI_BASE.buildUpon().appendPath("order").build();
    public static final Uri URI_ORDER_DETAIL = URI_BASE.buildUpon().appendPath("order_detail").build();

    public static final String[] PROJECTION_TYPE = new String[]{
            AccsTables.ProductType._ID, AccsTables.ProductType.COL_NAME
    };

    public static final int INDEX_TYPE_ID = 0;
    public static final int INDEX_TYPE_NAME = 1;

    public static final String[] PROJECTION_PRODUCT = new String[]{
            AccsTables.Product._ID//.........0
            , AccsTables.Product.COL_NAME//.....1
            , AccsTables.Product.COL_PRICE//....2
            , AccsTables.Product.COL_TYPE_ID//..3
            , AccsTables.Views.COL_VIEW_TYPE//..4
            , AccsTables.Product.COL_IMAGE//....5
    };

    public static final int INDEX_PRODUCT_ID = 0;
    public static final int INDEX_PRODUCT_NAME = 1;
    public static final int INDEX_PRODUCT_PRICE = 2;
    public static final int INDEX_PRODUCT_TYPE_ID = 3;
    public static final int INDEX_PRODUCT_TYPE = 4;
    public static final int INDEX_PRODUCT_IMAGE = 5;

    private QueryListener mQueryListener;


    public static interface QueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    private QueryManager(Context context) {
        this.mContext = context;
    }

}
