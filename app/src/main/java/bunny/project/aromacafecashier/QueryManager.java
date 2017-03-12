package bunny.project.aromacafecashier;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import bunny.project.aromacafecashier.provider.AccsProvider;

/**
 * Created by bunny on 2017/3/12.
 */

public class QueryManager {
    private volatile static QueryManager sInstance;
    private final Context mContext;
    private static final Uri BASE_URI = Uri.parse("content://" + AccsProvider.AUTHORITY);
    private static final Uri PRODUCT_URI = BASE_URI.buildUpon().appendPath("product").build();
    private static final Uri TYPE_URI = BASE_URI.buildUpon().appendPath("type").build();
    private static final Uri ORDER_URI = BASE_URI.buildUpon().appendPath("order").build();
    private static final Uri ORDER_TYPE_URI = BASE_URI.buildUpon().appendPath("order_detail").build();

    private QueryManager(Context context) {
        this.mContext = context;
    }

    public static QueryManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (QueryManager.class) {
                if (sInstance == null) {
                    sInstance = new QueryManager(context);
                }
            }
        }
        return sInstance;
    }

    public Cursor getProductType() {
        mContext.getContentResolver().query(TYPE_URI, null, null, null, null);
        return null;
    }
}
