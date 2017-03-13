package bunny.project.aromacafecashier.provider;

/**
 * Created by bunny on 2017/3/11.
 */

public class AccsTables {
    public static interface BaseColumn {
        public static final String COL_ID = "id";
    }

    public static class Product implements BaseColumn {
        public static String TABLE_NAME = "product";

        public static final String COL_TYPE_ID = "type_id";
        public static final String COL_NAME = "name";
        public static final String COL_PRICE = "price";
        public static final String COL_IMAGE = "image";

        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append("id INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_NAME + " TEXT")
                .append("," + COL_TYPE_ID + " INTEGER")
                .append("," + COL_IMAGE + " TEXT")
                .append("," + COL_PRICE + " FLOAT")
                .append(");")
                .toString();

    }

    public static class ProductType implements BaseColumn {
        public static String TABLE_NAME = "product_type";
        public static final String COL_NAME = "name";
        public static final String COL_SORT_ID = "sort_id";
        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append("id INTEGER PRIMARY KEY AUTOINCREMENT")
                .append("," + COL_NAME + " TEXT")
                .append("," + COL_SORT_ID + " INTEGER")
                .append(");")
                .toString();

    }


    public static class Order implements BaseColumn {
        public static String TABLE_NAME = "[order]";
        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append("id INTEGER PRIMARY KEY AUTOINCREMENT")
                .append(",date INTEGER")
                .append(",payed INTEGER")
                .append(");")
                .toString();
    }

    public static class OrderDetail implements BaseColumn {
        public static String TABLE_NAME = "order_detail";
        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append("id INTEGER PRIMARY KEY AUTOINCREMENT")
                .append(",order_id INTEGER")
                .append(",count INTEGER ")
                .append(");")
                .toString();
    }
}
