package bunny.project.aromacafecashier.lantransport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import bunny.project.aromacafecashier.common.MLog;

/**
 * Created by user on 15-5-4.
 */

/* 接收tcp连接 */
public class TcpReceiver extends Thread {
    private static final String TAG = "TcpReceiver";

    private boolean mIsRunning;
    private boolean mIsClosed;
    private LanTransportHelper.Progress mProgress;
    private Socket mSocket;
    private ServerSocket mServerSocket;

    public TcpReceiver(LanTransportHelper.Progress progress) {
        mProgress = progress;
    }

    public void close() {
        MLog.i(TAG, "[close] mSocket：" + mSocket);
        if (mSocket != null) {
            try {
                MLog.i(TAG, ">> close tcp mSocket");
                mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.close_tcp_socket, null);
                mSocket.close();
            } catch (IOException e) {
                MLog.i(TAG, e.toString());
                mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
            }
        } else {
            mIsClosed = true;
            if (mServerSocket != null) {
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    MLog.i(TAG, e.toString());
                    mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
                }
            }
            mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.close_tcp_socket, null);
        }
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void run() {

        mIsClosed = false;

        while (true) {
            mIsRunning = true;
            mSocket = null;
            try {
                MLog.i(TAG, " new ServerSocket ++++++++++");
                mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.start_tcp_receiver, null);
                mServerSocket = new ServerSocket(Constant.TCP_PORT);

                mSocket = mServerSocket.accept();

                if (mIsClosed) {
                    break;
                }

                MLog.i(TAG, " get mSocket ++++++++++++++++");
                mProgress.onProgress(LanTransportHelper.TOKEN_TCP_CONNECTED, R.string.tcp_socket_got, null);

                if (mSocket != null) {
                    InputStream socketInputStream = mSocket.getInputStream();

//                    byte[] bufName=new byte[1024];
//                    int lenInfo =0;
//                    lenInfo = socketIs.read(bufName);  //获取文件名

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

                    if (!dbFile.exists()) {
                        MLog.i(TAG, "FILE CREATE FAIL!");
                        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_file_fail, null);
                        break;
                    } else {
                        MLog.i(TAG, "FILE CREATE SUCCESS!");
                        mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.create_db_file_success, null);
                    }

                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.receive_db_ready, null);
                    FileOutputStream fos = new FileOutputStream(dbFile);
                    byte[] bufFile = new byte[1024 * 1024];   //接收数据的缓存
                    int len;
                    while (true) {
                        len = socketInputStream.read(bufFile); //接收数据
                        if (len != -1) {
                            fos.write(bufFile, 0, len); //写入硬盘文件
                        } else {
                            break;
                        }
                    }
//                    writeOutInfo(sock,"上传成功!");   //文件接收成功后给客户端反馈一个信息
//                    s.op("文件接收成功!"+System.getProperty("line.separator"));  //服务端打印一下
                    fos.close();
                    socketInputStream.close();

                    MLog.i(TAG, "file received");
                    mProgress.onProgress(LanTransportHelper.TOKEN_PROGRESS, R.string.receive_db_success, null);
                    mProgress.onProgress(LanTransportHelper.TOKEN_COMPLETE, 0, null);
                    break;
                }
            } catch (IOException e1) {
                MLog.i(TAG, e1.toString());
                mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e1.toString());
                break;
            } finally {
                try {
                    if (mSocket != null) {
                        mSocket.close();
                    }

                    if (mServerSocket != null) {
                        mServerSocket.close();
                    }
                } catch (IOException e) {
                    MLog.i(TAG, e.toString());
                    mProgress.onProgress(LanTransportHelper.TOKEN_ERROR, R.string.exception, e.toString());
                    break;
                }
            }
        }

        mIsRunning = false;
    }
}
