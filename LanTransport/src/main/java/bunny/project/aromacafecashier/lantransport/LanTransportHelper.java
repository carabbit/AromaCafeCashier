package bunny.project.aromacafecashier.lantransport;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import java.net.DatagramPacket;
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
    public static final int TOKEN_TCP_CONNECTED = 2;
    public static final int TOKEN_CLOSE_TCP_RECEIVER = 3;
    public static final int TOKEN_ERROR = 4;
    public static final int TOKEN_UPD_SENDER_CLOSE = 5;
    public static final int TOKEN_TOAST = 6;

    private static final int REPEAT_TIME = 5;

    private TcpReceiver mTcpReceiver;
    private TransportCallback mCallback;
    private WifiManager.MulticastLock mMulticastLock;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mCallback == null) {
                MLog.i(TAG, "mCallback is null");
                return;
            }

            switch (msg.what) {
                case TOKEN_PROGRESS: {
                    mCallback.progress(msg.arg1, (String) msg.obj);
                    break;
                }
                case TOKEN_COMPLETE: {
                    removeMessages(TOKEN_CLOSE_TCP_RECEIVER);
                    mCallback.transportComplete();
                    break;
                }
                case TOKEN_ERROR: {
                    removeMessages(TOKEN_CLOSE_TCP_RECEIVER);
                    mCallback.progress(msg.arg1, (String) msg.obj);
                    mCallback.transportComplete();
                    break;
                }
                case TOKEN_TCP_CONNECTED: {
                    removeMessages(TOKEN_CLOSE_TCP_RECEIVER);
                    mCallback.progress(msg.arg1, (String) msg.obj);
                    break;
                }
                case TOKEN_CLOSE_TCP_RECEIVER: {
                    int count = (int) msg.obj;
                    if (count <= REPEAT_TIME) {
                        mCallback.progress(msg.arg1, String.valueOf(count));
                        stopTcpReceiver(++count);
                    } else {
                        closeSync();
                    }
                    break;
                }
                case TOKEN_UPD_SENDER_CLOSE: {
                    stopTcpReceiver(1);
                    break;
                }
                case TOKEN_TOAST:{
                    mCallback.notification(msg.arg1, (String) msg.obj);
                    break;
                }
                default:
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

    private LanTransportHelper() {
    }

    private static volatile LanTransportHelper sInstance;

    public void acquireMultiCastLock(Context context) {
        if (mMulticastLock == null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mMulticastLock = wifiManager.createMulticastLock("accs_multicast");
        }
        mMulticastLock.acquire();
    }

    public void releaseMultiCastLock() {
        if (mMulticastLock != null) {
            mMulticastLock.release();
        }
    }

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

    public void closeSync() {
        MLog.i("", "[closeSync]");
        if (mTcpReceiver != null) {
            mTcpReceiver.close();
        }
    }

    public void sendUdpMulticast() {
        new UpdMultiSender(mProgress).run();
    }

    public void startUdpReceiver(TransportCallback callback) {
        mCallback = callback;
        new UdpReceiver(mProgress).start();
    }


    private void startTcpReceiver(Progress progress) {
        if (mTcpReceiver == null) {
            mTcpReceiver = new TcpReceiver(progress);
        }

        if (!mTcpReceiver.isRunning()) {
            mTcpReceiver.start();
            stopTcpReceiver(1);
        }
    }

    private void stopTcpReceiver(int count) {
        if (!mTcpReceiver.isRunning()) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = TOKEN_CLOSE_TCP_RECEIVER;
        message.arg1 = R.string.wait_for_receive_tcp;
        message.obj = count;
        mHandler.sendMessageDelayed(message, 1500);
        MLog.i("", "stopTcpReceiver: " + count);
    }

    public void startSyncServer(TransportCallback callback) {
        new UdpReceiveAndTcpSend(callback).start();
        MLog.i(TAG, "[startSyncServer]");
    }

    /**
     * 发送udp广播，搜索数据服务器地址。
     *
     * @param progress
     */
    private void startSearch(Progress progress) {
        new UdpSender(progress).start();
        MLog.i(TAG, "[startSearch]");
    }

    protected interface Progress {
        void onProgress(int token, int res, String text);
    }

    public void startTcpRecieverForPad(TransportCallback callback) {
        mCallback = callback;
        new TcpRecevierForPad(mProgress).start();
    }

    public void startTcpSenderForPhone(TransportCallback callback){
        mCallback = callback;
        new TcpTransferClient(mProgress).start();
    }

}
