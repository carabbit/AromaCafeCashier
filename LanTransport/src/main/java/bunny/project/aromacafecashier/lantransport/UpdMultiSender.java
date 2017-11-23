package bunny.project.aromacafecashier.lantransport;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by bunny on 17-11-22.
 */

public class UpdMultiSender extends Thread {
    private static final String TAG = "UpdMultiSender";
    byte[] data = Constant.SYNC_MESSAGE_ASK.getBytes(Charset.forName("UTF-8"));
    private LanTransportHelper.Progress mProgress;

    public UpdMultiSender(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }

    @Override
    public void run() {
//        DatagramSocket udpSocket = null;
        MulticastSocket multiSocket = null;
        try {
            MLog.i(TAG, "[udpBroadCast] before send udp");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.before_send_udp, null);

//            udpSocket = new DatagramSocket(Constant.UDP_SEND_PORT);
            multiSocket = new MulticastSocket();

            InetAddress address = InetAddress.getByName(Constant.UDP_MULTI_BROADCAST_ADDRESS);
            DatagramPacket dp = new DatagramPacket(data, data.length, address, Constant.UDP_MULTI_BROADCAST_PORT);
            multiSocket.send(dp);


            MLog.i(TAG, "[udpBroadCast] after  send udp");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.after_send_udp, null);
        } catch (Exception e) {
            MLog.i(TAG, e.toString());
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.exception, e.toString());
        } finally {
            if (multiSocket != null) {
                multiSocket.close();
            }
            MLog.i(TAG, "[udpBroadCast] finally");
            mProgress.onProgress(LanTransportHelper.TOKEN_UPD_SENDER_CLOSE, R.string.close_send_udp, null);
        }

    }
}
