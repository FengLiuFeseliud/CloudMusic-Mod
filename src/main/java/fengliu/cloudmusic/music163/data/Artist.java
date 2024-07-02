package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.*;

public class Artist extends Music163Obj implements IPrint, ICanSubscribe {
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
     * @return 歌曲列表
     */
    public List<IMusic> topSong(){
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/artist/top/song", data);
        List<IMusic> musics = new ArrayList<>();
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject album = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        album.get("name").getAsString(),
                                        album.getAsJsonObject("artist").get("name").getAsString(),
                                        album.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), album.get("name").getAsString()),
                        "/cloudmusic album " + album.get("id").getAsLong()
                );
            }
        };
    }

    public Page similar(){
        Map<String, Object> data = new HashMap<>();
        data.put("artistid", this.id);

        JsonObject json = this.api.POST_API("/api/discovery/simiArtist", data);
        return new Page(json.getAsJsonArray("artists")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject artist = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- id: %s"
                                .formatted(
                                        artist.get("name").getAsString(),
                                        artist.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), artist.get("name").getAsString()),
                        "/cloudmusic artist " + artist.get("id").getAsLong()
                );
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

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("play.top50", "/cloudmusic artist top " + this.id),
                new TextClickItem("album", "/cloudmusic artist album " + this.id),
                new TextClickItem("similar.artist", "/cloudmusic artist similar " + this.id),
                new TextClickItem("subscribe", "/cloudmusic artist subscribe " + this.id),
                new TextClickItem("unsubscribe", "/cloudmusic artist unsubscribe " + this.id),
                new TextClickItem("shar", Shares.ARTIST.getShar(this.id))
        ));
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
