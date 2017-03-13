package bunny.project.aromacafecashier;

import android.util.Log;

/**
 * Created by bunny on 17-3-13.
 */

public class MyLog {
    private static final String TAG = "bunny";

    public static void i(String tag, String text) {
        Log.i(TAG, "[" + tag + "]" + text);
    }
}
