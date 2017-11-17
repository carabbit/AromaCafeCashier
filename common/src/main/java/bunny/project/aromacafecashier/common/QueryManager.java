package bunny.project.aromacafecashier.common;

import android.content.Context;
import android.database.Cursor;

import bunny.project.aromacafecashier.common.provider.AccsTables;

/**
 * Created by bunny on 2017/3/12.
 */

public class QueryManager {
    private volatile static QueryManager sInstance;
    private final Context mContext;

    public static final String[] PROJECTION_TYPE = new String[]{
            AccsTables.Type._ID, AccsTables.Type.COL_NAME
    };

    public static final int INDEX_TYPE_ID = 0;
    public static final int INDEX_TYPE_NAME = 1;

    public static final String[] PROJECTION_ORDER = new String[]{
            AccsTables.Order._ID // ............... 0
            , AccsTables.Order.COL_DATE // ........ 1
            , AccsTables.Order.COL_PAYED // ....... 2
            , AccsTables.Order.COL_PAY_TIME // .... 3
            , AccsTables.Order.COL_STATUS // ...... 4
            , AccsTables.Order.COL_DISCOUNT // .... 5
    };

    public static final int INDEX_ORDER_ID = 0;
    public static final int INDEX_ORDER_DATE = 1;
    public static final int INDEX_ORDER_PAYED = 2;
    public static final int INDEX_ORDER_PAY_TIME = 3;
    public static final int INDEX_ORDER_STATUS = 4;
    public static final int INDEX_ORDER_DISCOUNT = 5;


    public static final String[] PROJECTION_ORDER_DETAIL = new String[]{
            AccsTables.OrderDetail._ID // ................0
            , AccsTables.OrderDetail.COL_ORDER_ID // .....1
            , AccsTables.OrderDetail.COL_PRODUCT_ID // ...2
            , AccsTables.OrderDetail.COL_PRODUCT_NAME // .3
            , AccsTables.OrderDetail.COL_PRODUCT_PRICE //.4
            , AccsTables.OrderDetail.COL_COUNT // ........5
            , AccsTables.OrderDetail.COL_DISCOUNT // .....6
    };

    public static final int INDEX_ORDER_DETAIL_ID = 0;
    public static final int INDEX_ORDER_DETAIL_ORDER_ID = 1;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_ID = 2;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_NAME = 3;
    public static final int INDEX_ORDER_DETAIL_PRODUCT_PRICE = 4;
    public static final int INDEX_ORDER_DETAIL_COUNT = 5;
    public static final int INDEX_ORDER_DETAIL_DISCOUNT = 6;


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
