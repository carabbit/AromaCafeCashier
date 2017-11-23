package bunny.project.aromacafecashier.lantransport;

/**
 * Created by bunny on 17-11-9.
 */

public class Constant {
    public static final String UDP_MULTI_BROADCAST_ADDRESS = "239.0.0.5";

    public static final int UDP_MULTI_BROADCAST_PORT = 55553;
    public static final int UDP_RECEIVE_PORT = 55554;
    public static final int UDP_SEND_PORT = 55555;
    public static final int TCP_PORT = 55556;

    public static final String SYNC_MESSAGE_ASK = "bunny:sync:request";
    public static final String SYNC_MESSAGE_ANSWER = "bunny:sync:permit";

    public static final String PREF_SERVER_LOG = "PREF_SERVER_LOG";
}
