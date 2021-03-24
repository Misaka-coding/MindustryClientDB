package uwu.misaka;

public class Ranker {
    public static final int lvl2 = 5;
    public static final int lvl3 = 10;
    public static final int lvl4 = 15;
    public static final int lvl5 = 25;
    public static final int lvl6 = 30;
    public static final int lvl7 = 35;
    public static final int lvl8 = 40;

    public static final String lvl1n="�������";
    public static final String lvl2n="������";
    public static final String lvl3n="�������";
    public static final String lvl4n="������";
    public static final String lvl5n="�����";
    public static final String lvl6n="�������";
    public static final String lvl7n="�����";
    public static final String lvl8n="�������";

    public static String prefix(int lvl){
        if(lvl>=lvl8){return "["+lvl8n+"]";}
        if(lvl>=lvl7){return "["+lvl7n+"]";}
        if(lvl>=lvl6){return "["+lvl6n+"]";}
        if(lvl>=lvl5){return "["+lvl5n+"]";}
        if(lvl>=lvl4){return "["+lvl4n+"]";}
        if(lvl>=lvl3){return "["+lvl3n+"]";}
        if(lvl>=lvl2){return "["+lvl2n+"]";}
        return "["+lvl1n+"]";
    }
}
