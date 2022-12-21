package fengliu.cloudmusic.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jetbrains.annotations.Nullable;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.client.command.MusicCommand;
import fengliu.cloudmusic.util.music163.Music;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MusicPlayer implements Runnable {
    private int volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
    private final MinecraftClient client;
    private final List<Music> playList;
    private SourceDataLine play;
    private int playIn = 0;
    private boolean loopPlayIn = true;
    private boolean loopPlay;
    private boolean load;
    private int Volume;

    public static int toVolume(int volumePercentage){
        if(volumePercentage < 0){
            volumePercentage = 0;
        }

        if(volumePercentage > 100){
            volumePercentage = 100;
        }

        return (int) (86 * 0.01 * volumePercentage);
    }

    public MusicPlayer(List<Music> playList, boolean loopPlay){
        this.client = MinecraftClient.getInstance();
        this.playList = playList;

        this.volumeSet(toVolume(this.volumePercentage));
    }

    @Override
    public void run() {
        int size = this.playList.size();

        while(this.loopPlayIn){
            for (this.playIn = 0; playIn < size; this.playIn++) {
                Music music = this.playList.get(playIn);

                this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.name), false);
                if(!MusicCommand.isPlayUrl()){
                    File file = HttpClient.download(music.getPlayUrl(0), CloudMusicClient.cacheHelper.getWaitCacheFile(music.id + ".mp3"));
                    CloudMusicClient.cacheHelper.addUseSize(file);
                    this.play(file);
                }else{
                    this.play(music.getPlayUrl(0));
                }

                if(!this.loopPlayIn){
                    break;
                }

                if(this.playIn == this.playList.size() - 1 && !this.loopPlay){
                    this.loopPlayIn = false;
                }
            }
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("CloudMusicPlayer thread");
        thread.start();
    }

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
        play.drain();
        play.stop();
        play.close();
    }

    public void play(String url){
        try {
            this.play(AudioSystem.getAudioInputStream(AudioSystem.getAudioInputStream(new URL(url))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(File file){
        try {
            this.play(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void down(){
        this.play.stop();
        this.play.close();
    }

    public void up(){
        this.playIn -= 2;
        if(this.playIn < -1){
            this.playIn = -1;
        }

        down();
    }

    public void exit(){
        this.loopPlayIn = false;
        down();
    }

    public void stop(){
        synchronized(this){
            this.load = false;
            notifyAll();
        }
    }

    public void continues(){
        synchronized(this){
            this.load = true;
            notifyAll();
        }
    }

    public Music playing(){
        return this.playList.get(this.playIn);
    }

    public static class MusicPlayList{
        private List<Music> musicList;

        public MusicPlayList(List<Music> musicList){
            this.musicList = musicList;
        }
        
        public MusicPlayList(Music music){
            this.musicList = new ArrayList<>();
            this.musicList.add(music);
        }

        public void addMusic(Music music){
            this.musicList.add(music);
        }

        public void addMusics(List<Music> musicList){
            this.musicList.addAll(musicList);
        }

        public MusicPlayer createMusicPlayer(@Nullable boolean loopPlay){
            return new MusicPlayer(musicList, loopPlay);
        }
        
    }
}
