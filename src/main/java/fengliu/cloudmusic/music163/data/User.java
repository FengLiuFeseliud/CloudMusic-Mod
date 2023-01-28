package fengliu.cloudmusic.music163.data;

import java.util.*;

import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.music163.data.PlayList;
import fengliu.cloudmusic.util.TextClick;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * 用户对象
 */
public class User extends Music163Obj implements IPrint {
    protected final HttpClient api;
    public final long id;
    public final String name;
    public final String signature;
    public final int level;
    public final int vip;
    public final long listenSongs;
    public final int playlistCount;
    public final int createTime;
    public final int createDay;
    private long likePlayListId = 0;

    public User(HttpClient api, JsonObject data) {
        super(api, data);
        this.api = api;
        JsonObject profile = data.getAsJsonObject("profile");

        this.id = profile.get("userId").getAsLong();
        this.name = profile.get("nickname").getAsString();
        this.signature = profile.get("signature").getAsString();
        this.vip = profile.get("vipType").getAsInt();
        this.playlistCount = profile.get("playlistCount").getAsInt();
        this.listenSongs = data.get("listenSongs").getAsInt();
        this.level = data.get("level").getAsInt();
        this.createTime = data.get("createTime").getAsInt();
        this.createDay = data.get("createDays").getAsInt();
    }

    private long getLikePlayListId(){
        if(this.likePlayListId != 0){
            return this.likePlayListId;
        }
        this.likePlayListId = this.playLists(0, 1).get(0).id;
        return this.likePlayListId;
    }
    
    /**
     * 用户歌单
     * @return 歌单列表
     */
    public List<PlayList> playLists(int page, @Nullable int limit){
        if(limit == 0){
            limit = 30;
        }

        Map<String, Object> postData = new HashMap<String, Object>();
        postData.put("uid", this.id);
        postData.put("limit", limit);
        postData.put("offset", limit * page);
        postData.put("includeVideo", true);

        JsonObject data = this.api.POST_API("/api/user/playlist", postData);

        List<PlayList> playLists = new ArrayList<>();
        data.get("playlist").getAsJsonArray().forEach(playList -> {
            playLists.add(new PlayList(this.api, playList.getAsJsonObject()));
        });

        return playLists;
    }

    protected Object[] getPlayListPageData(){
        Map<String, Object> postData = new HashMap<String, Object>();
        postData.put("uid", this.id);
        postData.put("limit", 24);
        postData.put("offset", 0);
        postData.put("includeVideo", true);

       return new Object[]{this.api.POST_API("/api/user/playlist", postData), postData};
    }

    /**
     * 用户歌单
     * @return 页对象
     */
    public ApiPage playListsPage(){
        Object[] data = this.getPlayListPageData();
        return new ApiPage(((JsonObject) data[0]).getAsJsonArray("playlist"), this.playlistCount, "/api/user/playlist", this.api, (Map<String, Object>) data[1]) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("playlist");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r§7 - "+ playList.getAsJsonObject("creator").get("nickname").getAsString() +" - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + playList.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * 用户创建的电台
     * @return 页对象
     */
    public ApiPage djRadio(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userId", this.id);

        JsonObject json = this.api.POST_API("/api/djradio/get/byuser", data);
        return new ApiPage(json.getAsJsonArray("djRadios"), json.get("count").getAsInt(), "/api/user/playlist", this.api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("djRadios");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject djRadios = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + djRadios.get("name").getAsString() + "§r§7 - "+ djRadios.getAsJsonObject("dj").get("nickname").getAsString() +" - id: " + djRadios.get("id").getAsLong(), "/cloudmusic dj " + djRadios.get("id").getAsLong());
                return newPageData;
            }
        };
    }

    /**
     * 听歌榜单(所有时间)
     * @return 歌曲列表
     */
    public List<IMusic> recordAll(){
        Map<String, Object> postData = new HashMap<String, Object>();
        postData.put("uid", this.id);
        postData.put("limit", 100);
        postData.put("type", 0);

        JsonObject data = this.api.POST_API("/api/v1/play/record", postData);

        List<IMusic> musics = new ArrayList<>();
        data.getAsJsonArray("allData").forEach(musicData -> {
            musics.add(new Music(this.api, ((JsonObject) musicData).getAsJsonObject("song"), null));
        });
        return musics;
    }

    /**
     * 听歌榜单(周)
     * @return 歌曲列表
     */
    public List<IMusic> recordWeek(){
        Map<String, Object> postData = new HashMap<String, Object>();
        postData.put("uid", this.id);
        postData.put("limit", 100);
        postData.put("type", 1);

        JsonObject data = this.api.POST_API("/api/v1/play/record", postData);

        List<IMusic> musics = new ArrayList<>();
        data.getAsJsonArray("weekData").forEach(musicData -> {
            musics.add(new Music(this.api, ((JsonObject) musicData).getAsJsonObject("song"), null));
        });
        return musics;
    }

    /**
     * 用户喜欢歌曲歌单
     * @return 歌单
     */
    public PlayList likeMusicPlayList(){
        return (new Music163(this.api.getCookies())).playlist(getLikePlayListId());
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal(this.name));
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.translatable("cloudmusic.info.user.level", this.level));
        source.sendFeedback(Text.translatable("cloudmusic.info.user.vip", this.vip));
        source.sendFeedback(Text.translatable("cloudmusic.info.user.id", this.id));
        
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal("§7" + this.signature));

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.user.like").getString(), "/cloudmusic user like " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.user.playlist").getString(), "/cloudmusic user playlist " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.user.dj").getString(), "/cloudmusic user dj " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.shar").getString(), Shares.USER.getShar(this.id));
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));

        optionsTextData.clear();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.record.all").getString(), "/cloudmusic user record all " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.record.week").getString(), "/cloudmusic user record week " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
    
}
