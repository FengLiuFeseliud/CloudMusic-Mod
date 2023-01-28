package fengliu.cloudmusic.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.music163.data.DjMusic;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.util.page.Page;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * 歌曲播放对象
 */
public class MusicPlayer implements Runnable {
    private final MinecraftClient client = MinecraftClient.getInstance();
    protected final List<IMusic> playList;
    private IMusic playingMusic = null;
    private SourceDataLine play;
    private Lyric lyric;
    protected int playIn = 0;
    protected int playListSize;
    protected boolean loopPlayIn = true;
    private boolean load;
    private int Volume;
    private long playingProgress;
    private long startPlayingTime;

    /**
     * 通过音量百分比计算音量增益
     * @param volumePercentage 音量百分比
     * @return 音量增益
     */
    public static int toVolume(int volumePercentage){
        if(volumePercentage < 0){
            volumePercentage = 0;
        }

        if(volumePercentage > 100){
            volumePercentage = 100;
        }

        return (int) (86 * 0.01 * volumePercentage);
    }

    /**
     * 歌曲播放对象
     * @param playList 歌曲列表
     */
    public MusicPlayer(List<IMusic> playList){
        this.playListSize = playList.size();
        this.playList = playList;

        this.volumeSet(toVolume(Configs.PLAY.VOLUME.getIntegerValue()));
    }

    public boolean isPlaying(){
        return this.loopPlayIn && this.load;
    }

    @Override
    public void run() {
        while(this.loopPlayIn){
            for (this.playIn = 0; playIn < this.playListSize; this.playIn++) {
                playMusic();

                if(!this.loopPlayIn){
                    break;
                }

                if(this.playIn == this.playListSize - 1 && !Configs.PLAY.PLAY_LOOP.getBooleanValue()){
                    this.loopPlayIn = false;
                }
            }
        }
    }

    /**
     * 启动歌曲播放
     */
    public void start(){
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("CloudMusicPlayer thread");
        thread.start();
    }

