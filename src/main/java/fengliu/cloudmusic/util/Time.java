package fengliu.cloudmusic.util;

public class Time {

    public static String secondToString(int Second) {
        int minute = Second / 60;
        int second = Second - (minute * 60);

        if (second < 10 && minute < 10){
            return "0" + minute + ":0" + second;
        }

        if (second < 10){
            return minute + ":0" + second;
        }

        if (minute < 10){
            return "0" + minute + ":" + second;
        }

        return minute + ":" + second;
    }

}
