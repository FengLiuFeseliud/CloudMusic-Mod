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
    public final JsonObject album;
    public final long duration;
    public final String picUrl;

    /**
     * 专辑歌曲没有 picUrl, 通过 cover 传入封面 picUrl
     */
    public Music(HttpClient api, JsonObject data, @Nullable String cover) {
        super(api, data);

        JsonObject music;
        if(data.get("songs") != null){
            music = data.get("songs").getAsJsonArray().get(0).getAsJsonObject();
        }else{
            music = data;
        }
        
        this.id = music.get("id").getAsLong();
        this.name = music.get("name").getAsString();
        
        JsonArray alias;
        if(music.has("alia")){
            alias = music.get("alia").getAsJsonArray();
        } else {
            alias = music.get("alias").getAsJsonArray();
        }
        
        if(alias.size() > 0){
            this.aliasName = alias.get(0).getAsString();
        }else{
            this.aliasName = "";
        }

        if(music.has("ar")){
            this.artists = music.get("ar").getAsJsonArray();
        }else{
            this.artists = music.get("artists").getAsJsonArray();
        }

        if(music.has("al")){
            this.album = music.get("al").getAsJsonObject();
        }else{
            this.album = music.get("album").getAsJsonObject();
        }
        
        if(music.has("dt")){
            this.duration = music.get("dt").getAsLong() / 1000;
        }else{
            this.duration = music.get("duration").getAsLong() / 1000;
        }

        if(this.album.has("picUrl")){
            this.picUrl = this.album.get("picUrl").getAsString();
        }else{
            this.picUrl = cover;
        }
    }

    /**
     * 红心音乐
     * @param like
     * @return
     */
    public JsonObject like(boolean like){
        Map<String, Object> data = new HashMap<>();
        data.put("alg", "itembased");
        data.put("trackId", this.id);
        data.put("like", like);
        data.put("time", 3);

        JsonObject result = this.api.POST_API("/api/radio/like", data);
        return result;
    }

    /**
     * 获得音乐 url
     * @param br
     * @return
     */
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
       source.sendFeedback(TextClick.suggestText("cloudmusic.info.music.album", "§b" + this.album.get("name").getAsString(), "/cloudmusic album " + this.album.get("id").getAsLong()));
       source.sendFeedback(Text.translatable("cloudmusic.info.music.id", this.id));

       Map<String, String> optionsTextData = new LinkedHashMap<>();
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic music play " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.like").getString(), "/cloudmusic music like " + this.id);
       optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic subscribe " + this.id);
       source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
    
}
