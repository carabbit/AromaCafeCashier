package bunny.project.aromacafecashier.lantransport;

import android.text.TextUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.nio.charset.Charset;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by bunny on 17-11-22.
 */

public class TcpTransferClient extends Thread {
    private static final String TAG = "TcpTransferClient";
    Socket mSocket = null;

    byte[] data = Constant.SYNC_MESSAGE_ASK.getBytes(Charset.forName("UTF-8"));
    private LanTransportHelper.Progress mProgress;
    private FileOutputStream mFileStream;
    private OutputStream mSocketOutputStream;
    private InputStream mSocketInputStream;

    public TcpTransferClient(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }

    @Override
    public void run() {

        String localIp = Utils.getLocalHostIp();

        if (TextUtils.isEmpty(localIp)) {
            mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, "IP NOT FOUND");
            return;
        }


//        MLog.i(TAG, "localIp:" + localIp);
//        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.local_ip, localIp);
//        MLog.i(TAG, "===");

        int localLastIp = Integer.parseInt(localIp.substring(localIp.lastIndexOf(".") + 1));

        String preIp = localIp.substring(0, localIp.lastIndexOf("."));

        MLog.i(TAG, "send TCP start");
        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.before_send_via_tcp, null);

        for (int i = 100; i < 255; i++) {
            if (i == localLastIp) {
                continue;
            }

            String targetIp = preIp + "." + i;

            try {
                InetAddress address = InetAddress.getByName(targetIp);

                MLog.i(TAG, "send to " + targetIp);
                mSocket = new Socket(address, Constant.TCP_PORT);
                mSocket.setSoTimeout(3000);
                mSocketOutputStream = mSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(mSocketOutputStream);

                dataOutputStream.writeUTF(Constant.SYNC_MESSAGE_ASK);
                MLog.i(TAG, "send TCP end");
                mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.after_send_via_tcp, null);


                mSocketInputStream = mSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(mSocketInputStream);

                String answerMessage = dataInputStream.readUTF();
                MLog.i(TAG, "answerMessage" + answerMessage);

                if (Constant.SYNC_MESSAGE_ANSWER.equals(answerMessage)) {

                    File dbFile = createFile();

                    if (dbFile == null) {
                        break;
                    }

                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.receive_db_ready, null);
                    mFileStream = new FileOutputStream(dbFile);
                    byte[] bufFile = new byte[1024 * 1024];   //接收数据的缓存
                    int len;
                    while (true) {
                        len = mSocketInputStream.read(bufFile); //接收数据
                        if (len != -1) {
                            mFileStream.write(bufFile, 0, len); //写入硬盘文件
                        } else {
                            break;
                        }
                    }

                    MLog.i(TAG, "file received");
                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.receive_db_success, null);
                    mProgress.onProgress(LanTransportHelper.TOKEN_COMPLETE, 0, null);

                    break;
                }
            } catch (NoRouteToHostException
                    | ConnectException e) {
                mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.screen_display, targetIp + " " + e.getMessage());
            } catch (Exception e) {
                onErrorProgress(e);
            } finally {
                if (mSocketOutputStream != null) {
                    try {
                        mSocketOutputStream.close();
                    } catch (IOException e) {
                        onErrorProgress(e);
                    }
                }

                if (mSocketInputStream != null) {
                    try {
                        mSocketInputStream.close();
                    } catch (IOException e) {
                        onErrorProgress(e);
                    }
                }

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        onErrorProgress(e);
                    }
                }

                if (mFileStream != null) {
                    try {
                        mFileStream.close();
                    } catch (IOException e) {
                        onErrorProgress(e);
                    }
                }
            }
        }


    }

    private File createFile() throws IOException {
        File dbPath = new File("/data/data/bunny.project.aromacafecashier.phone/databases/");
        if (!dbPath.exists()) {
            dbPath.mkdirs();
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_path, null);
        }

        File dbFile = new File("/data/data/bunny.project.aromacafecashier.phone/databases/accs.db");

        if (!dbFile.exists()) {
            dbFile.createNewFile();
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_file, null);

        }

        if (dbFile.exists()) {
            MLog.i(TAG, "FILE CREATE SUCCESS!");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_file_success, null);
            return dbFile;
        } else {
            MLog.i(TAG, "FILE CREATE FAIL!");
            mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_file_fail, null);
            return null;
        }
    }

    private void onErrorProgress(Exception e) {
        mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
    }
}
