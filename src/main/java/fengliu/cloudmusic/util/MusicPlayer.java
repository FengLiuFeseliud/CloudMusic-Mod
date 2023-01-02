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
import fengliu.cloudmusic.client.command.MusicCommand;
import fengliu.cloudmusic.music163.Lyric;
import fengliu.cloudmusic.music163.Music;
import fengliu.cloudmusic.util.page.Page;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * 音乐播放对象
 */
public class MusicPlayer implements Runnable {
    private int volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
    private final MinecraftClient client;
    protected final List<Music> playList;
    private SourceDataLine play;
    private Lyric lyric;
    protected int playIn = 0;
    protected boolean loopPlayIn = true;
    private boolean load;
    private int Volume;

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
     * 音乐播放对象
     * @param playList 音乐列表
     * @param loopPlay 
     */
    public MusicPlayer(List<Music> playList){
        this.client = MinecraftClient.getInstance();
        this.playList = playList;

        this.volumeSet(toVolume(this.volumePercentage));
    }

    @Override
    public void run() {
        int size = this.playList.size();

        while(this.loopPlayIn){
            for (this.playIn = 0; playIn < size; this.playIn++) {
                playMusic();

                if(!this.loopPlayIn){
                    break;
                }

                if(this.playIn == this.playList.size() - 1 && !MusicCommand.isLoopPlay()){
                    this.loopPlayIn = false;
                }
            }
        }
    }

    /**
     * 启动音乐播放
     */
    public void start(){
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("CloudMusicPlayer thread");
        thread.start();
    }

    /**
     * 播放音乐
     */
    protected void playMusic(){
        Music music = this.playList.get(this.playIn);
        this.lyric = music.lyric();

        if(!MusicCommand.isPlayUrl()){
            File file = HttpClient.download(music.getPlayUrl(0), CloudMusicClient.cacheHelper.getWaitCacheFile(music.id + ".mp3"));
            CloudMusicClient.cacheHelper.addUseSize(file);
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.name), false);
            this.play(file);
        }else {
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.name), false);
            this.play(music.getPlayUrl(0));
        }
    }

    /**
     * 播放音乐
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
        this.lyric.start();

        int count;
        byte tempBuff[] = new byte[1024];

        this.load = true;
        while((count = audioInputStream.read(tempBuff,0,tempBuff.length)) != -1){
            synchronized(this){
            while(!load)
                wait();
            }
            play.write(tempBuff,0,count);

        }

        this.lyric.exit();
        play.drain();
        play.stop();
        play.close();
    }

    /**
     * 通过 URL 播放音乐
     * @param url 音乐 url
     */
    public void play(String url){
        try {
            this.play(AudioSystem.getAudioInputStream(AudioSystem.getAudioInputStream(new URL(url))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件对象播放音乐
     * @param file 文件对象
     */
    public void play(File file){
        try {
            this.play(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音量增益
     * @param volume 音量增益
     * @return 音量增益
     */
    public int volumeSet(int volume){
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
            return this.Volume;
        }

        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(this.Volume);
        return this.Volume;
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
    public void down(){
        this.play.stop();
        this.play.close();
    }

    /**
     * 播放上一首
     */
    public void up(){
        this.playIn -= 2;
        if(this.playIn < -1){
            this.playIn = -1;
        }

        down();
    }

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
        down();
    }

    /**
     * 退出播放
     */
    public void exit(){
        this.lyric.continues();
        this.lyric.exit();

        this.loopPlayIn = false;
        down();
    }

    /**
     * 停止播放
     */
    public void stop(){
        this.lyric.stop();
        synchronized(this){
            this.load = false;
            notifyAll();
        }
    }

    /**
     * 继续播放
     */
    public void continues(){
        this.lyric.continues();
        synchronized(this){
            this.load = true;
            notifyAll();
        }
    }

    /**
     * 正在播放 
     * @return 音乐对象
     */
    public Music playing(){
        if(this.playList.isEmpty()){
            return null;
        }
        
        try {
            return this.playList.get(this.playIn);
        } catch (Exception err) {
            return null;
        }
    }

    public Page playingAll(){
        return new Page(this.playList) {

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                Music music = (Music) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + music.name + "§r - " + music.artists.get(0).getAsJsonObject().get("name").getAsString(), "/cloudmusic to " + (this.limit * this.pageIn + newPageData.size() + 1));
                return newPageData;
            }
            
        };
    }
}
