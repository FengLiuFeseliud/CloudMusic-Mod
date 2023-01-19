package fengliu.cloudmusic.music163;

import fengliu.cloudmusic.config.Configs;
import net.minecraft.text.Text;

import java.util.List;

public interface IMusic extends IPrint{
    long getId();
    String getName();
    String getPicUrl();
    String getPlayUrl();
}
