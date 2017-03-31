package bunny.project.aromacafecashier;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.TimeZone;

import bunny.project.aromacafecashier.report.ReportService;

/**
 * Created by bunny on 17-3-31.
 */
public class AccsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, ReportService.class));
    }

}
