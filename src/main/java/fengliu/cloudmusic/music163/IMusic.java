package fengliu.cloudmusic.music163;

public interface IMusic extends IPrint{
    public long getId();
    public String getName();
    public String getPicUrl();
    public String getPlayUrl(int br);
}
