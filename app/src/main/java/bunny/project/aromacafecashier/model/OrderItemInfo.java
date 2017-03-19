package bunny.project.aromacafecashier.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import bunny.project.aromacafecashier.QueryManager;
import bunny.project.aromacafecashier.provider.AccsTables;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderItemInfo implements Parcelable {

    private int orderId;
    private int productId;
    private int count;
    private float price;
    private String productName;


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(AccsTables.OrderDetail.COL_COUNT, getCount());
        values.put(AccsTables.OrderDetail.COL_ORDER_ID, getOrderId());
        values.put(AccsTables.OrderDetail.COL_PRODUCT_ID, getProductId());
        values.put(AccsTables.OrderDetail.COL_PRODUCT_NAME, getProductName());
        values.put(AccsTables.OrderDetail.COL_PRODUCT_PRICE, getProductPrice());

        return values;
    }

    public ContentProviderOperation toInertOperationWithOrderId(int orderId) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QueryManager.URI_ORDER_DETAIL);
        builder.withValue(AccsTables.OrderDetail.COL_ORDER_ID, orderId);
        buildInsertPart(builder);
        return builder.build();
    }

    public ContentProviderOperation toInsertContentProviderOperation(int backRef) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QueryManager.URI_ORDER_DETAIL);
        builder.withValueBackReference(AccsTables.OrderDetail.COL_ORDER_ID, backRef);
//        builder.withValues(toContentValues());
        buildInsertPart(builder);
        return builder.build();
    }

    private void buildInsertPart(ContentProviderOperation.Builder builder) {
        builder.withValue(AccsTables.OrderDetail.COL_COUNT, getCount());
        builder.withValue(AccsTables.OrderDetail.COL_PRODUCT_ID, getProductId());
        builder.withValue(AccsTables.OrderDetail.COL_PRODUCT_NAME, getProductName());
        builder.withValue(AccsTables.OrderDetail.COL_PRODUCT_PRICE, getProductPrice());
        builder.withYieldAllowed(true);
    }

    public static OrderItemInfo fromCursor(Cursor cursor) {
        OrderItemInfo item = new OrderItemInfo();
        item.setProductName(cursor.getString(QueryManager.INDEX_ORDER_DETAIL_PRODUCT_NAME));
        item.setProductId(cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_PRODUCT_ID));
        item.setCount(cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_COUNT));
        item.setOrderId(cursor.getInt(QueryManager.INDEX_ORDER_DETAIL_ORDER_ID));
        item.setProductPrice(cursor.getFloat(QueryManager.INDEX_ORDER_DETAIL_PRODUCT_PRICE));
        return item;
    }


    public OrderItemInfo() {

    }

    protected OrderItemInfo(Parcel in) {
        orderId = in.readInt();
        productId = in.readInt();
        count = in.readInt();
        price = in.readFloat();
        productName = in.readString();
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getProductPrice() {
        return price;
    }

    public void setProductPrice(float privce) {
        this.price = privce;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<OrderItemInfo> CREATOR = new Creator<OrderItemInfo>() {
        @Override
        public OrderItemInfo createFromParcel(Parcel in) {
            return new OrderItemInfo(in);
        }

        @Override
        public OrderItemInfo[] newArray(int size) {
            return new OrderItemInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderId);
        dest.writeInt(productId);
        dest.writeInt(count);
        dest.writeFloat(price);
        dest.writeString(productName);
    }
}