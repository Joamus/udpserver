import java.net.InetAddress;

/**
 * Created by Joakim on 14/09/2017.
 */
public class Client {
    String username;
    InetAddress ip;
    int port;
    long timeStamp;

    Client(String username, InetAddress ip, int port, long timeStamp) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.timeStamp = timeStamp;

    }

    public String getUserName() {
        return username;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


}
