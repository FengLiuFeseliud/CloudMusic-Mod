package fengliu.cloudmusic.util;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.ActionException;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.music163.Lyric;
import fengliu.cloudmusic.music163.data.DjMusic;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.util.page.Page;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

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
    protected boolean notExitFlag = true;
    private boolean load;
    private int volumePercentage;
    private long playingProgress;
    private long startPlayingTime;

    /**
     * 歌曲播放对象
     *
     * @param playList 歌曲列表
     */
    public MusicPlayer(List<IMusic> playList) {
        this.playListSize = playList.size();
        this.playList = playList;
        if (Configs.ENABLE.PLAY_AUTO_RANDOM.getBooleanValue()) {
            this.randomPlay();
        }

        this.volumeSet(Configs.PLAY.VOLUME.getIntegerValue());
    }

    public boolean isPlaying() {
        return this.loopPlayIn && this.load;
    }

    @Override
    public void run() {
        while (this.notExitFlag) {
            while (this.loopPlayIn) {
                for (; playIn < this.playListSize; this.playIn++) {
                    playMusic();

                    if (!this.loopPlayIn) {
                        break;
                    }

                    if (this.playIn == this.playListSize - 1 && !Configs.PLAY.PLAY_LOOP.getBooleanValue()) {
                        this.loopPlayIn = false;
                    }
                }

                if (!this.loopPlayIn) {
                    break;
                }
                this.playIn = 0;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * 启动歌曲播放
     */
    public void start() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("CloudMusicPlayer thread");
        thread.start();
    }

    /**
     * 播放歌曲
     */
    protected void playMusic() {
        // 开始播放的时候停止所有的声音(只会停止一瞬间)
        client.getSoundManager().stopAll();
        IMusic music = this.playList.get(this.playIn);

        String musicUrl;
        try {
            musicUrl = music.getPlayUrl();
        } catch (ActionException err) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(err.getMessage()), false);
            }
            this.stop();
            return;
        }

        if (music instanceof Music aMusic) {
            if (aMusic.freeTrialInfo != null && this.client.player != null) {
                this.client.player.sendMessage(
                        Text.translatable(
                                "cloudmusic.info.play.free.trial",
                                music.getName(),
                                aMusic.freeTrialInfo.get("start").getAsInt(),
                                aMusic.freeTrialInfo.get("end").getAsInt()
                        ), false);
            }
        }

        MusicIconTexture.getMusicIcon(music);
        if (music instanceof Music) {
            this.lyric = ((Music) music).lyric();
        } else {
            this.lyric = null;
        }

        this.playingMusic = music;
        if (!Configs.PLAY.PLAY_URL.getBooleanValue()) {
            String[] urls = musicUrl.split("\\.");
            String fileType = urls[urls.length - 1];

            File file;
            if (music instanceof DjMusic) {
                file = HttpClient.download(musicUrl, CloudMusicClient.cacheHelper.getWaitCacheFile("djmusic_" + music.getId() + "." + fileType));
            } else {
                file = HttpClient.download(musicUrl, CloudMusicClient.cacheHelper.getWaitCacheFile(music.getId() + "." + fileType));
            }

            CloudMusicClient.cacheHelper.addUseSize(file);
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.getName()), false);
            this.play(file);
        } else {
            this.client.inGameHud.setOverlayMessage(Text.translatable("record.nowPlaying", music.getName()), false);
            this.play(musicUrl);
        }
    }

    /**
     * 播放歌曲
     */
    private void play(AudioInputStream audioInputStream) throws IOException, InterruptedException, LineUnavailableException {
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
        this.volumeSet(volumePercentage);

        play.start();
        if (lyric != null) {
            this.lyric.start();
        }

        int count;
        byte[] tempBuff = new byte[1024];

        this.load = true;
        this.startPlayingTime = System.currentTimeMillis();
        while ((count = audioInputStream.read(tempBuff, 0, tempBuff.length)) != -1) {
            synchronized (this) {
                while (!load)
                    wait();
            }
            play.write(tempBuff, 0, count);
            this.playingProgress = System.currentTimeMillis() - this.startPlayingTime;
        }

        this.playingProgress = 0;
        if (lyric != null) {
            this.lyric.exit();
        }
    }

    /**
     * 通过 URL 播放歌曲
     *
     * @param url 歌曲 url
     */
    private void play(String url) {
        try {
            this.play(AudioSystem.getAudioInputStream(AudioSystem.getAudioInputStream(new URL(url))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件对象播放歌曲
     *
     * @param file 文件对象
     */
    private void play(File file) {
        try {
            this.play(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音量增益
     *
     * @param volume 音量百分比
     */
    public void volumeSet(int volume) {
        if (volume < 0) {
            volume = 0;
        }

        if (volume > 100) {
            volume = 100;
        }

        this.volumePercentage = volume;
        if (this.play == null) {
            return;
        }

        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gainControl.getMinimum() * (1 - volume / 100.0f));

        Configs.PLAY.VOLUME.setIntegerValue(this.volumePercentage);
        Configs.INSTANCE.save();
    }

    public void volumeAdd() {
        this.volumeSet(++this.volumePercentage);
    }

    public void volumeDown() {
        this.volumeSet(--this.volumePercentage);
    }

    /**
     * 获取当前滚动到的歌词
     *
     * @return 歌词
     */
    public String[] getLyric() {
        if (lyric == null) {
            return new String[]{};
        }
        return lyric.getToLyric();
    }

    /**
     * 播放下一首
     */
    public void next() {
        if (this.playList.isEmpty()) {
            return;
        }

        this.play.stop();
        this.play.close();
    }

    /**
     * 播放上一首
     */
    public void prev() {
        this.playIn -= 2;
        if (this.playIn < -1) {
            this.playIn = -1;
        }

        next();
    }

    /**
     * 跳转至...首播放
     *
     * @param in 歌曲序号 (索引加一)
     */
    public void to(int in) {
        in -= 1;
        if (in < 0) {
            in = 0;
        }

        int maxIndex = this.playList.size() - 1;
        if (in > maxIndex) {
            in = maxIndex;
        }

        this.playIn = in - 1;
        next();
    }

    /**
     * 退出播放
     */
    public void exit() {
        if (this.lyric != null) {
            this.lyric.continues();
            this.lyric.exit();
        }

        this.loopPlayIn = false;
        this.notExitFlag = false;
        next();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (this.lyric != null) {
            this.lyric.stop();
        }

        synchronized (this) {
            this.load = false;
            notifyAll();
        }
        this.loopPlayIn = false;
        this.clearPlayingMusic();
    }

    /**
     * 让正在播放的音乐为null, 以便停止渲染
     */
    private void clearPlayingMusic() {
        this.playingMusic = null;
    }

    /**
     * 继续播放
     */
    public void continues() {
        if (this.lyric != null) {
            this.lyric.continues();
        }
        this.startPlayingTime = System.currentTimeMillis() - this.playingProgress;

        synchronized (this) {
            this.load = true;
            notifyAll();
        }
        this.loopPlayIn = true;
    }

    /**
     * 从播放列表中删除当前播放歌曲
     */
    public void deletePlayingMusic() {
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
     *
     * @return 歌曲对象
     */
    public IMusic getPlayingMusic() {
        return this.playingMusic;
    }

    /**
     * 获取播放进度 (毫秒)
     *
     * @return 毫秒
     */
    public long getPlayingProgress() {
        return this.playingProgress;
    }

    /**
     * 获取播放进度 (秒)
     *
     * @return 秒
     */
    public int getPlayingProgressSecond() {
        return (int) (this.playingProgress / 1000);
    }

    /**
     * 获取播放进度 (字符串)
     *
     * @return 播放进度字符串 (格式 "分:秒")
     */
    public String getPlayingProgressToString() {
        return Time.secondToString(this.getPlayingProgressSecond());
    }

    /**
     * 播放队列
     *
     * @return 页对象
     */
    public Page playingAll() {
        return new Page(this.playList) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                if (data instanceof Music music) {
                    return new TextClickItem(
                            Text.literal("§b%s §r§7 - %s".formatted(music.name, Music.getArtistsName(music.artists))),
                            Text.translatable(IdUtil.getShowInfo("page.player.to"), music.name),
                            "/cloudmusic to " + (this.limit * this.pageIn + this.data.get(this.pageIn).indexOf(data) + 1)
                    );
                }

                if (data instanceof DjMusic music) {
                    return new TextClickItem(
                            Text.literal("§b%s §r§7 - %s".formatted(music.name, music.dj.get("nickname").getAsString())),
                            Text.translatable(IdUtil.getShowInfo("page.player.to"), music.name),
                            "/cloudmusic to " + (this.limit * this.pageIn + this.data.get(this.pageIn).indexOf(data) + 1)
                    );
                }

                return null;
            }

        };
    }

    /**
     * 将播放队列随机并重新播放
     */
    public void randomPlay() {
        Collections.shuffle(this.playList);
        if (!this.isPlaying()) {
            return;
        }

        this.playIn -= 1;
        this.next();
    }

}
