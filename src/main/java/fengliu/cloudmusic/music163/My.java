package fengliu.cloudmusic.music163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;

/**
 * cookie 用户对象
 */
public class My extends User {

    public My(HttpClient api, JsonObject data) {
        super(api, data);
    }
    
    /**
     * 日推歌
     * @return 歌曲列表
     */
    public List<IMusic> recommendSongs(){
        JsonObject data = this.api.POST_API("/api/v3/discovery/recommend/songs", null);

        List<IMusic> musics = new ArrayList<>();
        data.get("data").getAsJsonObject().get("dailySongs").getAsJsonArray().forEach(musicData -> {
            musics.add(new Music(api, musicData.getAsJsonObject(), null));
        });
        return musics;
    }

    /**
     * 日推歌单 
     * @return 页对象
     */
    public Page recommendResource(){
        JsonObject data = this.api.POST_API("/api/v1/discovery/recommend/resource", null);
        return new Page(data.get("recommend").getAsJsonArray()) {

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r§7 - "+ playList.getAsJsonObject("creator").get("nickname").getAsString() +" - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + playList.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * 心动模式
     * @return 歌曲列表
     */
    public List<IMusic> intelligencePlayMode(){
        PlayList playList = this.likeMusicPlayList();
        List<IMusic> musics = playList.getMusics();
        IMusic randomMusic = musics.get((int) (Math.random() * musics.size()));

        Map<String, Object> data = new HashMap<>();
        data.put("songId", randomMusic.getId());
        data.put("type", "fromPlayOne");
        data.put("playlistId", playList.id);
        data.put("startMusicId", randomMusic.getId());
        data.put("count", 1);

        musics.clear();
        JsonObject json = this.api.POST_API("/api/playmode/intelligence/list", data);
        json.getAsJsonArray("data").forEach(musicData -> {
            musics.add(new Music(this.api, ((JsonObject) musicData).getAsJsonObject("songInfo"), null));
        });
        return musics;
    }

    public ApiPage playListSetMusic(long musicId, String op){
        Object[] data = this.getPlayListPageData();
        return new ApiPage(((JsonObject) data[0]).getAsJsonArray("playlist"), this.playlistCount, "/api/user/playlist", this.api, (Map<String, Object>) data[1]) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray("playlist");
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject playList = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r§7 - id: " + playList.get("id").getAsLong(), "/cloudmusic playlist " + op + " " + playList.get("id").getAsLong() + " " + musicId);
                return newPageData;
            }

        };
    }

    /**
     * cookie 用户收藏的专辑
     * @return 页对象
     */
    public ApiPage sublistAlbum(){
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
                JsonObject album = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + album.get("name").getAsString() + "§r§7 - "+ album.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString() +" - id: " + album.get("id").getAsLong(), "/cloudmusic album " + album.get("id").getAsLong());
                return newPageData;
            }
            
        };
    }

    /**
     * cookie 用户收藏的歌手
     * @return 页对象
     */
    public ApiPage sublistArtist(){
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
                JsonObject artist = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + artist.get("name").getAsString() + "§r§7 - id: " + artist.get("id").getAsLong(), "/cloudmusic artist " + artist.get("id").getAsLong());
                return newPageData;
            }

        };
    }

    /**
     * cookie 用户收藏的电台
     * @return 页对象
     */
    public ApiPage sublistDjRadio(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("limit", 24);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/djradio/get/subed", data);
        return new ApiPage(json.get("djRadios").getAsJsonArray(), json.get("count").getAsInt(), "/api/djradio/get/subed", this.api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.get("djRadios").getAsJsonArray();
            }

            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject djRadios = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + djRadios.get("name").getAsString() + "§r§7 - "+ djRadios.getAsJsonObject("dj").get("nickname").getAsString() +" - id: " + djRadios.get("id").getAsLong(), "/cloudmusic dj " + djRadios.get("id").getAsLong());
                return newPageData;
            }
        };
    }

    /**
     * 获取 fm 曲目
     *
     * @return 歌曲列表
     */
    public List<IMusic> fm (){
        JsonObject json = this.api.POST_API("/api/v1/radio/get", null);

        List<IMusic> musics = new ArrayList<>();
        json.getAsJsonArray("data").forEach(music -> {
            musics.add(new Music(this.api, music.getAsJsonObject(), null));
        });
        return musics;
    }

    /**
     * cookie 用户曲风偏好
     * @return 页对象
     */
    public Page preferenceStyles(){
        JsonObject json = this.api.POST_API("/api/tag/my/preference/get", null);
        return new Page(json.getAsJsonObject("data").getAsJsonArray("tags")) {
            @Override
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject style = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + style.get("tagName").getAsString() + "§r§7 - " + style.get("enName").getAsString() + " - id: " + style.get("tagId").getAsInt(), "/cloudmusic style " + style.get("tagId").getAsInt());
                return newPageData;
            }
        };
    }

}
