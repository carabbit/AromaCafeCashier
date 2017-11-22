package bunny.project.aromacafecashier;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.lantransport.Constant;
import bunny.project.aromacafecashier.view.TitleGroupView;

/**
 * Created by bunny on 17-11-20.
 */
public class SyncDialogFragment extends DialogFragment {

    private TextView mScreen;
    private Button mBtnQuit;
    private Button mBtnClear;
    private Button mBtnRefresh;
    private TitleGroupView mWifiSectionView;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_quit:
                    dismiss();
                    break;
                case R.id.btn_clear: {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    pref.edit().putString(Constant.PREF_SERVER_LOG, "").commit();
                    mScreen.setText("");
                    break;
                }
                case R.id.btn_refresh: {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String log = pref.getString(Constant.PREF_SERVER_LOG, "");
                    mScreen.setText(log);
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sync_dialog_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScreen = (TextView) view.findViewById(R.id.screen);
        mBtnQuit = (Button) view.findViewById(R.id.btn_quit);
//        mWifiSectionView = (TitleGroupView) view.findViewById(R.id.section_wifi);
        mBtnClear = (Button) view.findViewById(R.id.btn_clear);
        mBtnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        mBtnQuit.setOnClickListener(mClickListener);
        mBtnClear.setOnClickListener(mClickListener);
        mBtnRefresh.setOnClickListener(mClickListener);

//        mWifiSectionView.setSectionTitle(R.string.add);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String log = pref.getString(Constant.PREF_SERVER_LOG, "");

        mScreen.setText(log);
    }
}
