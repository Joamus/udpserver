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

        String username = stringPacket.substring(5, stringPacket.length());
        Client client = new Client(username, packet.getAddress(), packet.getPort(), System.currentTimeMillis());

        boolean alreadyJoined = false;

        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getUserName().equalsIgnoreCase(username)) {
                alreadyJoined = true;
                break;
            }
        }

        if (alreadyJoined) {
            sendLoginError(client);
        } else if (!alreadyJoined) {
            sendLoginOK(client);
            connectedClients.add(client);
            updateOnlineUsers();
        }

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

    void listen() throws Exception {
        byte[] receiveData = new byte[1024];
        DatagramSocket socket = new DatagramSocket(serverPort);
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

        do {
            socket.receive(packet);

            String stringPacket = new String(packet.getData(), 0, packet.getLength());

            String transactionType = stringPacket.substring(0, 4);
            listenSwitch(transactionType, packet, stringPacket);

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

    public void updateUserHeartbeat(String stringPacket) {
        String username = stringPacket.substring(5, stringPacket.length());

        // Finder brugeren i connectedClients, og opdaterer hans tid til nu.

        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getUserName().equals(username)) {
                connectedClients.get(i).setTimeStamp(System.currentTimeMillis());
                break;
            }
        }
    }

    public void sendHeartBeat() throws Exception {
        long DELAY = 1 * 2000;
        while (true) {
            byte[] sendData;
            String heartBeat = "ALVE";
            sendData = heartBeat.getBytes();

            DatagramSocket socket = new DatagramSocket();

            for (Client client : connectedClients) {
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());
                socket.send(packet);
            }
            Thread.sleep(DELAY);
            removeUsersWithoutHeartBeat(DELAY);
        }

    }

    public void removeUsersWithoutHeartBeat(long DELAY) {
        long DELAY_WITH_BUFFER = DELAY + 2000;

        for (int i = 0; i < connectedClients.size(); i++) {
            if (System.currentTimeMillis()-connectedClients.get(i).getTimeStamp() > DELAY_WITH_BUFFER) {
                connectedClients.remove(connectedClients.get(i));
            }
        }
        try {
            updateOnlineUsers();
        } catch (Exception e) {
            e.printStackTrace();
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
                updateUserHeartbeat(stringPacket);
                break;


        }

    }

     void sendLoginError(Client client) throws Exception {
        byte[] sendData;
        String heartBeat = "JERR";
        sendData = heartBeat.getBytes();

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());

        socket.send(packet);


    }

    void sendLoginOK(Client client) throws Exception {
        byte[] sendData;
        String heartBeat = "J_OK";
        sendData = heartBeat.getBytes();

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, client.getIp(), client.getPort());

        socket.send(packet);

    }

}
