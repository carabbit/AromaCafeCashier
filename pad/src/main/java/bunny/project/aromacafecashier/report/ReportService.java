package bunny.project.aromacafecashier.report;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import bunny.project.aromacafecashier.MainActivity;
import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.R;
import bunny.project.aromacafecashier.common.MLog;
import bunny.project.aromacafecashier.lantransport.Constant;
import bunny.project.aromacafecashier.lantransport.LanTransportHelper;
import bunny.project.aromacafecashier.lantransport.TransportCallback;

/**
 * Created by bunny on 17-3-31.
 */

public class ReportService extends Service implements SendReportTask.OnSendFinishListener {
    private static final int MSG_SEND_REPORT = 1;
    private static final int MSG_RECV_MAIL = 2;

    private static final int REPEAT_INTERVAL = 1 * 30 * 1000;//间隔30秒
    private static final int RECEIVE_MAIL_INTERVAL = 2 * 60 * 1000;//接收邮件间隔2分

    private static final int SEND_HOUR = 21;//每天21：45时发送邮件
    private static final int SEND_MINUTE = 45;
    private static final int SEND_MINUTE_OFFSET = 2;
    private static final int SEND_MINUTE_LIMIT = SEND_MINUTE + SEND_MINUTE_OFFSET;


    private static final String PREF_FILE = "report_service";
    private static final String PREF_KEY_SEND_DAY = "send_day";

    private MyHandler mHandler;

    private TransportCallback mCallback = new TransportCallback() {

        private StringBuilder mStrBuilder = new StringBuilder();
        SharedPreferences pref;
        private DateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss");

        @Override
        public void progress(int res, String text) {
            mStrBuilder.delete(0, mStrBuilder.length());

            if (pref == null) {
                pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }

            String oriLog = pref.getString(Constant.PREF_SERVER_LOG, "");

            if (!TextUtils.isEmpty(oriLog)) {
                String[] commandLines = oriLog.split("\n> ");
                int length = commandLines.length;
                if (length > 100) {
                    for (int index = (length - 100); index < length; index++) {
                        mStrBuilder.append("\n> ");
                        mStrBuilder.append(commandLines[index]);
                    }
                } else {
                    mStrBuilder.append(oriLog);
                }
            }

            String log;
            if (TextUtils.isEmpty(text)) {
                log = getResources().getString(res);
            } else {
                log = getResources().getString(res, text);
            }

            mStrBuilder.append("\n> ");
            String formatTime = mTimeFormat.format(new Date(System.currentTimeMillis()));
            mStrBuilder.append(formatTime);
            mStrBuilder.append(" ");
            mStrBuilder.append(log);

            pref.edit().putString(Constant.PREF_SERVER_LOG, mStrBuilder.toString()).commit();

            MLog.i("", formatTime + " " + log);
        }

        @Override
        public void transportComplete() {
        }

        @Override
        public void notification(int res, String text) {
            Toast.makeText(ReportService.this, getString(res, text), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new MyHandler();

        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.delete);
        builder.setTicker("Aroma Cafe点单系统 开启");
        builder.setContentTitle("Aroma Cafe点单系统");
        builder.setContentText("运行中");
        Notification notification = builder.build();
        startForeground(1, notification);

//        LanTransportHelper.getInstance().acquireMultiCastLock(this);
//        LanTransportHelper.getInstance().startSyncServer(mCallback);
        LanTransportHelper.getInstance().startTcpRecieverForPad(mCallback);
        MLog.i("", "" + getDatabasePath("accs.db").getPath());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mHandler.hasMessages(MSG_SEND_REPORT)) {
            mHandler.removeMessages(MSG_SEND_REPORT);
            mHandler.sendEmptyMessageDelayed(MSG_SEND_REPORT, 0);
            MyLog.i("onStart", "sendEmptyMessage");
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_SEND_REPORT, REPEAT_INTERVAL);
            MyLog.i("onStart", "sendEmptyMessageDelayed");
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mHandler.hasMessages(MSG_SEND_REPORT)) {
            mHandler.removeMessages(MSG_SEND_REPORT);
            mHandler.sendEmptyMessageDelayed(MSG_SEND_REPORT, 0);
            MyLog.i("onStartCommand", "sendEmptyMessage");
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_SEND_REPORT, REPEAT_INTERVAL);
            MyLog.i("onStartCommand", "sendEmptyMessageDelayed");
        }

        MyLog.i("onStartCommand", "sendEmptyMessageDelayed");

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onSendFinish(boolean success) {
        if (success) {
            Calendar now = Calendar.getInstance();
            int dayNow = now.get(Calendar.DAY_OF_MONTH);

            SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
            sharedPreferences.edit().putInt(PREF_KEY_SEND_DAY, dayNow).commit();
        }
        MyLog.i("onSendFinish", "success:" + success);
    }

    private class MyHandler extends Handler {
        private Executor executor = Executors.newSingleThreadExecutor();

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SEND_REPORT) {
                Calendar now = Calendar.getInstance();
                int dayNow = now.get(Calendar.DAY_OF_MONTH);
                int hourNow = now.get(Calendar.HOUR_OF_DAY);
                int minuteNow = now.get(Calendar.MINUTE);
                MyLog.i("", " day:" + dayNow + " h:" + hourNow + " m:" + minuteNow);
                if (hourNow == SEND_HOUR && minuteNow >= SEND_MINUTE && minuteNow <= SEND_MINUTE_LIMIT) {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
                    int pref_day = sharedPreferences.getInt(PREF_KEY_SEND_DAY, 0);
                    MyLog.i("handleMessage", "pref_day:" + pref_day);
                    if (pref_day == 0 || pref_day != dayNow) {
                        new SendReportTask(ReportService.this, ReportService.this).executeOnExecutor(executor);
                        MyLog.i("handleMessage", "SendReportTask");
                    }

                }

                sendEmptyMessageDelayed(MSG_SEND_REPORT, REPEAT_INTERVAL);
            } else if (msg.what == MSG_RECV_MAIL) {
                sendEmptyMessageDelayed(MSG_RECV_MAIL, RECEIVE_MAIL_INTERVAL);
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LanTransportHelper.getInstance().releaseMultiCastLock();
    }

    //    public static void setReportAlarm(Context context) {
//        Intent intent = new Intent(context, ReportService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
//
//        long firstTime = SystemClock.elapsedRealtime(); //获取系统当前时间
//        long systemTime = System.currentTimeMillis();//java.lang.System.currentTimeMillis()，它返回从 UTC 1970 年 1 月 1 日午夜开始经过的毫秒数。
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); //  这里时区需要设置一下，不然会有8个小时的时间差
//        calendar.set(Calendar.HOUR_OF_DAY, 13);//设置为22：00点提醒
//        calendar.set(Calendar.MINUTE, 36);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        //选择的定时时间
//        long selectTime = calendar.getTimeInMillis();   //计算出设定的时间
//
//        //  如果当前时间大于设置的时间，那么就从第二天的设定时间开始
//        if (systemTime > selectTime) {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            selectTime = calendar.getTimeInMillis();
//        }
//
//        long time = selectTime - systemTime;// 计算现在时间到设定时间的时间差
//        long my_Time = firstTime + time;//系统 当前的时间+时间差
//
//        // 进行闹铃注册
//        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//
//
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, my_Time, 2 * 60 * 1000, pendingIntent);
//
//        MyLog.i("setReportAlarm", "");
//    }
}