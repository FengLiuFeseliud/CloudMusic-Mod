package fengliu.cloudmusic.music163;

import fengliu.cloudmusic.util.Time;

public interface IMusic extends IPrint{
    long getId();
    String getName();
    String getPicUrl();
    String getPlayUrl();
    long getDuration();

    default int getDurationSecond(){
        return (int) (getDuration() / 1000);
    }

    default String getDurationToString(){
        return Time.secondToString(this.getDurationSecond());
    }
}
