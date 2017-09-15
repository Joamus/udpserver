import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
        int indexOfComma = stringPacket.indexOf(";");
        int clientPort = Integer.parseInt(stringPacket.substring(5, indexOfComma));
        String username = stringPacket.substring(indexOfComma+1, stringPacket.length());
        Client client = new Client(username, packet.getAddress(), clientPort);
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


        switch (transactionType) {

            case "JOIN":
                connectUser(packet);
                break;

            case "DATA":
                msgToClients(stringPacket);
                break;
        }
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

}
