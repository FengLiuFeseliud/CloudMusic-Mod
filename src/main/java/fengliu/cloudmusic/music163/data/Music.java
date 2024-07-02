package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Music extends Music163Obj implements IMusic, ICanComment {
    public final long id;
    public final String name;
    public final String aliasName;
    public final JsonArray artists;
    public final JsonObject album;
    public final long duration;
    public final String picUrl;
    public JsonObject freeTrialInfo = null;

    public static String getArtistsName(JsonArray artists) {
        StringBuilder artistsName = new StringBuilder();
        for (JsonElement artistData : artists) {
            artistsName.append(((JsonObject) artistData).get("name").getAsString()).append("/");
        }
        return artistsName.substring(0, artistsName.length() - 1);
    }

    /**
     * 专辑歌曲没有 picUrl, 通过 cover 传入封面 picUrl
     */
    public Music(HttpClient api, JsonObject data, @Nullable String cover) {
        super(api, data);
        this.id = data.get("id").getAsLong();
        this.name = data.get("name").getAsString();
        
        JsonArray alias;
        if(data.has("alia")){
            alias = data.get("alia").getAsJsonArray();
        } else {
            alias = data.get("alias").getAsJsonArray();
        }
        
        if(alias.size() > 0){
            this.aliasName = alias.get(0).getAsString();
        }else{
            this.aliasName = "";
        }

        if(data.has("ar")){
            this.artists = data.get("ar").getAsJsonArray();
        }else{
            this.artists = data.get("artists").getAsJsonArray();
        }

        if(data.has("al")){
            this.album = data.get("al").getAsJsonObject();
        }else{
            this.album = data.get("album").getAsJsonObject();
        }
        
        if(data.has("dt")){
            this.duration = data.get("dt").getAsLong() / 1000;
        }else{
            this.duration = data.get("duration").getAsLong() / 1000;
        }

        if(this.album.has("picUrl")){
            this.picUrl = this.album.get("picUrl").getAsString();
        }else{
            this.picUrl = cover;
        }
    }

    /**
     * 红心歌曲
     */
    public void like(){
        Map<String, Object> data = new HashMap<>();
        data.put("alg", "itembased");
        data.put("trackId", this.id);
        data.put("like", true);
        data.put("time", 3);

        this.api.POST_API("/api/radio/like", data);
    }

    /**
     * 取消红心歌曲
     */
    public void unlike(){
        Map<String, Object> data = new HashMap<>();
        data.put("alg", "itembased");
        data.put("trackId", this.id);
        data.put("like", false);
        data.put("time", 3);

        this.api.POST_API("/api/radio/like", data);
    }

    /**
     * 将歌曲扔进垃圾桶 (优化推荐)
     */
    public void addTrashCan(){
        this.api.POST_API("/api/radio/trash/add?alg=RT&songId=" + this.id + "&time=25", null);
    }

    /**
     * 获取歌词
     * @return 滚动歌词对象
     */
    public Lyric lyric(){
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);
        data.put("lv", 0);
        data.put("tv", 0);

        return new Lyric(this.api.POST_API("/api/song/lyric", data));
    }

    /**
     * 获取相似歌曲
     * @return 页对象
     */
    public Page similar(){
        Map<String, Object> data = new HashMap<>();
        data.put("songid", this.id);
        data.put("limit", 50);
        data.put("offset", 0);

        JsonObject json = this.api.POST_API("/api/v1/discovery/simiSong", data);
        return new Page(json.getAsJsonArray("songs")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject music = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        music.get("name").getAsString(),
                                        Music.getArtistsName(music.getAsJsonArray("artists")),
                                        music.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), music.get("name").getAsString()),
                        "/cloudmusic music " + music.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 获取相似歌单
     * @return 页对象
     */
    public Page similarPlaylist(){
        Map<String, Object> data = new HashMap<>();
        data.put("songid", this.id);
        data.put("limit", 50);
        data.put("offset", 0);

        JsonObject json = this.api.POST_API("/api/discovery/simiPlaylist", data);
        return new Page(json.getAsJsonArray("playlists")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.getAsJsonObject("creator").get("nickname").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), playList.get("name").getAsString()),
                        "/cloudmusic playlist " + playList.get("id").getAsLong()
                );
            }
        };
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPicUrl() {
        return this.picUrl;
    }

    /**
     * 获得歌曲 url
     * @return 歌曲文件
     */
    public String getPlayUrl(){
        HttpClient playApi = new HttpClient("https://interface3.music.163.com", this.api.getHeader());
        Map<String, Object> data = new HashMap<>();
        data.put("ids", "[" + this.id +"]");
        data.put("level", Configs.PLAY.PLAY_QUALITY.getStringValue());
        data.put("encodeType", "flac");

        JsonObject result = playApi.POST_API("/api/song/enhance/player/url/v1", data);
        JsonObject music = result.get("data").getAsJsonArray().get(0).getAsJsonObject();
        if(music.get("code").getAsInt() != 200){
            throw new ActionException(Text.translatable("cloudmusic.exception.music.get.url", this.name));
        }

        if (!music.get("freeTrialInfo").isJsonNull()){
            this.freeTrialInfo = music.getAsJsonObject("freeTrialInfo");
        }
        return music.get("url").getAsString();
    }

    @Override
    public long getDuration() {
        return this.duration * 1000;
    }

    @Override
    public String getCommentId() {
        return "R_SO_4_%s".formatted(this.id);
    }

    @Override
    public ApiPage getComments(boolean hot) {
        return this.comments(this.api, this.id, hot);
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        if (this.aliasName.equals("")) {
            source.sendFeedback(Text.literal(this.name));
        } else {
            source.sendFeedback(Text.literal(this.name + " §7(" + this.aliasName + ")"));
        }
       
       source.sendFeedback(Text.literal(""));

        List<TextClickItem> artistsTexts = new ArrayList<>();
        for (JsonElement artistData : this.artists.asList()) {
            JsonObject artist = artistData.getAsJsonObject();
            artistsTexts.add(new TextClickItem(
                    Text.literal(artist.get("name").getAsString()),
                    Text.translatable(IdUtil.getShowInfo("music.artist")),
                    "/cloudmusic artist " + artist.get("id").getAsLong()
            ));
        }

        source.sendFeedback(TextClickItem.combine(
                "§f§l/",
                text -> text.setStyle(text.getStyle().withColor(Formatting.AQUA).withUnderline(true)),
                artistsTexts.toArray(new TextClickItem[]{})
        ));

        source.sendFeedback(new TextClickItem(
                "info.music.album",
                "/cloudmusic album " + this.album.get("id").getAsLong()
        ).append("§b§n" + this.album.get("name").getAsString()).build());

        source.sendFeedback(Text.translatable("cloudmusic.info.music.duration", this.getDurationToString()));
        source.sendFeedback(Text.translatable("cloudmusic.info.music.id", this.id));

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("play", "/cloudmusic music play " + this.id),
                new TextClickItem("similar.music", "/cloudmusic music similar music " + this.id),
                new TextClickItem("similar.playlist", "/cloudmusic music similar playlist " + this.id),
                new TextClickItem("like", "/cloudmusic music like " + this.id),
                new TextClickItem("unlike", "/cloudmusic music unlike " + this.id),
                new TextClickItem("shar", Shares.MUSIC.getShar(this.id))
        ));

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("hot.comment", "/cloudmusic music hotComment " + this.id),
                new TextClickItem("comment", "/cloudmusic music comment " + this.id),
                new TextClickItem("playlist.add", "/cloudmusic my playlist add " + this.id),
                new TextClickItem("playlist.del", "/cloudmusic my playlist del " + this.id)
        ));
    }
}
