package bunny.project.aromacafecashier.lantransport;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import bunny.project.aromacafecashier.common.MLog;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by bunny on 17-11-10.
 */

public class Utils {

    public static final String TAG = "Utils";

    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            MLog.i(TAG, "[getLocalHostIp]" + e.toString());
        }
        return ipaddress;
    }

    public static String getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        MLog.i(TAG, "wifi信息：" + wifiInfo.toString());
        MLog.i(TAG, "wifi名称：" + wifiInfo.getSSID());

        return null;
    }
}
