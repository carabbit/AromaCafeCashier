package bunny.project.aromacafecashier;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bunny.project.aromacafecashier.utility.IntentKeys;

/**
 * Created by bunny on 17-3-27.
 */

public class OrderDeleteDialogFragment extends DialogFragment {

    public static interface OnClickListener {
        void onConfirmClicked();
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_delete_confirm_dialog_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onConfirmClicked();
                }
                dismiss();
            }
        });

        Bundle arguments = getArguments();
        boolean isDeleteOrder = arguments.getBoolean(IntentKeys.DELETE);

        TextView textView = (TextView) view.findViewById(R.id.text);

        textView.setText(isDeleteOrder ? R.string.delete_order_confirm : R.string.recover_order_confirm);
    }
}
