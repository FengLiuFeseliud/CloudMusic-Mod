package fengliu.cloudmusic.music163;

import java.util.List;

public interface MusicList extends PrintObject {
    
    /**
     * 获取对象音乐列表
     * @return 音乐列表
     */
    List<Music> getMusics();
}
