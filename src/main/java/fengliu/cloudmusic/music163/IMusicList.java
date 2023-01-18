package fengliu.cloudmusic.music163;

import java.util.List;

public interface IMusicList extends IPrint {
    
    /**
     * 获取对象歌曲列表
     * @return 歌曲列表
     */
    List<IMusic> getMusics();
}
