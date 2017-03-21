package bunny.project.aromacafecashier;

import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bunny.project.aromacafecashier.model.OrderInfo;
import bunny.project.aromacafecashier.model.OrderItemInfo;
import bunny.project.aromacafecashier.provider.AccsProvider;
import bunny.project.aromacafecashier.provider.AccsTables;
import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * Created by bunny on 17-3-15.
 */
//TODO 按键0在实收为空的时候，显示成disable状态
public class OrderConfirmDialogFragment extends DialogFragment {
    private static final String TAG = OrderConfirmDialogFragment.class.getSimpleName();
    private static final int TOKEN_INSERT_ORDER = 1;

    private TextView mTotalCashView;
    private EditText mPayinView;
    private TextView mChargeView;
    private Button mConfirmBtn;
    private Button mTempOrderBtn;
    private Button mCanceltn;

    private static SparseIntArray DIGIT_ID_TO_EVENT_KEY = new SparseIntArray();

    static {
        DIGIT_ID_TO_EVENT_KEY.put(R.id.one, KeyEvent.KEYCODE_1);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.two, KeyEvent.KEYCODE_2);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.three, KeyEvent.KEYCODE_3);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.four, KeyEvent.KEYCODE_4);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.five, KeyEvent.KEYCODE_5);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.six, KeyEvent.KEYCODE_6);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.seven, KeyEvent.KEYCODE_7);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.eight, KeyEvent.KEYCODE_8);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.nine, KeyEvent.KEYCODE_9);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.zero, KeyEvent.KEYCODE_0);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.dot, KeyEvent.KEYCODE_NUMPAD_DOT);
        DIGIT_ID_TO_EVENT_KEY.put(R.id.del, KeyEvent.KEYCODE_DEL);
    }

    private ArrayList<OrderItemInfo> mOrderItems;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_temp_order) {
                v.setEnabled(false);
                new InsertOrderTask(getActivity().getContentResolver(), mOrderId, false).execute();
            } else if (v.getId() == R.id.btn_confirm) {
                v.setEnabled(false);
                new InsertOrderTask(getActivity().getContentResolver(), mOrderId, true).execute();
            } else if (v.getId() == R.id.btn_cancel) {
                dismiss();
            } else {
                int keyCode = DIGIT_ID_TO_EVENT_KEY.get(v.getId());
                mPayinView.onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            }

        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String totalCashSting = mTotalCashView.getText().toString();
            String payInString = mPayinView.getText().toString();

            float totalCash = TextUtils.isEmpty(totalCashSting) ? 0f : Float.valueOf(totalCashSting);
            float payIn = TextUtils.isEmpty(payInString) ? 0f : Float.valueOf(payInString);
            float charge = payIn - totalCash;

            mChargeView.setText(String.valueOf(charge > 0 ? charge : 0));

            mConfirmBtn.setEnabled(charge >= 0);
        }
    };
    private OrderListener mOrderListener;
    private int mOrderId = 0;


    public void setOrderListener(OrderListener listener) {
        this.mOrderListener = listener;
    }

    public interface OrderListener {
        void onOrderComplete();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    return true;
//                }
//                return false;
//            }
//        });

        View view = inflater.inflate(R.layout.order_comfirm, null);

        initView(view);

        return view;
    }

    @NonNull
    private View initView(View view) {
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        mTotalCashView = (TextView) view.findViewById(R.id.total_cash);
        mPayinView = (EditText) view.findViewById(R.id.pay_in);
        mChargeView = (TextView) view.findViewById(R.id.charge);
        mConfirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        mCanceltn = (Button) view.findViewById(R.id.btn_cancel);
        mTempOrderBtn = (Button) view.findViewById(R.id.btn_temp_order);

        view.findViewById(R.id.one).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.two).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.three).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.four).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.five).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.six).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.seven).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.eight).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.nine).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.zero).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.dot).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.del).setOnClickListener(mOnClickListener);

        Bundle data = getArguments();
        mOrderItems = data.getParcelableArrayList(IntentKeys.ORDER_ITEM_LIST);
        mOrderId = data.getInt(IntentKeys.ORDER_ID);

        MyLog.i(TAG, "orderId:" + mOrderId);

        float totalCash = getTotalCash(mOrderItems);
        mTotalCashView.setText(String.valueOf(totalCash));

        mConfirmBtn.setOnClickListener(mOnClickListener);
        mCanceltn.setOnClickListener(mOnClickListener);
        mTempOrderBtn.setOnClickListener(mOnClickListener);

        mPayinView.addTextChangedListener(mTextWatcher);

        return view;
    }

    private float getTotalCash(List<OrderItemInfo> items) {
        float totalCash = 0f;
        for (OrderItemInfo item : items) {
            totalCash += item.getCount() * item.getProductPrice();
        }
        return totalCash;
    }

//    @Override
//    public void dismiss() {
//        super.dismiss();
//
//    }

    private class InsertOrderTask extends AsyncTask<Boolean, Void, Integer> {

        private ContentResolver mResolver;
        private int mOrderId;
        private boolean mIsPayed;
        private ContentProviderResult[] mResults;

        public InsertOrderTask(ContentResolver resolver, int orderId, boolean isPayed) {
            mResolver = resolver;
            mOrderId = orderId;
            mIsPayed = isPayed;
        }

        @Override
        protected Integer doInBackground(Boolean[] params) {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            if (mOrderId <= 0) {
                buildNewInsert(operations, mIsPayed);
            } else {
                buildUpdateOrder(operations, mOrderId, mIsPayed);
            }


            try {
                mResults = mResolver.applyBatch(AccsProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }


            return null;
        }

        private void buildUpdateOrder(ArrayList<ContentProviderOperation> operations, int orderId, boolean isPayed) {
            ContentProviderOperation updatePeration = ContentProviderOperation.newUpdate(QueryManager.URI_ORDER)
                    .withValue(AccsTables.Order.COL_PAY_TIME, System.currentTimeMillis())
                    .withValue(AccsTables.Order.COL_PAYED, isPayed ? 1 : 0)
                    .withSelection(AccsTables.Order._ID + "=?", new String[]{String.valueOf(orderId)})
                    .build();
            operations.add(updatePeration);

            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(QueryManager.URI_ORDER_DETAIL)
                    .withSelection(AccsTables.OrderDetail.COL_ORDER_ID + "=?", new String[]{String.valueOf(orderId)})
                    .build();
            operations.add(deleteOperation);

            for (OrderItemInfo item : mOrderItems) {
                operations.add(item.toInertOperationWithOrderId(mOrderId));
            }
        }

        /**
         * 生成新的付款订单或挂单。
         */

        private void buildNewInsert(ArrayList<ContentProviderOperation> operations, boolean isPayed) {
            operations.add(OrderInfo.getNewInsertOperation(isPayed));

            for (OrderItemInfo item : mOrderItems) {
                operations.add(item.toInsertContentProviderOperation(0));
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            for (ContentProviderResult result : mResults) {
                MyLog.i(TAG, "result:" + result.toString());
            }
            dismiss();
            mOrderListener.onOrderComplete();
        }
    }


    private class OrderAsyncManager extends AsyncQueryHandler {

        public OrderAsyncManager(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            super.onUpdateComplete(token, cookie, result);
        }
    }
}
