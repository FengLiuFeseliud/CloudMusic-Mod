package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户对象
 */
public class User extends Music163Obj implements IPrint {
    protected final HttpClient api;
    public final long id;
    public final String name;
    public final String signature;
    public final int level;
    /**
     *  0: 无会员
     *
     *  11: VIP || SVIP
     */
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject djRadios = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        djRadios.get("name").getAsString(),
                                        djRadios.getAsJsonObject("dj").get("nickname").getAsString(),
                                        djRadios.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), djRadios.get("name").getAsString()),
                        "/cloudmusic dj " + djRadios.get("id").getAsLong()
                );
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
        source.sendFeedback(Text.literal("%s%s".formatted(this.name, this.vip > 0 ? "§7 - vip": "")));
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.translatable("cloudmusic.info.user.level", this.level));
        source.sendFeedback(Text.translatable("cloudmusic.info.user.id", this.id));

        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal("§7" + this.signature));

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("user.like", "/cloudmusic user like " + this.id),
                new TextClickItem("user.playlist", "/cloudmusic user playlist " + this.id),
                new TextClickItem("user.dj", "/cloudmusic user dj " + this.id),
                new TextClickItem("shar", Shares.USER.getShar(this.id))
        ));

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("record.all", "/cloudmusic user record all " + this.id),
                new TextClickItem("record.week", "/cloudmusic user record week " + this.id)
        ));
    }
    
}
