package fengliu.cloudmusic.music163;

import net.minecraft.text.Text;

/**
 * 分享类别
 */
public enum Shares {
    MUSIC ("cloudmusic.shar.music", "/cloudmusic music"),
    ALBUM ("cloudmusic.shar.album", "/cloudmusic album"),
    ARTIST ("cloudmusic.shar.artist", "/cloudmusic artist"),
    DJ_RADIO ("cloudmusic.shar.dj.radio", "/cloudmusic dj"),
    PLAY_LIST ("cloudmusic.shar.playlist", "/cloudmusic playlist"),
    USER ("cloudmusic.shar.user", "/cloudmusic user"),
    STYLE ("cloudmusic.shar.style", "/cloudmusic style");

    private final String translationKey;
    private final String command;

    /**
     * 设置分享类别
     * @param translationKey 分享类别名语言文件 key
     * @param command 分享类别的获取资源指令
     */
    Shares(String translationKey, String command){
        this.translationKey = translationKey;
        this.command = command;
    }

    /**
     * 判断字符串是否该分享类别一致
     * @param sharText 分享类别名
     * @return 一致 true
     */
    public boolean isShar(String sharText){
        return Text.translatable(translationKey).getString().equals(sharText);
    }

    /**
     * 获取分享消息文本
     * @param id 分享资源 id
     * @return 分享消息字符串
     */
    public String getShar(long id){
        return "CloudMusic# " + Text.translatable(translationKey).getString() + " id: " + id;
    }

    /**
     * 获取分享类别的完整获取资源指令
     * @param id 分享资源 id
     * @return 获取资源指令字符串
     */
    public String getCommand(long id){
        return command + " " + id;
    }
}
