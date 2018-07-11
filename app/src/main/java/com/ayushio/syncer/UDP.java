package com.ayushio.syncer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Client {
    public static void main(String[] args) {
        new UDP().send("127.0.0.1", 52000, "hoge");
    }
}

class Server implements UDPserver {
    private int count;
    private UDP udp;

    public static void main(String[] args) {
        Server s = new Server();
        s.count = 1;
        s.udp = new UDP(s);
        s.udp.boot(50000);
    }

    public void recv(String host, int port, String data) {
        if(count <= 5) {
            System.out.println(host + " ["+ port + "]");
            System.out.println("data:" + data);
            count++;
        }
        else{
            udp.shutdown();
        }
    }
}


interface UDPserver {
    public void recv(String host, int port, String data);
}

class UDP {
    private UDPserver serverInst = null;
    private DatagramSocket serverSock = null;

    public UDP(){
    }

    public UDP(UDPserver serverInst){
        this.serverInst = serverInst;
    }

    public void send(String host, int port, String data){
        new SendProc(host, port, data).start();
    }

    class SendProc extends Thread {
        private String host;
        private int port;
        private String data;

        SendProc(String host, int port, String data){
            this.host = host;
            this.port = port;
            this.data = data;
        }

        public void run () {
            try{
                InetAddress ia = InetAddress.getByName(host);
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(
                        data.getBytes(),
                        data.getBytes().length,
                        ia,
                        port);
                sock.send(packet);
                sock.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    public void boot(int port) {
        try{
            if(serverSock == null) {
                serverSock = new DatagramSocket(port);
                new RecvProc().start();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    class RecvProc extends Thread {
        public void run(){
            try {
                byte buf[] = new byte[1024*128];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                while(true) {
                    serverSock.receive(packet);

                    InetAddress	ia = packet.getAddress();
                    String host = ia.toString();
                    int port = packet.getPort();
                    String data = new String(packet.getData(), 0, packet.getLength());

                    serverInst.recv(host, port, data);
                }
            } catch (Exception e) {
                System.out.println("server down");
            }
        }
    }

    public void shutdown(){
        try{
            if(serverSock != null) {
                serverSock.close();
                serverSock = null;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}

