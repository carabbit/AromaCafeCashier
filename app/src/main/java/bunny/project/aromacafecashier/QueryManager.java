package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import bunny.project.aromacafecashier.provider.AccsProvider;

/**
 * Created by bunny on 2017/3/12.
 */

public class QueryManager {
    private volatile static QueryManager sInstance;
    private final Context mContext;
    private static final Uri BASE_URI = Uri.parse("content://" + AccsProvider.AUTHORITY);
    public static final Uri PRODUCT_URI = BASE_URI.buildUpon().appendPath("product").build();
    public static final Uri TYPE_URI = BASE_URI.buildUpon().appendPath("type").build();
    public static final Uri ORDER_URI = BASE_URI.buildUpon().appendPath("order").build();
    public static final Uri ORDER_TYPE_URI = BASE_URI.buildUpon().appendPath("order_detail").build();

    public static final String[] TYPE_PROJECTION = new String[] {
        "id","name"
    };

    private QueryListener mQueryListener;


    public static interface QueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    private QueryManager(Context context) {
        this.mContext = context;
//        mQqueryHandle = new QueryProductHandler(context.getContentResolver());
    }

//    public static QueryManager getInstance(Context context) {
//        if (sInstance == null) {
//            synchronized (QueryManager.class) {
//                if (sInstance == null) {
//                    sInstance = new QueryManager(context);
//                }
//            }
//        }
//        return sInstance;
//    }


    public void queryProductType(QueryListener listener) {
        mQueryListener = listener;
    }
}
