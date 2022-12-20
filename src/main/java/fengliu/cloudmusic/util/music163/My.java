package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.music163.page.*;

public class My extends User {

    public My(HttpClient api, JsonObject data) {
        super(api, data);
    }
    
    public List<Music> recommend_songs(){
        JsonObject data = this.api.POST_API("/api/v3/discovery/recommend/songs", null);

        List<Music> musics = new ArrayList<>();
        data.get("data").getAsJsonObject().get("dailySongs").getAsJsonArray().forEach(musicData -> {
            musics.add(new Music(api, musicData.getAsJsonObject()));
        });
        return musics;
    }

    public PlayListPage.FixedPlayList recommend_resource(){
        JsonObject data = this.api.POST_API("/api/v1/discovery/recommend/resource", null);
        return new PlayListPage.FixedPlayList(data.get("recommend").getAsJsonArray());
    }

    public AlbumPage.UncertaintyAlbum sublist_album(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/album/sublist", data);
        return new AlbumPage.UncertaintyAlbum(json.get("data").getAsJsonArray(), json.get("count").getAsInt(), "/api/album/sublist", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("data");
            }
            
        };
    }

    public ArtistPage.UncertaintyArtist sublist_artist(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/artist/sublist", data);
        return new ArtistPage.UncertaintyArtist(json.get("data").getAsJsonArray(), json.get("count").getAsInt(), "/api/artist/sublist", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("data");
            }
            
        };
    }



}
