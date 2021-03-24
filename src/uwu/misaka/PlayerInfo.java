package uwu.misaka;

import java.util.ArrayList;

import static uwu.misaka.Ichi.gson;

public class PlayerInfo {
    public String uuid; //primary key
    public int blocksPlaced=0;
    public int blocksDestroyed=0;
    public long totalPlayedTime =0;
    public int pvpWins=0;
    public int hexedWins=0;
    public int attackWins=0;
    public int totalWaves=0;
    public long discordId=0;
    public String nick = "";
    public ArrayList<String> warnings = new ArrayList<>();

    public PlayerInfo(String uuid){
        this.uuid=uuid;
    }
    public static PlayerInfo fromJson(String json){
        return gson.fromJson(json, PlayerInfo.class);
    }
    public String toJson(){
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        String rtn="";
        long tm=totalPlayedTime;
        long minutes=0;
        long hours=0;
        long days=0;
        while (tm>60){tm -= 60;hours++;}
        minutes=tm;
        while (hours>24){hours -= 24;days++;}
        int lvl = 1;
        long xp = totalPlayedTime * 5+blocksPlaced*3+blocksDestroyed+pvpWins*10+hexedWins*20+attackWins*7+totalWaves*2;
        long levelCap = 1000;
        while(xp>levelCap){
            xp-=levelCap;
            lvl++;
            levelCap*=2;
        }
        String time = days+"д. "+hours+"ч. "+minutes+"м.";
        rtn+="Общее время игры на сервере: "+time+"\n";
        rtn+="Всего блоков сломано: "+blocksDestroyed+"\n";
        rtn+="Всего блоков построено: "+blocksPlaced+"\n";
        rtn+="Всего побед на пвп: "+pvpWins+"\n";
        rtn+="Всего побед на атаке: "+attackWins+"\n";
        rtn+="Всего волн: "+attackWins+"\n";
        rtn+="Уровень: "+lvl+"\n";
        rtn+="До следующего уровня осталось: "+(levelCap-xp);
        return rtn;
    }
    public int getLvl(){
        int lvl = 1;
        long xp = totalPlayedTime * 5+blocksPlaced*3+blocksDestroyed+pvpWins*10+hexedWins*20+attackWins*7+totalWaves*2;
        long levelCap = 1000;
        while(xp>levelCap){
            xp-=levelCap;
            lvl++;
            levelCap*=2;
        }
        return lvl;
    }
}
