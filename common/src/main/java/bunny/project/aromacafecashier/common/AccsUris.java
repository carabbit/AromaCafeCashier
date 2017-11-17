package bunny.project.aromacafecashier.common;

import android.net.Uri;

import bunny.project.aromacafecashier.common.provider.AccsProvider;

/**
 * Created by bunny on 17-11-15.
 */

public class AccsUris {
    private static final Uri URI_BASE = Uri.parse("content://" + AccsProvider.AUTHORITY);
    public static final Uri URI_ORDER_DETAIL = URI_BASE.buildUpon().appendPath("order_detail").build();
    public static final Uri URI_ORDER = URI_BASE.buildUpon().appendPath("order").build();
    public static final Uri URI_TYPE = URI_BASE.buildUpon().appendPath("type").build();
    public static final Uri URI_PRODUCT = URI_BASE.buildUpon().appendPath("product").build();
}
