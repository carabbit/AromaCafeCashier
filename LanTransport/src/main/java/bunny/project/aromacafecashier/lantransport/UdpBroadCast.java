package bunny.project.aromacafecashier.lantransport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by user on 15-5-4.
 */

/* 发送udp多播 */
public class UdpBroadCast extends Thread {
    MulticastSocket sender = null;
    DatagramPacket dj = null;
    InetAddress group = null;

    byte[] data = new byte[1024];

    public UdpBroadCast(String dataString) {
        data = dataString.getBytes();
    }

    @Override
    public void run() {
        try {
            sender = new MulticastSocket();
            group = InetAddress.getByName(Constant.MULTI_BROADCAST_ADDRESS);
            dj = new DatagramPacket(data, data.length, group, Constant.UDP_PORT);
            sender.send(dj);
            sender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}