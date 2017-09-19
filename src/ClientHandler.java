import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joakim on 14/09/2017.
 */
public class ClientHandler {
    List<Client> connectedClients;
    int serverPort = 4700;



    ClientHandler() {
        connectedClients = new ArrayList<>();

    }

    void connectUser(DatagramPacket packet) throws Exception {
        String stringPacket = new String(packet.getData(), 0, packet.getLength());
        System.out.println(packet.getPort());

        String username = stringPacket.substring(5, stringPacket.length());
        Client client = new Client(username, packet.getAddress(), packet.getPort());
        connectedClients.add(client);
        updateOnlineUsers();

    }

    void updateOnlineUsers() throws Exception {
        byte[] sendData = new byte[1024];
        String concattedClientNames = "LIST ";

        for (Client client : connectedClients) {
            concattedClientNames += client.getUserName() + ",";
        }

        sendData = concattedClientNames.getBytes();
        DatagramSocket socket = new DatagramSocket();

        for (Client client : connectedClients) {
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());
            socket.send(packet);

        }
    }

    void removeUser(Client client) {
        connectedClients.remove(client);
        System.out.println(client.getUserName() + " has left the server.\n");
    }

    void listen() throws Exception {
        byte[] receiveData = new byte[1024];
        DatagramSocket socket = new DatagramSocket(serverPort);
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

        do {
            socket.receive(packet);

        String stringPacket = new String(packet.getData(), 0, packet.getLength());

        String transactionType = stringPacket.substring(0, 4);
        System.out.println(stringPacket);

        // Lytte tråd

            new Thread(() -> {
                try {
                    listenSwitch(transactionType, packet, stringPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Lytte tråd

            new Thread(() -> {
                try {
                    sendHeartBeat();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }).start();


        } while (socket.isBound());

    }


    void msgToClients(String messagePacket) throws Exception {
        byte[] sendData = messagePacket.getBytes();
        DatagramSocket socket = new DatagramSocket();

        for (Client client : connectedClients) {
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());
            socket.send(packet);
        }

    }

    public void updateHeartBeat() {

    }

    public void sendHeartBeat() throws IOException {
        byte[] sendData;
        String heartBeat = "ALVE";
        sendData = heartBeat.getBytes();

        DatagramSocket socket = new DatagramSocket();

        for (Client client : connectedClients) {
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());
            socket.send(packet);

        }

    }



    public void listenSwitch(String transactionType, DatagramPacket packet, String stringPacket) throws Exception {
        switch (transactionType) {

            case "JOIN":
                connectUser(packet);
                break;

            case "DATA":
                msgToClients(stringPacket);
                break;

            case "ALVE":
                updateHeartBeat();
                break;
        }

    }

}
