package uwu.misaka;

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Timer;
import com.google.gson.Gson;

import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.*;

import java.io.IOException;
import java.util.ArrayList;

public class Ichi extends Plugin{
    public static Gson gson = new Gson();
    public static ArrayList<uwu.misaka.PlayerInfo> info = new ArrayList<>();
    public static Net net;

    @Override
    public void init() {
        try {
            net = new Net();
        } catch (IOException e) {
            Log.err("Connection failed");
        }
        Timer.schedule(()->{info.forEach(i->{
            if(i==null){return;}
            i.totalPlayedTime++;});},60,60);
        Events.on(EventType.BlockBuildEndEvent.class,e->{
            if(!e.unit.isPlayer()){return;}
            Player p=e.unit.getPlayer();
            info.forEach(i->{
                if(i==null){return;}
                if(p.uuid().equals(i.uuid)){
                    if(e.breaking){i.blocksDestroyed++;}else{i.blocksPlaced++;}
                }
            });
        });
        Events.on(EventType.GameOverEvent.class,e->{
            for(Player p: Groups.player){
                if(p.team()!=e.winner){
                    info.forEach(i->{if(i==null){return;};write(i);});
                    Log.info("Writed info about "+info.size()+" players");
                    return;
                }
                if(Vars.state.rules.pvp){
                    info.forEach(i->{
                        if(i==null){return;}
                        if (p.uuid().equals(i.uuid)) {
                            i.pvpWins++;
                        }
                    });
                }
                if(Vars.state.rules.attackMode){
                    info.forEach(i->{
                        if(i==null){return;}
                        if (p.uuid().equals(i.uuid)) {
                            i.attackWins++;
                        }
                    });
                }

                info.forEach(i->{
                if(i==null){return;}write(i);});
                Log.info("Writed info about "+info.size()+" players");
            }
        });
        Events.on(EventType.WaveEvent.class,e->{info.forEach(i->{if(i==null){return;};i.totalWaves++;});});
        Events.on(EventType.PlayerLeave.class,e->{
            ArrayList<PlayerInfo> removed=new ArrayList<>();
            info.forEach(i->{
                if(i==null){return;}
                if (e.player.uuid().equals(i.uuid)) {
                    removed.add(i);
                }
            });
            removed.forEach(i->{
                if(i==null){return;}
                loadOut(i);
                Log.info("Writed info about "+i.uuid);
            });
        });
        Events.on(EventType.PlayerJoin.class,(e)->{
            load(e.player.uuid());
        });
    }
    public void load(String uuid){
        if(net==null){reRunNet();}
        if(net==null){return;}
        net.request(uuid);
    }
    public void write(PlayerInfo player){
        if(net==null){reRunNet();}
        if(net==null){return;}
        net.write(player);
    }
    public void loadOut(PlayerInfo player){
        if(net==null){reRunNet();}
        if(net==null){return;}
        net.write(player);
        info.remove(player);
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.removeCommand("exit");
        handler.register("exit","Exit the server application.",args->{
            Log.info("Shutting down server");
            Vars.net.dispose();
            Core.app.exit();
            net.exit();
            System.exit(0);
        });
    }
    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("status","Player info",(args,player)->{
            String status="Статус игрока "+player.name()+"[][][][][][]\n";
            PlayerInfo i = null;
            for(PlayerInfo p:info){
                if(p==null){Call.infoMessage(player.con(),"Нет доступа к базе");}
                if(p.uuid.equals(player.uuid())){
                    i=p;
                    break;
                }
            }
            if(i==null){return;}
            status+=i.toString();
            Call.infoMessage(player.con(),status);
        });
        handler.<Player>register("changename","<name>","Change your nickname",(args,player)->{
            for(PlayerInfo p:info){
                if(p.getLvl()<10){Call.infoMessage(player.con(),"Требуется 10 уровень и выше");}
                if(p==null){Call.infoMessage(player.con(),"Нет доступа к базе");}
                if(p.uuid.equals(player.uuid())){
                    p.nick=args[0];
                }
            }
        });
        handler.<Player>register("rating","Change your nickname",(args,player)->{
            if(net==null){reRunNet();}
            if(net==null){return;}
            net.rating(player.uuid());
        });
        handler.<Player>register("clearname","Reset your nickname",(args,player)->{
            for(PlayerInfo p:info){
                if(p==null){Call.infoMessage(player.con(),"Нет доступа к базе");}
                if(p.uuid.equals(player.uuid())){
                    p.nick="";
                }
            }
        });
    }
    public void reRunNet(){
        try {
            net = new Net();
        } catch (IOException e) {
            Log.err("Connection failed");
        }
    }
}
