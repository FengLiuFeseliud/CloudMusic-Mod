package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;

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
}
