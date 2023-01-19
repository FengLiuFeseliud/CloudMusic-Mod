package fengliu.cloudmusic.music163;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

import fengliu.cloudmusic.util.page.Page;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Music extends Music163Obj implements IMusic {
    public final long id;
    public final String name;
    public final String aliasName;
    public final JsonArray artists;
    public final JsonObject album;
    public final long duration;
    public final String picUrl;

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
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject music = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + music.get("name").getAsString() + "§r§7  - " + Music.getArtistsName(music.getAsJsonArray("artists")) + " - id: " + music.get("id").getAsLong(), "/cloudmusic music " + music.get("id").getAsLong());
                return newPageData;
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
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r§7 - "+ playList.getAsJsonObject("creator").get("nickname").getAsString() +" - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + playList.get("id").getAsLong());
                return newPageData;
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
     * @param br
     * @return
     */
    public String getPlayUrl(int br){
        if(br == 0){
            br = 999000;
        }
        
        HttpClient playApi = new HttpClient("https://interface3.music.163.com", this.api.getHeader());
        Map<String, Object> data = new HashMap<>();
        data.put("ids", "[" + this.id +"]");
        data.put("br", br);

        JsonObject result = playApi.POST_API("/api/song/enhance/player/url", data);
        JsonObject music = result.get("data").getAsJsonArray().get(0).getAsJsonObject();
        if(music.get("code").getAsInt() != 200){
            throw new ActionException(Text.translatable("cloudmusic.exception.music.get.url", this.name));
        }
        return music.get("url").getAsString();
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
       source.sendFeedback(Text.literal(""));

       if(this.aliasName.equals("")){
            source.sendFeedback(Text.literal(this.name));
       }else{
            source.sendFeedback(Text.literal(this.name + " §7(" + this.aliasName + ")"));
       }
       
       source.sendFeedback(Text.literal(""));

       Map<String, String> artistsTextData = new LinkedHashMap<>();
       for (JsonElement artistData : this.artists.asList()) {
            JsonObject artist = artistData.getAsJsonObject();
            artistsTextData.put("§b§n" + artist.get("name").getAsString(), "/cloudmusic artist " + artist.get("id").getAsLong());
       }
       
       source.sendFeedback(TextClick.suggestTextMap("cloudmusic.info.music.artist", artistsTextData, "§f§l/"));
       source.sendFeedback(TextClick.suggestText("cloudmusic.info.music.album", "§b" + this.album.get("name").getAsString(), "/cloudmusic album " + this.album.get("id").getAsLong()));
       source.sendFeedback(Text.translatable("cloudmusic.info.music.id", this.id));

       Map<String, String> optionsTextData = new LinkedHashMap<>();
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic music play " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.similar.music").getString(), "/cloudmusic music similar music " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.similar.playlist").getString(), "/cloudmusic music similar playlist " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.like").getString(), "/cloudmusic music like " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.unlike").getString(), "/cloudmusic music unlike " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.shar").getString(), Shares.MUSIC.getShar(this.id));
       source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));

        optionsTextData.clear();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.playlist.add").getString(), "/cloudmusic my playlist add " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.playlist.del").getString(), "/cloudmusic my playlist del " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
    
}
