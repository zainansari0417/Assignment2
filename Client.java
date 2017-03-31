//Ammar Khan
//Zain Ansari

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client extends Application {

    //ip, port and directory lists
    public InetAddress SERVER_IP;
    public int SERVER_PORT;
    public String currentDir;
    ArrayList <String> serverDir;
    ArrayList <String> localDir;
    DatagramSocket mySocket;

    //client window
    public void start(Stage primaryStage) {
        localDir = new ArrayList<String>();
        serverDir = new ArrayList<String>();

        try {
            mySocket = new DatagramSocket();
        } catch(IOException e) {
            e.printStackTrace();
        }

        SERVER_PORT = 4444;
        final ListView list1 = new ListView();
        final ListView list2 = new ListView();
        currentDir = ".";

        primaryStage.setTitle("File Sharer v1.0");

        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout, 700, 500);

        GridPane menu = new GridPane();
        layout.setTop(menu);
        Button button1 = new Button("Download");
        Button button2 = new Button("Upload");
        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {}
        });

        //download button
        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                String selected = "";
                selected = (String) list1.getSelectionModel().getSelectedItem();
                System.out.println(selected);
                try {
                    uploadFile(selected);
                    serverDirectory();
                    list2.getItems().clear();

                    for (String directory : serverDir) {
                        list2.getItems().add(directory);
                    }
                }catch (Exception eeee){
                    System.out.println (eeee);
                }

            }
        });

        //upload button
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selected = "";
                selected = (String) list2.getSelectionModel().getSelectedItem();
                System.out.println(selected);
                try{
                    downloadFile(selected);
                    localDir = new ArrayList<String>();
                    File dir = new File(currentDir);
                    File[] filesList = dir.listFiles();
                    for (File file : filesList) {
                        if (file.isFile()) {
                            localDir.add(file.getName());
                        }
                    }
                    list1.getItems().clear();
                    for (String directory : localDir) {
                        list1.getItems().add(directory);
                    }
                }catch (Exception e1){
                    System.out.println(e1);
                }
            }
        });
        menu.add(button1, 1, 1);
        menu.add(button2, 2, 1);

        SplitPane sp = new SplitPane();
        layout.setCenter(sp);
        sp.setPrefWidth(scene.getWidth());
        sp.setPrefHeight(scene.getHeight());
        sp.setPadding(new Insets(10,10,10,10));

        for (String directory : localDir) {
            list1.getItems().add(directory);
        }
        for (String directory : serverDir) {
            list2.getItems().add(directory);
        }
        sp.getItems().addAll(list1, list2);
        sp.setDividerPositions(0.5);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    //Directory on the server
    public void serverDirectory () throws Exception
    {

    }

    //Downloading a file from the server
    public void downloadFile(String fileName) throws Exception{
        String temp;
        byte[]data;
        byte[] spk,rpk;
        spk ="send_file".getBytes();
        DatagramPacket pkt = new DatagramPacket(spk,spk.length, SERVER_IP, SERVER_PORT);
        mySocket.send(pkt);

        rpk = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(rpk, rpk.length);
        mySocket.receive(receivePacket);

        data = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
        temp = new String(data);
        int TEMP_PORT = Integer.parseInt(temp);

        Socket newConnection = new Socket("localhost",TEMP_PORT);

        System.out.println("Connected");
        DataInputStream input = new DataInputStream( newConnection.getInputStream());
        DataOutputStream output = new DataOutputStream( newConnection.getOutputStream());

        output.writeBytes(fileName + "\n");

        FileWriter out = new FileWriter(currentDir + "\\"+ fileName);

        out.close();
        newConnection.close();
    }

    //Uploading a file to the server
    public void uploadFile(String fileName)throws Exception{
        String temp;
        byte[]data;
        byte[] spk,rpk;

        spk ="req_file".getBytes();
        DatagramPacket pkt = new DatagramPacket(spk,spk.length, SERVER_IP, SERVER_PORT);
        mySocket.send(pkt);

        rpk = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(rpk, rpk.length);
        mySocket.receive(receivePacket);

        data = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
        temp = new String(data);
        int TEMP_PORT = Integer.parseInt(temp);
        System.out.println ("recv ");
        System.out.println (TEMP_PORT);
        System.out.println("Connecting to server.");
        Socket newConnection = new Socket("localhost",TEMP_PORT);

        System.out.println("Connected");
        DataInputStream input = new DataInputStream( newConnection.getInputStream());
        DataOutputStream output = new DataOutputStream( newConnection.getOutputStream());

        output.writeBytes(fileName + "\n");
        output.writeBytes(Long.toString(new File(fileName).length()) + "\n");
        System.out.println(new File(fileName).length());

        FileReader in = new FileReader(fileName);
        BufferedReader b = new BufferedReader(in);

        ArrayList <String> stringArray = new ArrayList<String>();

        char[] cbuf = new char[256];
        ArrayList <String> cArray = new ArrayList<String>();

        String line = "";
        while ((line = b.readLine()) != null)   {
            cArray.add(line);
        }
        output.writeBytes( Integer.toString(cArray.size()) + "\n");
        for (String aLine : cArray){
            output.writeBytes(aLine+"\n");
        }
        in.close();
        b.close();
    }

}
