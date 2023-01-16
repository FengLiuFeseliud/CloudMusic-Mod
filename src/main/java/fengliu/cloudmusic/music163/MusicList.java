package fengliu.cloudmusic.music163;

import java.util.List;

public interface MusicList extends PrintObject {
    
    /**
     * 获取对象歌曲列表
     * @return 歌曲列表
     */
    List<Music> getMusics();
}
