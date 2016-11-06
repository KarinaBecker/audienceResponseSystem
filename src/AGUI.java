/**
 * AGUI
 * Display the game
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class AGUI extends JPanel {
    //Globals
    private static AClient client;
    public static String userName = "Anonymous";

    // GUI Globals - Main Window
    private static JFrame MainWindow = new JFrame();
    private static JButton B_CONNECT = new JButton();
    private static JButton B_DISCONNECT = new JButton();
    private static JButton B_SEND = new JButton();
    private static JLabel messageLabel = new JLabel();
    public static JTextField messageText = new JTextField(20);
    private static JLabel conversationLabel = new JLabel();
    public static JTextArea conversationText = new JTextArea();
    private static JScrollPane conversationScroll = new JScrollPane();

    //GUI Globals LogInWindow
    private static JFrame LogInWindow = new JFrame();
    private static JTextField LogInUserName = new JTextField(20);
    private static JButton LogInEnter = new JButton("ENTER");
    private static JLabel LogInLabel = new JLabel("Enter username: ");
    private static JPanel logInPanel = new JPanel();

    // Main method
    public AGUI(AClient client) {
        this.client = client;
        BuildLogInWindow();
    }

    // Build main window
    private static void BuildMainWindow() {
        MainWindow.setTitle("Multiple Choice Quiz - Name: " + userName);
        MainWindow.setSize(500, 450);
        MainWindow.setLocation(220, 180);
        MainWindow.setResizable(false);

        ConfigureMainWindow();
        MainWindowAction();
        MainWindow.setVisible(true);
    }

    // Set design for main window
    private static void ConfigureMainWindow() {
        MainWindow.setBackground(new java.awt.Color(255, 255, 255));
        MainWindow.getContentPane().setLayout(null); //

        B_CONNECT.setBackground(new java.awt.Color(0, 0, 255));
        B_CONNECT.setForeground(new java.awt.Color(255, 255, 255));
        B_CONNECT.setText("CONNECT");
        B_CONNECT.setToolTipText("");
        MainWindow.getContentPane().add(B_CONNECT);
        B_CONNECT.setBounds(10, 40, 110, 25);

        B_DISCONNECT.setBackground(new java.awt.Color(0, 0, 255));
        B_DISCONNECT.setForeground(new java.awt.Color(255, 255, 255));
        B_DISCONNECT.setText("DISCONNECT");
        MainWindow.getContentPane().add(B_DISCONNECT);
        B_DISCONNECT.setBounds(130, 40, 110, 25);

        B_SEND.setBackground(new java.awt.Color(0, 0, 255));
        B_SEND.setForeground(new java.awt.Color(255, 255, 255));
        B_SEND.setText("SEND");
        MainWindow.getContentPane().add(B_SEND);
        MainWindow.getRootPane().setDefaultButton(B_SEND);
        B_SEND.setBounds(250, 40, 110, 25);

        messageLabel.setText("Message");
        MainWindow.getContentPane().add(messageLabel);
        messageLabel.setBounds(10, 10, 60, 20);

        messageText.setForeground(new java.awt.Color(0, 0, 255));
        messageText.requestFocus();
        MainWindow.getContentPane().add(messageText);
        messageText.setBounds(70, 4, 290, 30);

        conversationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        conversationLabel.setText("Conversation");
        MainWindow.getContentPane().add(conversationLabel);
        conversationLabel.setBounds(100, 70, 140, 16);

        conversationText.setFont(new java.awt.Font("Arial", 0, 12));
        conversationText.setForeground(new java.awt.Color(0, 0, 255));
        conversationText.setLineWrap(true);
        conversationText.setRows(5);
        conversationText.setEditable(false);

        conversationScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        conversationScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        conversationScroll.setViewportView(conversationText);
        MainWindow.getContentPane().add(conversationScroll);
        conversationScroll.setBounds(10, 90, 450, 300);
    }

    // Build log in window
    private static void BuildLogInWindow() {
        LogInWindow.setTitle("Your name");
        LogInWindow.setSize(400, 100);
        LogInWindow.setLocation(250, 200);
        LogInWindow.setResizable(false);
        logInPanel = new JPanel();
        logInPanel.add(LogInLabel);
        logInPanel.add(LogInUserName);
        logInPanel.add(LogInEnter);
        LogInWindow.getRootPane().setDefaultButton(LogInEnter);
        LogInWindow.add(logInPanel);
        LogInEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                LogInEnterAction();
            }
        });
        LogInWindow.setVisible(true);
    }

    // Login Method upon click
    private static void LogInEnterAction() {
        if (!LogInUserName.getText().equals("")) {
            userName = LogInUserName.getText().trim();
            AServer.clientNames.add(userName);
            BuildMainWindow();
            LogInWindow.setVisible(false);
            B_SEND.setEnabled(true);
            B_DISCONNECT.setEnabled(true);
            B_CONNECT.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a name");
        }
    }

    // Connects to server
    private static void Connect() {
        try {
            String hostName = "localhost";
            int portNumber = 2345;
            Socket socket = new Socket(hostName, portNumber);
            client = new AClient(socket);
            client.startClient();
        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Server not responding.");
            System.exit(0);
        }
    }

    // Actions from main window (Connect, Disconnect, Send)
    private static void MainWindowAction() {
        B_CONNECT.addActionListener(
                new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //BuildLogInWindow();
                        LogInWindow.repaint();
                        LogInWindow.setVisible(true);
                        MainWindow.setVisible(false);
                        Connect();
                    }
                }
        );

        B_DISCONNECT.addActionListener(
                new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            client.disconnect();
                            B_SEND.setEnabled(false);
                            B_DISCONNECT.setEnabled(false);
                            B_CONNECT.setEnabled(true);
                        } catch (Exception e2) {
                            System.out.println("Problems while disconnecting.");
                            e2.printStackTrace();
                        }
                    }
                }
        );

        B_SEND.addActionListener(
                new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (!messageText.getText().equals("")) {
                            client.send(messageText.getText());
                        }
                    }
                }
        );
    }
}