package bunny.project.aromacafecashier.lantransport;

import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by user on 15-5-4.
 */

/*接收udp多播 并 发送tcp 连接*/
public class UdpReceiveAndTcpSend extends Thread {

    private static final String TAG = "UdpReceiveAndTcpSend";
    Socket socket = null;
    MulticastSocket ms = null;
    DatagramPacket dp;

    public UdpReceiveAndTcpSend() {
    }

    @Override
    public void run() {
        Message msg;
        String information;

        byte[] data = new byte[1024];
        try {
            InetAddress groupAddress = InetAddress.getByName(Constant.MULTI_BROADCAST_ADDRESS);
            ms = new MulticastSocket(Constant.UDP_PORT);
            ms.joinGroup(groupAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                dp = new DatagramPacket(data, data.length);
                if (ms != null) {
                    MLog.i(TAG, "start receiving...");
                    ms.receive(dp);
                }
            } catch (Exception e) {
                MLog.i(TAG, e.toString());
                break;
            }

            MLog.i(TAG, "udp received");

            if (dp.getAddress() != null) {
                final String quest_ip = dp.getAddress().toString();

                String host_ip = Utils.getLocalHostIp();

                MLog.i(TAG, "host_ip:          " + host_ip);
                MLog.i(TAG, "remote_device_ip: " + quest_ip.substring(1));

                /* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

                if ((!host_ip.equals("")) && host_ip.equals(quest_ip.substring(1))) {
                    continue;
                }

                final String codeString = new String(data, 0, dp.getLength());

//                msg = new Message();
//                msg.what = 0x222;
                information = "收到来自: \n" + quest_ip.substring(1) + "\n" + "的udp请求\n";
//                        + "请求内容: " + codeString + "\n\n";
//
                MLog.i(TAG, information);

//                msg.obj = information;
//                handler.sendMessage(msg);

                try {
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
                } catch (IOException e) {
                    e.printStackTrace();
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
                    }
                }
            }
        }
    }


}
