package bunny.project.aromacafecashier;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bunny on 17-3-26.
 */

public class PasswordDialogFragment extends DialogFragment {
    private EditText mPasswordView;
    private Button mBtnConfirm;
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

    private final static String PASSWORD = "0501";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_confirm) {
                if (PASSWORD.equals(mPasswordView.getText().toString())) {
                    dismiss();
                } else {
                    mPasswordView.setText("");
                    Toast.makeText(getActivity(), R.string.password_wrong, Toast.LENGTH_SHORT).show();
                }

            } else {
                int keyCode = DIGIT_ID_TO_EVENT_KEY.get(v.getId());
                mPasswordView.onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_dialog_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPasswordView = (EditText) view.findViewById(R.id.password_view);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);

        mBtnConfirm.setOnClickListener(mOnClickListener);
        mPasswordView.setOnClickListener(mOnClickListener);
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
        view.findViewById(R.id.del).setOnClickListener(mOnClickListener);
    }
}
