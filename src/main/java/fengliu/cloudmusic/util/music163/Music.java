package fengliu.cloudmusic.util.music163;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Music extends Music163Object implements PrintObject {
    public final long id;
    public final String name;
    public final String aliasName;
    public final JsonArray artists;
    public final long albumId;
    public final String albumName;
    public final long duration;

    public Music(HttpClient api, JsonObject data) {
        super(api, data);

        JsonObject music;
        if(data.get("songs") != null){
            music = data.get("songs").getAsJsonArray().get(0).getAsJsonObject();
        }else{
            music = data;
        }
        
        this.id = music.get("id").getAsLong();
        this.name = music.get("name").getAsString();
        
        JsonArray alias = music.get("alia").getAsJsonArray();
        if(alias.size() > 0){
            this.aliasName = alias.get(0).getAsString();
        }else{
            this.aliasName = "";
        }

        this.artists = music.get("ar").getAsJsonArray();

        JsonObject album = music.get("al").getAsJsonObject();
        this.albumId = album.get("id").getAsLong();
        this.albumName = album.get("name").getAsString();
        this.duration = music.get("dt").getAsLong() / 1000;
    }

    public JsonObject like(boolean like){
        Map<String, Object> data = new HashMap<>();
        data.put("alg", "itembased");
        data.put("trackId", this.id);
        data.put("like", like);
        data.put("time", 3);

        JsonObject result = this.api.POST_API("/api/radio/like", data);
        return result;
    }

    public String getPlayUrl(@Nullable int br){
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
            return null;
        }
        return music.get("url").getAsString();
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
       source.sendFeedback(Text.literal(""));

       if(this.aliasName == ""){
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
       source.sendFeedback(TextClick.suggestText("cloudmusic.info.music.album", "§b" + this.albumName, "/cloudmusic album " + this.albumId));
       source.sendFeedback(Text.translatable("cloudmusic.info.music.id", this.id));

       Map<String, String> optionsTextData = new LinkedHashMap<>();
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic music play " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.like").getString(), "/cloudmusic music like " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic subscribe " + this.id);
       source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
    
}
