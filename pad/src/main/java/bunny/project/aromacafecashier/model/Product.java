package bunny.project.aromacafecashier.model;

import android.database.Cursor;

import bunny.project.aromacafecashier.common.QueryManager;

/**
 * Created by bunny on 17-3-14.
 */

public class Product {
    private int id;
    private int typeId;
    private String type;
    private String name;
    private float price;
    private byte[] image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Product fromCursor(Cursor cursor) {
        int id = cursor.getInt(QueryManager.INDEX_PRODUCT_ID);
        int typeId = cursor.getInt(QueryManager.INDEX_PRODUCT_TYPE_ID);
        String type = cursor.getString(QueryManager.INDEX_PRODUCT_TYPE);
        String name = cursor.getString(QueryManager.INDEX_PRODUCT_NAME);
        float price = cursor.getFloat(QueryManager.INDEX_PRODUCT_PRICE);
        byte[] imgBytes = cursor.getBlob(QueryManager.INDEX_PRODUCT_IMAGE);

        Product product = new Product();
        product.setId(id);
        product.setTypeId(typeId);
        product.setType(type);
        product.setPrice(price);
        product.setName(name);
        product.setImage(imgBytes);

        return product;
    }
}
