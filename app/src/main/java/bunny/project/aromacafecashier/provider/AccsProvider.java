package bunny.project.aromacafecashier.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;

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
        sURIMatcher.addURI(AUTHORITY, "order_fragment", ORDER);
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {
        int match = sURIMatcher.match(uri);
        String tableName = "";
        switch (match) {
            case PRODUCT:
                tableName = AccsTables.Views.VIEW_PRODUCT;
                break;
            case ORDER:
                tableName = AccsTables.Order.TABLE_NAME;
                break;
            case ORDER_DETAIL:
                tableName = AccsTables.OrderDetail.TABLE_NAME;
                break;
            case TYPE:
                tableName = AccsTables.Type.TABLE_NAME;
                break;
        }
        return mDb.query(tableName, projection, selection, selectionArgs, null, null, orderBy);
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
            case TYPE:
                tableName = AccsTables.Type.TABLE_NAME;
                break;
            default:
                throw new RuntimeException("无法处理uri：" + uri);
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sURIMatcher.match(uri);
        String tableName = "";
        switch (match) {
            case PRODUCT:
                tableName = AccsTables.Product.TABLE_NAME;
                break;
//            case ORDER_ITEM_LIST:
//                tableName = AccsTables.OrderInfo.TABLE_NAME;
//                break;
            case ORDER_DETAIL:
                tableName = AccsTables.OrderDetail.TABLE_NAME;
                break;
            case TYPE:
                tableName = AccsTables.Type.TABLE_NAME;
                break;
            default:
                throw new RuntimeException("无法处理uri：" + uri);
        }

        return mDb.delete(tableName, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sURIMatcher.match(uri);
        String tableName = "";
        switch (match) {
//            case PRODUCT:
//                tableName = AccsTables.Product.TABLE_NAME;
//                break;
//            case ORDER_ITEM_LIST:
//                tableName = AccsTables.OrderInfo.TABLE_NAME;
//                break;
            case ORDER:
                tableName = AccsTables.Order.TABLE_NAME;
                break;
//            case TYPE:
//                tableName = AccsTables.Type.TABLE_NAME;
//                break;
            default:
                throw new RuntimeException("无法处理uri：" + uri);
        }

        return mDb.update(tableName, contentValues, selection, selectionArgs);
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        mDb.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            mDb.setTransactionSuccessful();
            return results;
        } finally {
            mDb.endTransaction();
        }
    }
}
