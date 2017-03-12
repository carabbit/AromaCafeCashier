package bunny.project.aromacafecashier.provider;

/**
 * Created by bunny on 2017/3/11.
 */

public class AccsTables {
    public static class Product {
        public static String TABLE_NAME = "product";
        public static String CREATE_TABLE = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append("id INTEGER PRIMARY KEY AUTOINCREMENT")
                .append(",name TEXT")
                .append(",type INTEGER")
                .append(",image BLOB")
                .append(",price FLOAT")
                .append(");")
                .toString();

    }

    public static class Order {
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

    public static class OrderDetail {
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
