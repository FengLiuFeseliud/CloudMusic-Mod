package fengliu.cloudmusic.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * 用户对象
 */
public class User implements MusicList {
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

    /**
     * 用户歌单
     * @return 页对象
     */
    public ApiPage playListsPage(){
        Map<String, Object> postData = new HashMap<String, Object>();
        postData.put("uid", this.id);
        postData.put("limit", 24);
        postData.put("offset", 0);
        postData.put("includeVideo", true);

        JsonObject data = this.api.POST_API("/api/user/playlist", postData);
        return new ApiPage(data.getAsJsonArray("playlist"), this.playlistCount, "/api/user/playlist", this.api, postData) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("playlist");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = ((JsonElement) data).getAsJsonObject(); 
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + playList.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * 用户喜欢音乐歌单
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
    }

    @Override
    public List<Music> getMusics() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
