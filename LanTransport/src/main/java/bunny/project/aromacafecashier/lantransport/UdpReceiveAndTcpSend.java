package bunny.project.aromacafecashier.lantransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by user on 15-5-4.
 */

/*接收udp多播 并 发送tcp 连接*/
public class UdpReceiveAndTcpSend extends Thread {

    private static final String TAG = "UdpReceiveAndTcpSend";
    Socket socket = null;
    //    MulticastSocket ms = null;
    DatagramPacket dp;
    private TransportCallback mCallback;

    public UdpReceiveAndTcpSend(TransportCallback callback) {
        mCallback = callback;
    }

    @Override
    public void run() {
        String information;

        String host_ip = Utils.getLocalHostIp();
        MLog.i(TAG, "host_ip:          " + host_ip);
        mCallback.progress(R.string.local_ip, host_ip);

        byte[] data = new byte[1024];
//        try {
//            InetAddress groupAddress = InetAddress.getByName(Constant.UDP_MULTI_BROADCAST_ADDRESS);
//            ms = new MulticastSocket(Constant.UDP_RECEIVE_PORT);
//            ms.joinGroup(groupAddress);
//        } catch (Exception e) {
//            MLog.i(TAG, e.toString());
//            mCallback.progress(R.string.exception, e.toString());
//            mCallback.transportComplete();
//            return;
//        }
        DatagramSocket updSocket = null;
        try {
            updSocket = new DatagramSocket(Constant.UDP_RECEIVE_PORT);
            updSocket.setReuseAddress(true);
        } catch (SocketException e) {
            MLog.i(TAG, e.toString() + " port:" + Constant.UDP_RECEIVE_PORT);
            mCallback.progress(R.string.exception, e.toString());
            return;
        }

        while (true) {
            try {
                dp = new DatagramPacket(data, data.length);
                if (updSocket != null) {
                    MLog.i(TAG, "start receiving...");
                    mCallback.progress(R.string.start_udp_receiver, null);
                    updSocket.receive(dp);
                }
            } catch (Exception e) {
                MLog.i(TAG, e.toString());
                mCallback.progress(R.string.exception, e.toString());
                mCallback.transportComplete();
                return;
            }

            MLog.i(TAG, "udp received");
            mCallback.progress(R.string.udp_received, null);

            if (dp.getAddress() != null) {
                final String quest_ip = dp.getAddress().toString();


                MLog.i(TAG, "remote_device_ip: " + quest_ip.substring(1));
                mCallback.progress(R.string.remote_ip, quest_ip.substring(1));

                /* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

                if ((!host_ip.equals("")) && host_ip.equals(quest_ip.substring(1))) {
                    continue;
                }


                byte[] udpData = dp.getData();
                String udpMessage = new String(udpData, 0, dp.getLength(), Charset.forName("UTF-8"));
                mCallback.progress(R.string.udp_message, udpMessage);

                information = "收到来自: " + quest_ip.substring(1) + " 的udp请求\n"
                        + "请求内容: " + udpMessage;
                MLog.i(TAG, information);

                if (!Constant.SYNC_MESSAGE_ASK.equals(udpMessage)) {
                    return;
                }
//

//                msg.obj = information;
//                handler.sendMessage(msg);

                try {
                    mCallback.progress(R.string.before_send_via_tcp, null);
                    final String target_ip = dp.getAddress().toString().substring(1);

//                    msg = new Message();
//                    msg.what = 0x111;
//                    information = "发送tcp请求到: \n" + target_ip + "\n";
//                    msg.obj = information;
//                    handler.sendMessage(msg);

                    socket = new Socket(target_ip, Constant.TCP_PORT);


                    OutputStream outputStream = socket.getOutputStream();
//                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    File dbFile = new File("/data/data/bunny.project.aromacafecashier/databases/accs.db");
                    FileInputStream fis = new FileInputStream(dbFile);
                    byte[] bufFile = new byte[1024];
                    int len;

                    while (true) {
                        len = fis.read(bufFile);
                        if (len != -1) {
                            outputStream.write(bufFile, 0, len); //将从硬盘上读取的字节数据写入socket输出流
                        } else {
                            break;
                        }
                    }

//                    PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
//                    printWriter.print("你好，服务端已接收到您的信息");
//                    printWriter.flush();
                    socket.shutdownOutput();// 关闭输出流
                    mCallback.progress(R.string.after_send_via_tcp, null);

                } catch (IOException e) {
                    mCallback.progress(R.string.exception, e.toString());
                } finally {

                    try {
                        if (socket != null) {
                            socket.close();
                        }

//                        if (ms != null) {
//                            ms.close();
//                        }
                    } catch (IOException e) {
                        MLog.i(TAG, e.toString());
                        mCallback.progress(R.string.exception, e.toString());
                    }

                    mCallback.transportComplete();
                }
            }
        }
    }


}
