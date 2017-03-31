import java.net.*;
import java.io.*;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

//Server class
public class Server {
    public DatagramSocket serverSocket;
    public List<Thread> threads;
    public String currentDir;
    public Server() {
        try {
            currentDir = "C:\\Users\\100602925\\Desktop\\TEST";
            threads = new ArrayList<Thread>();;
            serverSocket = new DatagramSocket(4444);
        } catch (IOException e) {
            System.exit(1);
        }
    }

    //runs server
    public void run (){
        while (1==1){
            try {
                byte[] receivePacket = new byte[1024];
                DatagramPacket pkt = new DatagramPacket(receivePacket,receivePacket.length);
                serverSocket.receive(pkt);
                byte[] data = new byte[pkt.getLength()];
                System.arraycopy(pkt.getData(), pkt.getOffset(), data, 0, pkt.getLength());
                String sentence = new String(data);
                System.out.println ("Recieved. "+  sentence);
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    //main
    public static void main(String[] args)
    {
        Server server = new Server();
        server.run();
    }

}
