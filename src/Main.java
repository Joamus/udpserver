/**
 * Created by Joakim on 14/09/2017.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ClientHandler clienthandler = new ClientHandler();

        new Thread (() -> {
            try {
                clienthandler.sendHeartBeat();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {
            try {
                clienthandler.listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
