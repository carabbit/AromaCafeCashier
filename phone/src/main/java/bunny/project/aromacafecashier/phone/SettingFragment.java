package bunny.project.aromacafecashier.phone;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.lantransport.LanTransportHelper;
import bunny.project.aromacafecashier.lantransport.TransportCallback;
import bunny.project.aromacafecashier.lantransport.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss");

    private View mContentView;
    private TextView mCommondDisplay;
    private TextView mLastSyncTimeView;
    private TextView mLocalTimeView;
    private Button mBtnSync;


    private TransportCallback mCallback = new TransportCallback() {
        @Override
        public void progress(int res, String text) {
            if (!isAdded()) {
                return;
            }

            String formatText;
            if (TextUtils.isEmpty(text)) {
                formatText = getString(res);
            } else {
                formatText = getString(res, text);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("\n> ");
            sb.append(mTimeFormat.format(new Date(System.currentTimeMillis())));
            sb.append(" ");
            sb.append(formatText);

            mCommondDisplay.append(sb.toString());
        }

        @Override
        public void transportComplete() {
            if (!isAdded()) {
                return;
            }

            setTimeView();
            mBtnSync.setEnabled(true);
        }

        @Override
        public void notification(int res, String text) {

        }
    };

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MLog.i("", "action:" + action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                updateWifiStateView();
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_sync) {
//                LanTransportHelper.getInstance().sync(mCallback);
                v.setEnabled(false);
//                LanTransportHelper.getInstance().startUdpReceiver(mCallback);
                LanTransportHelper.getInstance().startTcpSenderForPhone("", mCallback);
            }
        }
    };

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        MLog.i("xxx", "newInstance");
        SettingFragment fragment = new SettingFragment();
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
        MLog.i("xxx", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MLog.i("xxx", "onCreateView");
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_setting, container, false);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mCommondDisplay == null) {
            mCommondDisplay = (TextView) view.findViewById(R.id.command_display);
            mLastSyncTimeView = (TextView) view.findViewById(R.id.last_sync_time);
            mLocalTimeView = (TextView) view.findViewById(R.id.local_ip);
            mBtnSync = (Button) view.findViewById(R.id.btn_sync);
            mBtnSync.setOnClickListener(mOnClickListener);

            initView();
        }
        MLog.i("xxx", "onViewCreated");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registerWifiReceiver(context);
    }


    private void registerWifiReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mWifiReceiver != null) {
            getContext().unregisterReceiver(mWifiReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LanTransportHelper.getInstance().closeSync();
    }

    private void initView() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        long lastSyncTime = pref.getLong("last_sync_time", -1);
        if (lastSyncTime > 0) {
            mLastSyncTimeView.setText(getString(R.string.last_sync_time, mDateFormat.format(new Date(lastSyncTime))));
        } else {
            mLastSyncTimeView.setText(R.string.not_sync);
        }

        updateWifiStateView();
    }

    private void updateWifiStateView() {
        mLocalTimeView.setText(getString(R.string.local_ip, Utils.getLocalHostIp()));

        //如果非局域网，同步按钮禁用。
        mBtnSync.setEnabled(Utils.isWifiNetwork(getContext()));
    }


    private void setTimeView() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        long currentTime = System.currentTimeMillis();
        pref.edit().putLong("last_sync_time", currentTime).commit();
        mLastSyncTimeView.setText(getString(R.string.last_sync_time, mDateFormat.format(new Date(currentTime))));
    }
}
