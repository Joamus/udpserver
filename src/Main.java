/**
 * Created by Joakim on 14/09/2017.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ClientHandler clienthandler = new ClientHandler();
        try {
            clienthandler.listen();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
