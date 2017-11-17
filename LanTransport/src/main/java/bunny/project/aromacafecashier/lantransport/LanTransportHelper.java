package bunny.project.aromacafecashier.lantransport;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import bunny.project.aromacafecashier.common.MLog;
//import org.apache.http.conn.util.InetAddressUtils;

/**
 * Created by bunny on 17-11-9.
 */

public class LanTransportHelper {
    private static final String TAG = "LanTransportHelper";

    /* 用于 udpReceiveAndTcpSend 的3个变量 */
    Socket socket = null;
    MulticastSocket ms = null;
    DatagramPacket dp;

    public static final int TOKEN_PROGRESS = 0;
    public static final int TOKEN_COMPLETE = 1;
    public static final int TOKEN_ERROR = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOKEN_PROGRESS) {
                mCallback.progress(msg.arg1, (String) msg.obj);
            } else if (msg.what == TOKEN_COMPLETE) {
                mCallback.transportComplete();
            } else if (msg.what == TOKEN_ERROR) {
                mCallback.progress(msg.arg1, (String) msg.obj);
                mCallback.transportComplete();
            } else {
                throw new RuntimeException("invalid token:" + msg.what);
            }
        }
    };

    private Progress mProgress = new Progress() {
        @Override
        public void onProgress(int token, int res, String text) {
            Message message = mHandler.obtainMessage();
            message.what = token;
            message.arg1 = res;
            message.obj = text;
            message.sendToTarget();
        }
    };

    private TcpReceiver mTcpReceiver;
    private TransportCallback mCallback;

    private LanTransportHelper() {
    }

    private static volatile LanTransportHelper sInstance;

    public static LanTransportHelper getInstance() {
        if (sInstance == null) {
            synchronized (LanTransportHelper.class) {
                if (sInstance == null) {
                    sInstance = new LanTransportHelper();
                }
            }
        }

        return sInstance;
    }


    /**
     * 手机端发起同步操作。
     *
     * @param callback
     */
    public void sync(TransportCallback callback) {
        mCallback = callback;
        startTcpReceiver(mProgress);
        startSearch(mProgress);
    }

    public void closeSync(TransportCallback callback) {
        if (mTcpReceiver != null) {
            mTcpReceiver.close();
        }
    }


    private void startTcpReceiver(Progress progress) {
        if (mTcpReceiver == null) {
            mTcpReceiver = new TcpReceiver(progress);
        }

        if (!mTcpReceiver.isRunning()) {
            mTcpReceiver.start();
        }
    }

    public void startSyncServer() {
        new UdpReceiveAndTcpSend().start();
        MLog.i(TAG, "[startSyncServer]");
    }

    /**
     * 发送udp广播，搜索数据服务器地址。
     *
     * @param progress
     */
    private void startSearch(Progress progress) {
        new udpBroadCast(progress).start();
        MLog.i(TAG, "[startSearch]");
    }

    /* 发送udp多播 */
    private class udpBroadCast extends Thread {
        MulticastSocket sender = null;
        DatagramPacket dp = null;
        InetAddress group = null;

        byte[] data = new byte[1024];
        private Progress mProgress;

        public udpBroadCast(Progress progress) {
            mProgress = progress;
        }

        @Override
        public void run() {
            try {
                String localIp = Utils.getLocalHostIp();
                MLog.i(TAG, "localIp:" + localIp);
                mProgress.onProgress(TOKEN_PROGRESS, R.string.local_ip, localIp);
                MLog.i(TAG, "===");
                sender = new MulticastSocket();
                group = InetAddress.getByName(Constant.MULTI_BROADCAST_ADDRESS);
                dp = new DatagramPacket(data, data.length, group, Constant.UDP_PORT);

                MLog.i(TAG, "[udpBroadCast] before send udp");
                mProgress.onProgress(TOKEN_PROGRESS, R.string.before_send_udp, null);
                sender.send(dp);
                MLog.i(TAG, "[udpBroadCast] after  send udp");
                mProgress.onProgress(TOKEN_PROGRESS, R.string.after_send_udp, null);
            } catch (Exception e) {
                Log.i(TAG, e.toString());
                mProgress.onProgress(TOKEN_PROGRESS, R.string.exception, e.toString());
            } finally {
                sender.close();
                MLog.i(TAG, "[udpBroadCast] finally");
                mProgress.onProgress(TOKEN_PROGRESS, R.string.close_send_udp, null);
            }
        }
    }

    protected interface Progress {
        void onProgress(int token, int res, String text);
    }

}
