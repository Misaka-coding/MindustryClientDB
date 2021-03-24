package uwu.misaka;


import arc.util.Log;
import mindustry.net.Administration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Net{
    public static String ipAddr = "127.0.0.1";
    public static int port = 9000;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private ReadMsg readMsg;
    public Net() throws IOException {
        socket=new Socket(ipAddr,port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        readMsg = new ReadMsg();
        readMsg.start();
        out.write("////"+ Administration.Config.valueOf("name").get().toString()+"\n");
        out.flush();
        Log.info("Connected to server");
    }
    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            while (true) {
                try {
                    str = in.readLine();
                    PlayerInfo info =PlayerInfo.fromJson(str);
                    ArrayList<PlayerInfo> l = new ArrayList<>();
                    Ichi.info.forEach(e->{if(e.uuid.equals(info.uuid))l.add(e);});
                    l.forEach(e->Ichi.info.remove(e));
                    Ichi.info.add(info);
                    Log.info("Loaded info about "+info.uuid);
                } catch (Exception ignored) {
                    Log.err("Connection lost");
                    Ichi.net=null;
                    this.interrupt();
                    return;
                }
            }
        }
    }
    public void request(String uuid){
        try {
            out.write("r "+uuid+"\n");
            out.flush();
        } catch (IOException e) {
            Log.err("Connection lost");
            Ichi.net=null;
            return;
        }
    }
    public void write(PlayerInfo i){
        try {
            out.write("w "+i.toJson()+"\n");
            out.flush();
        } catch (IOException e) {
            Log.err("Connection lost");
            Ichi.net=null;
            return;
        }
    }
    public void exit(){
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readMsg.interrupt();
        System.exit(0);
    }

}
