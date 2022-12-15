package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class User implements MusicList {
    protected final HttpClient api;
    public final long id;
    public final String name;
    public final String signature;
    public final int level;
    public final int vip;
    private long likePlayListId = 0;

    public User(HttpClient api, JsonObject data) {
        this.api = api;
        if(data.has("profile")){
            data = data.get("profile").getAsJsonObject();
        }

        this.id = data.get("userId").getAsLong();
        this.name = data.get("nickname").getAsString();
        this.signature = data.get("signature").getAsString();
        if(data.has("level")){
            this.level = data.get("level").getAsInt();
        }else{
            this.level = 0;
        }
        this.vip = data.get("vipType").getAsInt();
    }

    private long getLikePlayListId(){
        if(this.likePlayListId != 0){
            return this.likePlayListId;
        }
        this.likePlayListId = this.playLists(0, 1).get(0).id;
        return this.likePlayListId;
    }

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
