package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.JsonPage;

/**
 * cookie 用户对象
 */
public class My extends User {

    public My(HttpClient api, JsonObject data) {
        super(api, data);
    }
    
    /**
     * 日推歌
     * @return 音乐列表
     */
    public List<Music> recommend_songs(){
        JsonObject data = this.api.POST_API("/api/v3/discovery/recommend/songs", null);

        List<Music> musics = new ArrayList<>();
        data.get("data").getAsJsonObject().get("dailySongs").getAsJsonArray().forEach(musicData -> {
            musics.add(new Music(api, musicData.getAsJsonObject()));
        });
        return musics;
    }

    /**
     * 日推歌单 
     * @return 页对象
     */
    public JsonPage recommend_resource(){
        JsonObject data = this.api.POST_API("/api/v1/discovery/recommend/resource", null);
        return new JsonPage(data.get("recommend").getAsJsonArray()) {

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = ((JsonElement) data).getAsJsonObject(); 
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + playList.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * cookie 用户收藏的专辑
     * @return 页对象
     */
    public ApiPage sublist_album(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/album/sublist", data);
        return new ApiPage(json.get("data").getAsJsonArray(), json.get("count").getAsInt(), "/api/album/sublist", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("data");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject album = ((JsonElement) data).getAsJsonObject(); 
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + album.get("name").getAsString() + "§r - id: " + album.get("id").getAsLong(), "/cloudmusic album " + album.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * cookie 用户收藏的歌手
     * @return 页对象
     */
    public ApiPage sublist_artist(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/artist/sublist", data);
        return new ApiPage(json.get("data").getAsJsonArray(), json.get("count").getAsInt(), "/api/artist/sublist", this.api, data) {
            
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("data");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject artist = ((JsonElement) data).getAsJsonObject(); 
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + artist.get("name").getAsString() + "§r - id: " + artist.get("id").getAsLong(), "/cloudmusic artist " + artist.get("id").getAsLong());
                return newPageData;
            }

        };
    }



}
