package uwu.misaka;


import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
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
                    handle(str);
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
    public void rating(String i){
        try {
            out.write("s "+i+"\n");
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
        System.exit(0);
    }
    public void handle(String str){
        if(str.startsWith("w ")){
            str=str.substring(2);
            PlayerInfo info = PlayerInfo.fromJson(str);
            ArrayList<PlayerInfo> l = new ArrayList<>();
            Ichi.info.forEach(e->{if(e.uuid.equals(info.uuid))l.add(e);});
            l.forEach(e->Ichi.info.remove(e));
            for(Player p: Groups.player){
                if(p.uuid().equals(info.uuid)){
                    info.loginName=p.name();
                    String name = Ranker.prefix(info.getLvl());
                    if(!info.nick.equals("")){
                        name+=info.nick;
                    }else{name+=p.name();}
                    p.name(name);
                }
            }
            Ichi.info.add(info);
            Log.info("Loaded info about "+info.uuid);
            return;
        }
        if(str.equals("ar")){
            Ichi.info.forEach(i->write(i));
            return;
        }
        if(str.startsWith("s ")){
            str=str.substring(2);
            RatingInfo info = RatingInfo.fromJson(str);
            Groups.player.forEach(a->{if(a.uuid().equals(info.rId)){
                Call.infoMessage(a.con(),info.toString());
            }});
            return;
        }
    }
}
