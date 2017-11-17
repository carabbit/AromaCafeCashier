package bunny.project.aromacafecashier.common;

import android.util.Log;

/**
 * Created by bunny on 17-11-10.
 */

public class MLog {
    private static final String TAG = "aroma";

    public static void i(String tag, String msg) {
        Log.i(TAG, tag + " - " + msg);
    }
}
