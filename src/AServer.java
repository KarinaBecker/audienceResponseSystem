/** class AServer
 * Starts server side and socket, needs to be started first
 *
 * TO DO
 * fix name issue from "Anonymous"
 * GUI: get radio buttons implemented
 * fix "Disconnect" + proper output on server console
 *
 * LATER
 * fix threads + change to synchronised ones
 * get real time report from server
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class AServer {

    public static ArrayList<Socket> clientConnections = new ArrayList<Socket>(); //hold connections, so that message to all users
    public static ArrayList<String> clientNames = new ArrayList<String>(); //List of users

    public static void main(String[] args) throws IOException {
        int portNumber = 2345;
        boolean listening = true;
        Socket clientSocket = null;
        ServerSocket serverSocket = null;

        try {
            // Accept connection to client
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is now online on port " + portNumber);
        } catch (IOException e) {
            System.err.println("Exception: couldn't create socket");
            e.printStackTrace();
        }
        try {
            // Returns new Socket so that server can continue to listen to client requests on original socket
            while (listening) {
                if (serverSocket == null) {
                    return;
                }
                clientSocket = serverSocket.accept();
                new Thread(new AProtocol(clientSocket)).start();
                System.out.println("Client connected from " + clientSocket.getLocalAddress().getHostName() + " to port " + portNumber);
                clientConnections.add(clientSocket);
                System.out.println("Client sockets: " + clientConnections);
                clientNames.add(AGUI.userName);
                System.out.println("Client names: " + clientNames);
            }

        } catch (IOException e) {
            System.err.println("Exception: couldn't connect to client socket");
            e.printStackTrace();
        }
    }
}