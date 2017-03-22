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
    public static final Uri URI_ORDER = URI_BASE.buildUpon().appendPath("order_fragment").build();
    public static final Uri URI_ORDER_DETAIL = URI_BASE.buildUpon().appendPath("order_detail").build();

    public static final String[] PROJECTION_TYPE = new String[]{
            AccsTables.Type._ID, AccsTables.Type.COL_NAME
    };

    public static final int INDEX_TYPE_ID = 0;
    public static final int INDEX_TYPE_NAME = 1;

    public static final String[] PROJECTION_ORDER = new String[]{
            AccsTables.Order._ID
            , AccsTables.Order.COL_DATE
            , AccsTables.Order.COL_PAYED
            , AccsTables.Order.COL_PAY_TIME
    };

    public static final int INDEX_ORDER_ID = 0;
    public static final int INDEX_ORDER_DATE = 1;
    public static final int INDEX_ORDER_PAYED = 2;
    public static final int INDEX_ORDER_PAY_TIME = 3;


    public static final String[] PROJECTION_ORDER_DETAIL = new String[]{
            AccsTables.OrderDetail._ID
            , AccsTables.OrderDetail.COL_ORDER_ID
            , AccsTables.OrderDetail.COL_PRODUCT_ID
            , AccsTables.OrderDetail.COL_PRODUCT_NAME
            , AccsTables.OrderDetail.COL_PRODUCT_PRICE
            , AccsTables.OrderDetail.COL_COUNT
    };

    public static final int INDEX_ORDER_DETAIL_ID = 0;
    public static final int INDEX_ORDER_DETAIL_ORDER_ID = 1;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_ID = 2;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_NAME = 3;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_PRICE = 4;
    public static final int INDEX_ORDER_DETAIL_COUNT = 5;


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
