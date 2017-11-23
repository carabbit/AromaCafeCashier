package bunny.project.aromacafecashier.lantransport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by bunny on 17-11-22.
 */

public class TcpRecevierForPad extends Thread {
    private static final String TAG = "TcpReceiver";

    private boolean mIsRunning;
    private boolean mIsClosed;
    private LanTransportHelper.Progress mProgress;
    private Socket mSocket;
    private ServerSocket mServerSocket;
    private OutputStream mSocketOutputStream;
    private InputStream mSocketInputStream;
    private FileInputStream mFis;

    public TcpRecevierForPad(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }


    @Override
    public void run() {

        mIsClosed = false;


        mIsRunning = true;
        mSocket = null;

        MLog.i(TAG, " new ServerSocket ++++++++++");
        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.start_tcp_receiver, null);
        try {
            mServerSocket = new ServerSocket(Constant.TCP_PORT);
        } catch (IOException e) {
            MLog.i(TAG, e.toString());
            onErrorProgress(e);
            return;
        }

        while (true) {
            try {
                mSocket = mServerSocket.accept();

                if (mIsClosed) {
                    return;
                }

                MLog.i(TAG, " get mSocket ++++++++++++++++");
                mProgress.onProgress(LanTransportHelper.TOKEN_TCP_CONNECTED, R.string.tcp_socket_got, null);

                if (mSocket != null) {
                    mSocketInputStream = mSocket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(mSocketInputStream);

                    String message = dataInputStream.readUTF();

                    mProgress.onProgress(LanTransportHelper.TOKEN_TOAST, R.string.tcp_message, message);


                    if (Constant.SYNC_MESSAGE_ASK.equals(message)) {
                        mSocketOutputStream = mSocket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(mSocketOutputStream);
                        dataOutputStream.writeUTF(Constant.SYNC_MESSAGE_ANSWER);

                        File dbFile = new File("/data/data/bunny.project.aromacafecashier/databases/accs.db");
                        mFis = new FileInputStream(dbFile);
                        byte[] bufFile = new byte[1024];
                        int len;

                        while (true) {
                            len = mFis.read(bufFile);
                            if (len != -1) {
                                mSocketOutputStream.write(bufFile, 0, len); //将从硬盘上读取的字节数据写入socket输出流
                            } else {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e1) {
                MLog.i(TAG, e1.toString());
                onErrorProgress(e1);
                break;
            } finally {

                if (mFis!=null){
                    try {
                        mFis.close();
                    } catch (IOException e) {
                        MLog.i(TAG, e.toString());
                        onErrorProgress(e);
                    }
                }

                if (mSocketOutputStream != null) {
                    try {
                        mSocketOutputStream.close();
                    } catch (IOException e) {
                        MLog.i(TAG, e.toString());
                        onErrorProgress(e);
                    }
                }

                if (mSocketInputStream != null) {
                    try {
                        mSocketInputStream.close();
                    } catch (IOException e) {
                        MLog.i(TAG, e.toString());
                        onErrorProgress(e);
                    }
                }


                try {
                    if (mSocket != null) {
                        mSocket.close();
                    }
                } catch (IOException e) {
                    MLog.i(TAG, e.toString());
                    onErrorProgress(e);
                }

            }
        }

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                MLog.i(TAG, e.toString());
                onErrorProgress(e);
            }
        }

        mIsRunning = false;
    }

    private void onErrorProgress(Exception e) {
        mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
    }
}
