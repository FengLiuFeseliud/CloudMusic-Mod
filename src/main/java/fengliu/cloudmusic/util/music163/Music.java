package fengliu.cloudmusic.util.music163;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Music extends Music163Object implements PrintObject {
    public final long id;
    public final String name;
    public final String aliasName;
    public final Map<String, Long> artists;
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

        Map<String, Long> artists = new HashMap<>();
        music.get("ar").getAsJsonArray().forEach(element -> {
            JsonObject artist = element.getAsJsonObject();
            artists.put(artist.get("name").getAsString(), artist.get("id").getAsLong());
        });
        this.artists = artists;

        JsonObject album = music.get("al").getAsJsonObject();
        this.albumId = album.get("id").getAsLong();
        this.albumName = album.get("name").getAsString();
        this.duration = music.get("dt").getAsLong() / 1000;
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

       String artistName = "";
       for (String artist : this.artists.keySet()) {
            artistName += artist + "/";
       }
       artistName = (String) artistName.subSequence(0, artistName.length() - 1);
       source.sendFeedback(Text.translatable("cloudmusic.info.music.artist", artistName));
       source.sendFeedback(Text.translatable("cloudmusic.info.music.album", this.albumName));
       source.sendFeedback(Text.translatable("cloudmusic.info.music.id", this.id));
    }
    
}
