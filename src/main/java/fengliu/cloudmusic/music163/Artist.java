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
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Artist extends Music163Object implements PrintObject, CanSubscribeObject {
    public final long id;
    public final String name;
    public final String briefDesc;
    public final int albumSize;
    public final int musicSize;

    public Artist(HttpClient api, JsonObject data) {
        super(api, data);
        JsonObject data_ = data.getAsJsonObject("data").getAsJsonObject("artist");
        this.id = data_.get("id").getAsLong();
        this.name = data_.get("name").getAsString();
        if(!data_.get("briefDesc").isJsonNull()){
            this.briefDesc = data_.get("briefDesc").getAsString();
        } else {
            this.briefDesc = null;
        }
        this.albumSize = data_.get("albumSize").getAsInt();
        this.musicSize = data_.get("musicSize").getAsInt();
    }

    /**
     * 热门 50 首
     * @return 音乐列表
     */
    public List<Music> topSong(){
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/artist/top/song", data);
        List<Music> musics = new ArrayList<>();
        json.get("songs").getAsJsonArray().forEach(music -> {
            musics.add(new Music(this.api, music.getAsJsonObject(), null));
        });

        return musics;
    }

    /**
     * 获取专辑
     * @return 页对象
     */
    public ApiPage albumPage(){
        Map<String, Object> data = new HashMap<>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", false);

        JsonObject json = this.api.POST_API("/api/artist/albums/" + this.id, data);
        return new ApiPage(json.getAsJsonArray("hotAlbums"), albumSize, "/api/artist/albums/" + this.id, api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("hotAlbums");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject album = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + album.get("name").getAsString() + "§r - id: " + album.get("id").getAsLong(), "/cloudmusic album " + album.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    public Page similar(){
        Map<String, Object> data = new HashMap<>();
        data.put("artistid", this.id);

        JsonObject json = this.api.POST_API("/api/discovery/simiArtist", data);
        return new Page(json.getAsJsonArray("artists")) {
            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject artist = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + artist.get("name").getAsString() + "§r - id: " + artist.get("id").getAsLong(), "/cloudmusic artist " + artist.get("id").getAsLong());
                return newPageData;
            }
        };
    }

    // public List<Music> music(){
    //     Map<String, Object> data = new HashMap<>();
    //     data.put("id", this.id);
    //     data.put("limit", 200);
    //     data.put("offset", 0);
    //     data.put("order", "time");

    //     List<Music> musics = new ArrayList<>();
    //     int musicsSize = 0;
    //     while(musicsSize != this.musicSize){
    //         JsonObject json = this.api.POST_API("/api/v1/artist/songs", data);
    //         json.getAsJsonArray("songs").forEach(music -> {
    //             musics.add(new Music(this.api, music.getAsJsonObject()));
    //         });

    //         musicsSize += json.get("total").getAsInt();
    //         data.put("offset", ((int) data.get("offset")) + 200);
    //     }

    //     return musics;
    // }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));
        
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.music", this.musicSize));
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.album", this.albumSize));
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.id", this.id));
        
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal("§7" + this.briefDesc));

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play.top50").getString(), "/cloudmusic artist top " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.album").getString(), "/cloudmusic artist album " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.similar.artist").getString(), "/cloudmusic artist similar " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic artist subscribe " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.unsubscribe").getString(), "/cloudmusic artist unsubscribe " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("artistId", this.id);
        data.put("artistIds", "[\"" + this.id + "\"]");

        this.api.POST_API("/api/artist/sub", data);
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("artistId", this.id);
        data.put("artistIds", "[\"" + this.id + "\"]");

        this.api.POST_API("/api/artist/unsub", data);
    }
    
}
