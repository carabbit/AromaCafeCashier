package bunny.project.aromacafecashier.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * 数据provider
 * Created by bunny on 2017/3/11.
 */

public class AccsProvider extends ContentProvider {
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    private static final int PRODUCT = 1;
    private static final int ORDER = 2;
    private static final int ORDER_DETAIL = 3;
    private static final int TYPE = 4;

    public static final String AUTHORITY = "bunny.project.aromacafecashier";

    static {
        sURIMatcher.addURI(AUTHORITY, "product", PRODUCT);
        sURIMatcher.addURI(AUTHORITY, "order", ORDER);
        sURIMatcher.addURI(AUTHORITY, "order_detail", ORDER_DETAIL);
        sURIMatcher.addURI(AUTHORITY, "type", TYPE);
    }


    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mDb = new AccsDbHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sURIMatcher.match(uri);
        String tableName = "";
        switch (match) {
            case PRODUCT:
                tableName = AccsTables.Product.TABLE_NAME;
                break;
            case ORDER:
                tableName = AccsTables.Order.TABLE_NAME;
                break;
            case ORDER_DETAIL:
                tableName = AccsTables.OrderDetail.TABLE_NAME;
                break;
        }

        if (TextUtils.isEmpty(tableName)) {
            return null;
        }

        long result = mDb.insert(tableName, null, contentValues);

        if (result > 0) {
            return ContentUris.withAppendedId(uri, result);
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}