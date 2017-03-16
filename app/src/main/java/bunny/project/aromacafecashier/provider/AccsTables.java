package bunny.project.aromacafecashier.provider;

import android.provider.BaseColumns;

/**
 * Created by bunny on 2017/3/11.
 */

public class AccsTables {

    public static class Product implements BaseColumns {
        public static String TABLE_NAME = "product";

        public static final String COL_TYPE_ID = "type_id";
        public static final String COL_NAME = "name";
        public static final String COL_PRICE = "price";
        public static final String COL_IMAGE = "image";

        public static final String COL_CONCRETE_ID = TABLE_NAME + "." + _ID;
        public static final String COL_CONCRETE_TYPE_ID = TABLE_NAME + "." + COL_TYPE_ID;
        public static final String COL_CONCRETE_TYPE_NAME = TABLE_NAME + "." + COL_NAME;

        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_NAME + " TEXT")
                .append("," + COL_TYPE_ID + " INTEGER")
                .append("," + COL_IMAGE + " TEXT")
                .append("," + COL_PRICE + " FLOAT")
                .append(");")
                .toString();

    }

    public static class ProductType implements BaseColumns {
        public static String TABLE_NAME = "product_type";

        public static final String COL_NAME = "name";
        public static final String COL_SORT_ID = "sort_id";
        public static final String COL_CONCRETE_ID = TABLE_NAME + "." + _ID;
        public static final String COL_CONCRETE_NAME = TABLE_NAME + "." + COL_NAME;

        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_NAME + " TEXT")
                .append("," + COL_SORT_ID + " INTEGER")
                .append(");")
                .toString();

    }


    public static class Order implements BaseColumns {
        public static String TABLE_NAME = "[order]";

        public static final String COL_DATE = "[date]";
        public static final String COL_PAYED = "payed";
        public static final String COL_PAY_TIME = "pay_time";

        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_DATE + " LONG")
                .append("," + COL_PAYED + " INTEGER")
                .append("," + COL_PAY_TIME + " LONG")
                .append(");")
                .toString();
    }

    public static class OrderDetail implements BaseColumns {
        public static String TABLE_NAME = "order_detail";

        public static final String COL_ORDER_ID = "order_id";
        public static final String COL_PRODUCT_ID = "product_id";
        public static final String COL_PRODUCT_NAME = "product_name";
        public static final String COL_PRODUCT_PRICE = "product_price";
        public static final String COL_COUNT = "count";

        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_ORDER_ID + " INTEGER")
                .append("," + COL_PRODUCT_ID + " INTEGER")
                .append("," + COL_PRODUCT_NAME + " TEXT")
                .append("," + COL_PRODUCT_PRICE + " TEXT")
                .append("," + COL_COUNT + " INTEGER ")
                .append(");")
                .toString();
    }

    public static class Views {
        public static final String COL_VIEW_TYPE = "type";

        public static final String VIEW_PRODUCT = "view_product";

        public static final String CREATE_PRODUCT_VIEW = "CREATE VIEW " + VIEW_PRODUCT + " AS "
                + "SELECT "
                + Product.COL_CONCRETE_ID + " AS " + Product._ID
                + "," + Product.COL_CONCRETE_TYPE_NAME + " AS " + Product.COL_NAME
                + "," + Product.COL_TYPE_ID
                + "," + Product.COL_PRICE
                + "," + Product.COL_IMAGE
                + "," + ProductType.COL_CONCRETE_NAME + " AS " + COL_VIEW_TYPE
                + " FROM " + Product.TABLE_NAME
                + " LEFT JOIN " + ProductType.TABLE_NAME + " ON " + Product.COL_CONCRETE_TYPE_ID + " = " + ProductType.COL_CONCRETE_ID;
    }
}
