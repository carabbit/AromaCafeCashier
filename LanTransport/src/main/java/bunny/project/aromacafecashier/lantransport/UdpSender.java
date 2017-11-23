package bunny.project.aromacafecashier.lantransport;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by user on 15-5-4.
 */

/* 发送udp多播 */
public class UdpSender extends Thread {
    private static final String TAG = "UdpSender";

    byte[] data = Constant.SYNC_MESSAGE_ASK.getBytes(Charset.forName("UTF-8"));
    private LanTransportHelper.Progress mProgress;

    public UdpSender(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }

    @Override
    public void run() {
        DatagramSocket udpSocket = null;

        try {
            String localIp = Utils.getLocalHostIp();
            MLog.i(TAG, "localIp:" + localIp);
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.local_ip, localIp);
            MLog.i(TAG, "===");

            int lastIp = Integer.parseInt(localIp.substring(localIp.lastIndexOf(".") + 1));

            String preIp = localIp.substring(0, localIp.lastIndexOf("."));

            MLog.i(TAG, "[udpBroadCast] before send udp");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.before_send_udp, null);

            udpSocket = new DatagramSocket(Constant.UDP_SEND_PORT);
            for (int i = 1; i < 255; i++) {
                if (i == lastIp) {
                    continue;
                }

                String targetIp = preIp + "." + i;
                InetAddress address = InetAddress.getByName(targetIp);
                DatagramPacket dp = new DatagramPacket(data, data.length, address, Constant.UDP_RECEIVE_PORT);
                udpSocket.send(dp);
                MLog.i(TAG, "send to " + targetIp);
            }


            MLog.i(TAG, "[udpBroadCast] after  send udp");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.after_send_udp, null);
        } catch (Exception e) {
            MLog.i(TAG, e.toString());
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.exception, e.toString());
        } finally {
            if (udpSocket != null) {
                udpSocket.close();
            }
            MLog.i(TAG, "[udpBroadCast] finally");
            mProgress.onProgress(LanTransportHelper.TOKEN_UPD_SENDER_CLOSE, R.string.close_send_udp, null);
        }
    }
}