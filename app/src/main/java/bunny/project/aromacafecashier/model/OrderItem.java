package bunny.project.aromacafecashier.model;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import bunny.project.aromacafecashier.QueryManager;
import bunny.project.aromacafecashier.provider.AccsTables;

import static android.support.v7.widget.AppCompatDrawableManager.get;

/**
 * Created by bunny on 17-3-15.
 */

public class OrderItem implements Parcelable {

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

    public ContentProviderOperation toInsertContentProviderOperation(int backRef) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QueryManager.URI_ORDER_DETAIL);
        builder.withValueBackReference(AccsTables.OrderDetail.COL_ORDER_ID, backRef);
        builder.withValues(toContentValues());
        builder.withYieldAllowed(true);
        return builder.build();
    }


    public OrderItem() {

    }

    protected OrderItem(Parcel in) {
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


    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
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