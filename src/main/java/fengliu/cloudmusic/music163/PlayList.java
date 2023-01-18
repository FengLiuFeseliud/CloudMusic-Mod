package fengliu.cloudmusic.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * 歌单对象
 */
public class PlayList extends Music163Obj implements IMusicList, ICanSubscribe {
    public final long id;
    public final String name;
    public final String cover;
    public final int count;
    public final long playCount;
    public final JsonObject creator;
    public final String[] description;
    public final JsonArray tags;
    public final String tagsStr;
    private final JsonArray tracks;
    private List<IMusic> musics;

    public PlayList(HttpClient api, JsonObject data) {
        super(api, data);

        JsonObject playlist;
        if(data.has("playlist")){
            playlist = data.get("playlist").getAsJsonObject();
        }else{
            playlist = data;
        }

        this.id = playlist.get("id").getAsLong();
        this.name = playlist.get("name").getAsString();
        this.cover = playlist.get("coverImgUrl").getAsString();
        this.count = playlist.get("trackCount").getAsInt();
        this.playCount = playlist.get("playCount").getAsLong();
        this.creator = playlist.get("creator").getAsJsonObject();
        if(!playlist.get("description").isJsonNull()){
            this.description = playlist.get("description").getAsString().split("\n");
        }else{
            this.description = null;
        }
        this.tags = playlist.get("tags").getAsJsonArray();

        String[] _tagsStr = {""};
        this.tags.forEach(element -> {
            _tagsStr[0] += element.getAsString() + "/";
        });
        if(!_tagsStr[0].isEmpty()){
            this.tagsStr = (String) _tagsStr[0].subSequence(0, _tagsStr[0].length() - 1);
        }else{
            this.tagsStr = "";
        }

        if(!playlist.get("tracks").isJsonNull()){
            this.tracks = playlist.get("tracks").getAsJsonArray();
        }else{
            this.tracks = new JsonArray();
        }
        
    }

    public void add(long musicId){
        Map<String, Object> data = new HashMap<>();
        data.put("op", "add");
        data.put("pid", this.id);
        data.put("trackIds", "[" + musicId + "]");
        data.put("imme", "true");

        this.api.POST_API("/api/playlist/manipulate/tracks", data);
    }

    public void del(long musicId){
        Map<String, Object> data = new HashMap<>();
        data.put("op", "del");
        data.put("pid", this.id);
        data.put("trackIds", "[" + musicId + "]");
        data.put("imme", "true");

        this.api.POST_API("/api/playlist/manipulate/tracks", data);
    }

    @Override
    public List<IMusic> getMusics(){
        if(this.musics != null){
            return this.musics;
        }

        this.musics = new ArrayList<>();
        this.tracks.forEach(element -> {
            this.musics.add(new Music(api, element.getAsJsonObject(), null));
        });
        return this.musics;
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(TextClick.suggestText("cloudmusic.info.playlist.creator", "§b" + this.creator.get("nickname").getAsString(), "/cloudmusic user " + this.creator.get("userId").getAsLong()));
        if(!this.tagsStr.isEmpty()){
            source.sendFeedback(Text.translatable("cloudmusic.info.playlist.tags", this.tagsStr));
        }
        source.sendFeedback(Text.translatable("cloudmusic.info.playlist.count", this.count, this.playCount));
        source.sendFeedback(Text.translatable("cloudmusic.info.playlist.id", this.id));

        if(this.description != null){
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic playlist play " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic platlist subscribe " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.unsubscribe").getString(), "/cloudmusic platlist unsubscribe " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/playlist/subscribe", data);
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/playlist/unsubscribe", data);
    }
    
}