    /**
     * 播放歌曲
     */
    protected void playMusic(){
        IMusic music = this.playList.get(this.playIn);

        String musicUrl;
        try {
            musicUrl = music.getPlayUrl();
        }catch(ActionException err){
            MinecraftClient client = MinecraftClient.getInstance();
            if(client.player != null){
                client.player.sendMessage(Text.literal(err.getMessage()));
            }
            return;
        }

        MusicIconTexture.getMusicIcon(music);
        if (music instanceof Music){
            this.lyric = ((Music) music).lyric();
        } else {
            this.lyric = null;
        }

        this.playingMusic = music;
        if(!Configs.PLAY.PLAY_URL.getBooleanValue()){
            String[] urls = musicUrl.split("\\.");
            String fileType = urls[urls.length - 1];

            File file;
            if (music instanceof DjMusic){
                file = HttpClient.download(musicUrl, CloudMusicClient.cacheHelper.getWaitCacheFile("djmusic_" + music.getId() + "." + fileType));
            } else {
                file = HttpClient.download(musicUrl, CloudMusicClient.cacheHelper.getWaitCacheFile(music.getId() + "." + fileType));
            }

            CloudMusicClient.cacheHelper.addUseSize(file);
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.getName()), false);
            this.play(file);
        }else {
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.getName()), false);
            this.play(musicUrl);
        }
    }

    /**
     * 播放歌曲
     */
    private void play(AudioInputStream audioInputStream) throws IOException, InterruptedException, LineUnavailableException{
        AudioFormat audioFormat = audioInputStream.getFormat();
        // 转换文件编码
        if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            System.out.println(audioFormat.getEncoding());
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
            audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
        }

        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        play = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        play.open(audioFormat);
        //设置音量
        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(this.Volume);

        play.start();
        if (lyric != null){
            this.lyric.start();
        }

        int count;
        byte[] tempBuff = new byte[1024];

        this.load = true;
        this.startPlayingTime = System.currentTimeMillis();
        while((count = audioInputStream.read(tempBuff,0,tempBuff.length)) != -1){
            synchronized(this){
            while(!load)
                wait();
            }
            play.write(tempBuff,0,count);
            this.playingProgress = System.currentTimeMillis() - this.startPlayingTime;
        }

        this.playingProgress = 0;
        if (lyric != null){
            this.lyric.exit();
        }
    }

    /**
     * 通过 URL 播放歌曲
     * @param url 歌曲 url
     */
    private void play(String url){
        try {
            this.play(AudioSystem.getAudioInputStream(AudioSystem.getAudioInputStream(new URL(url))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件对象播放歌曲
     * @param file 文件对象
     */
    private void play(File file){
        try {
            this.play(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音量增益
     *
     * @param volume 音量增益
     */
    public void volumeSet(int volume){
        if(volume < 0){
            volume = 0;
        }

        if(volume <= 80 && volume != 0){
            volume = (80 - volume) * -1;
        }

        if(volume > 80){
            volume = volume - 80;
        }
        
        this.Volume = volume;
        if(this.play == null){
            return;
        }

        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(this.Volume);
    }

    /**
     * 获取当前滚动到的歌词
     * @return 歌词
     */
    public String[] getLyric() {
        if(lyric == null){
            return new String[]{};
        }
        return lyric.getToLyric();
    }

    /**
     * 播放下一首
     */
    public void next(){
        this.play.stop();
        this.play.close();
    }

    /**
     * 播放上一首
     */
    public void prev(){
        this.playIn -= 2;
        if(this.playIn < -1){
            this.playIn = -1;
        }

        next();
    }

    /**
     * 跳转至...首播放
     * @param in 歌曲序号 (索引加一)
     */
    public void to(int in){
        in -= 1;
        if(in < 0){
            in = 0;
        }

        int maxIndex = this.playList.size() - 1;
        if(in > maxIndex){
            in = maxIndex;
        }

        this.playIn = in - 1;
        next();
    }

    /**
     * 退出播放
     */
    public void exit(){
        if (this.lyric != null){
            this.lyric.continues();
            this.lyric.exit();
        }

        this.loopPlayIn = false;
        next();
    }

    /**
     * 停止播放
     */
    public void stop(){
        if (this.lyric != null){
            this.lyric.stop();
        }

        synchronized(this){
            this.load = false;
            notifyAll();
        }
    }

    /**
     * 继续播放
     */
    public void continues(){
        if (this.lyric != null){
            this.lyric.continues();
        }
        this.startPlayingTime = System.currentTimeMillis() - this.playingProgress;

        synchronized(this){
            this.load = true;
            notifyAll();
        }
    }

    /**
     * 从播放列表中删除当前播放歌曲
     */
    public void deletePlayingMusic(){
        if (this.playListSize == 0) {
            if (this.playList.isEmpty()) {
                return;
            }

            this.playListSize = this.playList.size();
        }

        this.playList.remove(this.getPlayingMusic());
        this.playListSize -= 1;
        this.playIn -= 1;
        this.next();
    }

    /**
     * 正在播放 
     * @return 歌曲对象
     */
    public IMusic getPlayingMusic(){
        return this.playingMusic;
    }

    /**
     * 获取播放进度 (毫秒)
     * @return 毫秒
     */
    public long getPlayingProgress() {
        return this.playingProgress;
    }

    /**
     * 获取播放进度 (秒)
     * @return 秒
     */
    public int getPlayingProgressSecond() {
        return (int) (this.playingProgress / 1000);
    }

    /**
     * 获取播放进度 (字符串)
     * @return 播放进度字符串 (格式 "分:秒")
     */
    public String getPlayingProgressToString() {
        return Time.secondToString(this.getPlayingProgressSecond());
    }

    /**
     * 播放队列
     * @return 页对象
     */
    public Page playingAll(){
        return new Page(this.playList) {

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                if (data instanceof Music music){
                    newPageData.put("[" +(newPageData.size() + 1) + "] §b" + music.name + "§r§7 - "+ Music.getArtistsName(music.artists), "/cloudmusic to " + (this.limit * this.pageIn + newPageData.size() + 1));
                }

                if (data instanceof DjMusic music){
                    newPageData.put("[" +(newPageData.size() + 1) + "] §b" + music.name + "§r§7 - "+ music.dj.get("nickname").getAsString(), "/cloudmusic to " + (this.limit * this.pageIn + newPageData.size() + 1));
                }
                return newPageData;
            }
            
        };
    }
}
