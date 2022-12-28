package fengliu.cloudmusic.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * 专辑对象
 */
public class Album extends Music163Object implements MusicList, CanSubscribeObject {
    public final long id;
    public final String name;
    public final String cover;
    public final JsonArray alias;
    public final JsonArray artists;
    public final int size;
    public final String[] description;
    private JsonArray songs;
    private List<Music> musics;

    public Album(HttpClient api, JsonObject album) {
        super(api, album);
        this.songs = album.get("songs").getAsJsonArray();
        album = album.get("album").getAsJsonObject();

        this.id = album.get("id").getAsLong();
        this.name = album.get("name").getAsString();
        this.cover = album.get("picUrl").getAsString();
        this.size = album.get("size").getAsInt();
        this.artists = album.get("artists").getAsJsonArray();
        this.alias = album.get("alias").getAsJsonArray();
        if(!album.get("description").isJsonNull()){
            this.description = album.get("description").getAsString().split("\n");
        } else {
            this.description = null;
        }
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        if(this.alias.size() == 0){
            source.sendFeedback(Text.literal(this.name));
        }else{
            String aliasName = "";
            for(JsonElement alia: this.alias.asList()){
                aliasName += alia.getAsString() + " / ";
            }
            aliasName = aliasName.substring(0, aliasName.length() - 3);

            source.sendFeedback(Text.literal(this.name + " §7(" + aliasName + ")"));
        }

        source.sendFeedback(Text.literal(""));
        
        Map<String, String> artistsTextData = new LinkedHashMap<>();
        for (JsonElement artistData : this.artists.asList()) {
                JsonObject artist = artistData.getAsJsonObject();
                artistsTextData.put("§b§n" + artist.get("name").getAsString(), "/cloudmusic artist " + artist.get("id").getAsLong());
        }

        source.sendFeedback(TextClick.suggestTextMap(artistsTextData, "§f§l/"));
        source.sendFeedback(Text.translatable("cloudmusic.info.album.id", this.id));
        source.sendFeedback(Text.translatable("cloudmusic.info.album.size", this.size));
        
        if(this.description != null){
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic album play " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic album subscribe " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.unsubscribe").getString(), "/cloudmusic album unsubscribe " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }

    @Override
    public List<Music> getMusics() {
        if(this.musics != null){
            return this.musics;
        }

        this.musics = new ArrayList<>();
        this.songs.forEach(element -> {
            this.musics.add(new Music(api, element.getAsJsonObject(), this.cover));
        });
        return this.musics;
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/album/sub", data);
        if(json.has("message")){
            throw new ActionException(json.get("message").getAsString());
        }
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/album/unsub", data);
        if(json.has("message")){
            throw new ActionException(json.get("message").getAsString());
        }
    }
    
}
