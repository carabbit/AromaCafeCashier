package bunny.project.aromacafecashier.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bunny on 2017/3/11.
 */

public class AccsDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "accs.db";
    public static final int DB_VERSION = 1;

    public AccsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AccsTables.Product.CREATE_TABLE);
        sqLiteDatabase.execSQL(AccsTables.Type.CREATE_TABLE);
        sqLiteDatabase.execSQL(AccsTables.Order.CREATE_TABLE);
        sqLiteDatabase.execSQL(AccsTables.OrderDetail.CREATE_TABLE);
        sqLiteDatabase.execSQL(AccsTables.Views.CREATE_PRODUCT_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
