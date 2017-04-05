package bunny.project.aromacafecashier.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bunny on 2017/3/11.
 */

public class AccsDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "accs.db";
    public static final int DB_VERSION = 3;

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + AccsTables.Order.TABLE_NAME + " ADD " + AccsTables.Order.COL_STATUS + " INTEGER DEFAULT 0;");
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + AccsTables.Order.TABLE_NAME + " ADD " + AccsTables.Order.COL_DISCOUNT + " FLOAT DEFAULT 1.0;");
            db.execSQL("ALTER TABLE " + AccsTables.OrderDetail.TABLE_NAME + " ADD " + AccsTables.OrderDetail.COL_DISCOUNT + " FLOAT DEFAULT 1.0;");
        }
    }
}
