package uwu.misaka;

import uwu.misaka.Ichi;

import java.util.ArrayList;

import static uwu.misaka.Ichi.gson;

public class RatingInfo {
    public String rId;
    public int userPlace;
    public ArrayList<String> players;
    public RatingInfo(){
    }
    public String toJson(){
        return gson.toJson(this);
    }
    public static RatingInfo fromJson(String json){
        return gson.fromJson(json, RatingInfo.class);
    }

    @Override
    public String toString() {
        String rtn = "РЕЙТИНГ\n";
        for(String s:players){
            rtn+=s+"[]\n";
        }
        rtn+="\nВаше место: "+userPlace;
        return rtn;
    }
}

