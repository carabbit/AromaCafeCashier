package bunny.project.aromacafecashier;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import bunny.project.aromacafecashier.common.AccsUris;
import bunny.project.aromacafecashier.model.ProductType;
import bunny.project.aromacafecashier.common.provider.AccsTables;
import bunny.project.aromacafecashier.utility.BitmapTool;

/**
 * 商品编辑界面，用于商品新增，或编辑。
 * Created by bunny on 2017/3/12.
 */

public class ProductEditorActivity extends Activity {
    public static final String TAG = ProductEditorActivity.class.getSimpleName();


    private Spinner mTypeSpinner;
    private Button mBtnAddType;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private EditText mEdtName;
    private EditText mEdtPrice;
    private ImageView mImgProduct;

    private static final int TOKEN_INSERT_PRODUCT = 1;
    private static final int TOKEN_INSERT_TYPE = 2;
    private static final int TOKEN_QUERY_TYPE = 3;
    private static final int TOKEN_QUERY_ALL_TYPE = 4;
    private static final int TOKEN_QUERY_PRODUCT = 5;

    private static final int DIALOG_CREATE_TYPE = 1;

    private static final int REQUEST_OPEN_GALLERY = 1;
    private static final int REQUEST_PHOTO_CUT = 2;// 结果

    private static final int TAG_PRODUCT_IMG_EXIST = 1;
    private static final int TAG_PRODUCT_IMG_UPDATE = 2;// 结果

    private QueryProductHandler mQueryProductHandler;
    private ArrayAdapter<ProductType> mSpinnerAdapter;

    private class QueryProductHandler extends AsyncQueryHandler {

        public QueryProductHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            mBtnCancel.setEnabled(true);

            if (uri == null) {
                showToast(R.string.create_fail);
            } else {
                showToast(R.string.create_success);
            }

            if (token == TOKEN_INSERT_PRODUCT) {
                long id = ContentUris.parseId(uri);
                if (id > 0) {
                    finish();
                }
            } else if (token == TOKEN_INSERT_TYPE) {
                long id = ContentUris.parseId(uri);
                MyLog.i(TAG, "insert product type id:" + id);
                if (id > 0) {
                    ProductType productType = new ProductType();
                    productType.setId((int) id);
                    productType.setName((String) cookie);
                    mSpinnerAdapter.add(productType);
                    mSpinnerAdapter.notifyDataSetChanged();

                    int position = mSpinnerAdapter.getPosition(productType);
                    mTypeSpinner.setSelection(position);
                }
            }
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == TOKEN_QUERY_TYPE) {
                if (cursor != null && cursor.getCount() > 0) {
                    showToast(R.string.type_exist);
                } else {
                    if (cookie != null && cookie instanceof String) {
                        insertProductType((String) cookie);
                    }
                }
            } else if (token == TOKEN_QUERY_ALL_TYPE) {
                mSpinnerAdapter.clear();
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        ProductType productType = new ProductType();
                        productType.setId(cursor.getInt(0));
                        productType.setName(cursor.getString(1));
                        mSpinnerAdapter.add(productType);
                    }

                    mSpinnerAdapter.notifyDataSetChanged();
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_product);

        mQueryProductHandler = new QueryProductHandler(getContentResolver());

        initViews();
    }

    private void initViews() {
        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);
        mEdtName = (EditText) findViewById(R.id.edit_name);
        mEdtPrice = (EditText) findViewById(R.id.edit_price);
        mBtnAddType = (Button) findViewById(R.id.btn_add_type);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mImgProduct = (ImageView) findViewById(R.id.img_product);

        mBtnAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_CREATE_TYPE);
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTypeSpinner.getSelectedItemId() < 0) {
                    showToast(R.string.select_product_type);
                    return;
                } else if (TextUtils.isEmpty(mEdtName.getText().toString())) {
                    showToast(R.string.input_product_name);
                    return;
                } else if (TextUtils.isEmpty(mEdtPrice.getText().toString())) {
                    showToast(R.string.input_product_pirce);
                    return;
                } else if (mImgProduct.getTag() == null) {
                    showToast(R.string.select_product_image);
                    return;
                }

                mBtnCancel.setEnabled(false);

                saveProduct();
            }
        });

        mImgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_OPEN_GALLERY);
            }
        });

        mSpinnerAdapter = new ArrayAdapter<ProductType>(this, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(R.layout.product_type_spinner_dropdown_item);
        mTypeSpinner.setAdapter(mSpinnerAdapter);
    }

    private void showToast(int resId) {
        Toast.makeText(ProductEditorActivity.this, resId, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String text) {
        Toast.makeText(ProductEditorActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void saveProduct() {
        ContentValues values = new ContentValues();
        Object selectedItem = mTypeSpinner.getSelectedItem();
        values.put(AccsTables.Product.COL_TYPE_ID, ((ProductType) selectedItem).getId());
        values.put(AccsTables.Product.COL_NAME, mEdtName.getText().toString());
        values.put(AccsTables.Product.COL_PRICE, Float.valueOf(mEdtPrice.getText().toString()));

        if (mImgProduct.getTag() != null && ((Integer) mImgProduct.getTag() == TAG_PRODUCT_IMG_UPDATE)) {
            values.put(AccsTables.Product.COL_IMAGE, BitmapTool.drawable2Bytes(mImgProduct.getDrawable()));
        }
        mQueryProductHandler.startInsert(TOKEN_INSERT_PRODUCT, null, AccsUris.URI_PRODUCT, values);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryAllProductType();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_CREATE_TYPE) {
            final EditText edtType = new EditText(this);
            edtType.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.product_type);
            builder.setView(edtType);
            builder.setPositiveButton(R.string.comfirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String typeStr = edtType.getText().toString();
                    if (TextUtils.isEmpty(typeStr)) {
                        Toast.makeText(ProductEditorActivity.this, R.string.input_product_type, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        queryProductType(typeStr);
                    }
                }
            });
            return builder.create();
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                crop(data.getData());
            }
        } else if (requestCode == REQUEST_PHOTO_CUT) {
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                mImgProduct.setImageDrawable(roundedBitmapDrawable);
                mImgProduct.setTag(TAG_PRODUCT_IMG_UPDATE);
            }
        }
    }

    private void queryAllProductType() {
        String[] projection = new String[]{AccsTables.Type._ID, AccsTables.Type.COL_NAME};
        mQueryProductHandler.startQuery(TOKEN_QUERY_ALL_TYPE, null, AccsUris.URI_TYPE, projection, null, null, null);
    }

    private void queryProductType(String type) {
        String selection = AccsTables.Type.COL_NAME + "=?";
        String[] agrs = new String[]{type};
        mQueryProductHandler.startQuery(TOKEN_QUERY_TYPE, type, AccsUris.URI_TYPE, new String[]{AccsTables.Type._ID}, selection, agrs, null);
    }

    private void insertProductType(String type) {
        ContentValues values = new ContentValues();
        values.put(AccsTables.Type.COL_NAME, type);
        mQueryProductHandler.startInsert(TOKEN_INSERT_TYPE, type, AccsUris.URI_TYPE, values);
    }

    /*
      * 剪切图片
      */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "PNG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, REQUEST_PHOTO_CUT);
    }
}