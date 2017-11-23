package bunny.project.aromacafecashier.lantransport;

/**
 * Created by bunny on 17-11-15.
 */
public interface TransportCallback {
    void progress(int res, String text);

    void transportComplete();

    void notification(int res, String text);
}
