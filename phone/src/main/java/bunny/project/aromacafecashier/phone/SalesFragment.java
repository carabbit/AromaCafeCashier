package bunny.project.aromacafecashier.phone;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.common.model.OrderInfo;
import bunny.project.aromacafecashier.phone.view.SalesItemView;
import bunny.project.aromacafecashier.phone.view.SalesSummaryView;

public class SalesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SalesFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mBtnToday;
    private Button mBtnMonth;
    private Button mBtnCustom;
    private SalesSummaryView mSalesSummaryView;
    private ListView mListView;
    private DataAccessHelper mDataAccessHelper;
    private DataAccessHelper.AccessCallback mQueryCallback = new DataAccessHelper.AccessCallback() {
        @Override
        public void onQueryComplete(int token, Cursor cursor) {
            if (token == DataAccessHelper.TOKEN_QUERY_TODAY_PRODUCTS) {
                mSalesSummaryView.setSales(cursor);
            } else if (token == DataAccessHelper.TOKEN_QUERY_TODAY_ORDERS) {
                mAdapter.changeCursor(cursor);
                mSalesSummaryView.setOrderInfo(cursor);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_today:
                    mDataAccessHelper.getTodaySalesSummary();
                    break;
                case R.id.btn_month:
                    mDataAccessHelper.getMonthSalesSummary();
                    break;
                case R.id.btn_custom:
                    break;
                default:
                    break;
            }
        }
    };
    private SalesAdapter mAdapter;

    public SalesFragment() {
        // Required empty public constructor
        MLog.i(TAG, "SalesFragment");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SalesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SalesFragment newInstance(String param1, String param2) {
        SalesFragment fragment = new SalesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDataAccessHelper = new DataAccessHelper(context.getContentResolver(), mQueryCallback);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBtnToday = (Button) view.findViewById(R.id.btn_today);
        mBtnMonth = (Button) view.findViewById(R.id.btn_month);
        mBtnCustom = (Button) view.findViewById(R.id.btn_custom);
        mSalesSummaryView = (SalesSummaryView) view.findViewById(R.id.sales_summary);
        mListView = (ListView) view.findViewById(R.id.list);

        mAdapter = new SalesAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mBtnToday.setOnClickListener(mOnClickListener);
        mBtnMonth.setOnClickListener(mOnClickListener);
        mBtnCustom.setOnClickListener(mOnClickListener);
    }

    private class SalesAdapter extends CursorAdapter {
        public SalesAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.sales_item, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (!(view instanceof SalesItemView)) {
                return;
            }
            OrderInfo info = OrderInfo.fromCusor(cursor);
            SalesItemView itemView = (SalesItemView) view;
            itemView.setOrderId(info.getId());
            itemView.setOrderTime(info.getDate());
            itemView.setPayTime(info.getPayTime());
            itemView.setOrderStatus(info.getOrderStatus());
            itemView.setPayStatus(info.getPayed());
        }
    }
}
