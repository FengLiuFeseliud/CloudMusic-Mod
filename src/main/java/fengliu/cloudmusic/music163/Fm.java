package fengliu.cloudmusic.music163;

import fengliu.cloudmusic.music163.data.My;
import fengliu.cloudmusic.util.MusicPlayer;

/**
 * Fm 私人电台
 */
public class Fm extends MusicPlayer {
    private My my;

    public Fm(My my) {
        super(my.fm());
        this.my = my;
    }

    /**
     * 重写播放器 run
     */
    @Override
    public void run() {
        while(this.loopPlayIn){
            playMusic();

            if(this.playIn == this.playList.size() - 1 && this.loopPlayIn){
                this.playList.addAll(my.fm());
            }

            this.playIn += 1;
        }
    }
    
}
