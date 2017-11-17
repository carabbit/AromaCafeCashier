package bunny.project.aromacafecashier.backup;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import bunny.project.aromacafecashier.MyLog;
import bunny.project.aromacafecashier.common.provider.AccsDbHelper;

/**
 * Created by bunny on 17-4-28.
 */

public class BackupDatabase {
    private static final String TAG = BackupDatabase.class.getSimpleName();

    private static final String DB_PATH = "/data/data/bunny.project.aromacafecashier/databases/" + AccsDbHelper.DB_NAME;


//    public static String getDatabasePath() {
//        return null;
//    }

    public static File pullDatabase() {
        return pullDatabase(DB_PATH, AccsDbHelper.DB_NAME);
    }

    private static File pullDatabase(String srcPath, String destFileName) {
        File sourceFile = new File(srcPath);

        File sdPath = null;

        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

        if (sdCardExist) {
            sdPath = Environment.getExternalStorageDirectory();// 获取根目录
        }

        File targetFile = new File(sdPath, destFileName);

        FileHelper helper = new FileHelper();

        try {
            helper.copy(sourceFile, targetFile);
            MyLog.i(TAG, "pull " + destFileName + " success");
        } catch (IOException e) {
            MyLog.i(TAG, "[pullDatabase]:" + e.getMessage());
            MyLog.i(TAG, "pull " + destFileName + " fail");
            targetFile = null;
        }

        return targetFile;
    }
}
