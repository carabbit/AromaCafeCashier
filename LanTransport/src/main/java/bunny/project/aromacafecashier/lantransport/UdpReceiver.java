package bunny.project.aromacafecashier.lantransport;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by bunny on 17-11-22.
 */

public class UdpReceiver extends Thread {
    private static final String TAG = "UdpReceiver";
    Socket socket = null;
    //    MulticastSocket multicastSocket = null;
    DatagramPacket dp;
    private LanTransportHelper.Progress mProgress;
    private MulticastSocket multicastSocket;

    public UdpReceiver(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }

    @Override
    public void run() {
        {
            String information;

            String host_ip = Utils.getLocalHostIp();
            MLog.i(TAG, "host_ip:          " + host_ip);
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.local_ip, host_ip);

            byte[] data = new byte[1024];
//        try {
//            InetAddress groupAddress = InetAddress.getByName(Constant.UDP_MULTI_BROADCAST_ADDRESS);
//            multicastSocket = new MulticastSocket(Constant.UDP_RECEIVE_PORT);
//            multicastSocket.joinGroup(groupAddress);
//        } catch (Exception e) {
//            MLog.i(TAG, e.toString());
//            mCallback.progress(R.string.exception, e.toString());
//            mCallback.transportComplete();
//            return;
//        }
//            DatagramSocket updSocket = null;
            try {
                InetAddress groupAddress = InetAddress.getByName(Constant.UDP_MULTI_BROADCAST_ADDRESS);
                multicastSocket = new MulticastSocket(Constant.UDP_MULTI_BROADCAST_PORT);
                multicastSocket.joinGroup(groupAddress);
            } catch (Exception e) {
                MLog.i(TAG, e.toString() + " port:" + Constant.UDP_MULTI_BROADCAST_PORT);
                mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
                return;
            }

            while (true) {
                try {
                    dp = new DatagramPacket(data, data.length);
                    if (multicastSocket != null) {
                        MLog.i(TAG, "start receiving...");
                        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.start_udp_receiver, null);
                        multicastSocket.receive(dp);
                    }
                } catch (Exception e) {
                    MLog.i(TAG, e.toString());
                    mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
                    return;
                }

                MLog.i(TAG, "udp received");
                mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.udp_received, null);

                if (dp.getAddress() != null) {
                    final String quest_ip = dp.getAddress().toString();


                    MLog.i(TAG, "remote_device_ip: " + quest_ip.substring(1));
                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.remote_ip, quest_ip.substring(1));

                /* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

                    if ((!host_ip.equals("")) && host_ip.equals(quest_ip.substring(1))) {
                        continue;
                    }


                    byte[] udpData = dp.getData();
                    String udpMessage = new String(udpData, 0, dp.getLength(), Charset.forName("UTF-8"));
                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.udp_message, udpMessage);

                    information = "收到来自: " + quest_ip.substring(1) + " 的udp请求\n"
                            + "请求内容: " + udpMessage;
                    MLog.i(TAG, information);


                    if (!Constant.SYNC_MESSAGE_ASK.equals(udpMessage)) {
                        return;
                    }

//                    try {
//                        mCallback.progress(R.string.before_send_tcp, null);
//                        final String target_ip = dp.getAddress().toString().substring(1);
//
//                        mSocket = new Socket(target_ip, Constant.TCP_PORT);
//
//                        OutputStream outputStream = mSocket.getOutputStream();
//                        File dbFile = new File("/data/data/bunny.project.aromacafecashier/databases/accs.db");
//                        FileInputStream fis = new FileInputStream(dbFile);
//                        byte[] bufFile = new byte[1024];
//                        int len;
//
//                        while (true) {
//                            len = fis.read(bufFile);
//                            if (len != -1) {
//                                outputStream.write(bufFile, 0, len); //将从硬盘上读取的字节数据写入socket输出流
//                            } else {
//                                break;
//                            }
//                        }
//
//                        mSocket.shutdownOutput();// 关闭输出流
//                        mCallback.progress(R.string.after_send_tcp, null);
//
//                    } catch (IOException e) {
//                        mCallback.progress(R.string.exception, e.toString());
//                    } finally {
//
//                        try {
//                            if (mSocket != null) {
//                                mSocket.close();
//                            }
//                        } catch (IOException e) {
//                            MLog.i(TAG, e.toString());
//                            mCallback.progress(R.string.exception, e.toString());
//                        }
//
//                        mCallback.transportComplete();
//                    }
                }
            }
        }
    }
}
