/**
 * class AClient
 * start after AServer
 */

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class AClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 2345;

        try {
            socket = new Socket(hostName, portNumber);
            AClient client = new AClient(socket);
            new AGUI(client);
            client.startClient();

        } catch (UnknownHostException e) {
            System.err.println("Don't know host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Problems with IO");
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Problems closing socket");
                    e.printStackTrace();
                }
            }
        }
    }

    // Constructor AClient
    public AClient(Socket socket) {
        this.socket = socket;
    }

    public void startClient() {
        try {
            //get socket's output stream (write data to destination) and input stream (reads data from source)
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //receive server's text, send user input
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            while ((fromServer = in.readLine()) != null) {
                AGUI.conversationText.append(fromServer + "\n");
                if (fromServer.equals("That's it. Bye."))
                    break;

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    out.println(fromUser);
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        } finally {
            try {
                System.out.println("Client is disconnected");
                socket.close();
            } catch (IOException e) {
                System.out.println("Try to close client socket. failed: ");
                e.printStackTrace();
            }
        }
    }

    // Send method to AGUI
    public void send(String message) {
        try {
            out.println(message);    // server console
            AGUI.conversationText.append(message + "\n"); // user gui client output
            AGUI.conversationText.append(in.readLine() + "\n"); // user gui server output
            out.flush();
            AGUI.messageText.setText("");
            AGUI.messageText.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //disconnect method to AGUI
    public void disconnect() {
        try {
            out.println("User has disconnected");
            out.flush();
            socket.close();
            JOptionPane.showMessageDialog(null, "You disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}