package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        data.get("data").getAsJsonObject().get("dailySongs").getAsJsonArray().forEach(musicData -> musics.add(new Music(api, musicData.getAsJsonObject(), null)));
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.getAsJsonObject("creator").get("nickname").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), playList.get("name").getAsString()),
                        "/cloudmusic playlist " + playList.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 历史每日推荐歌曲记录
     * @return 页对象
     */
    public Page recommendHistorySongsRecent(){
        JsonObject data = this.api.POST_API("/api/discovery/recommend/songs/history/recent", null);
        return new Page(data.get("data").getAsJsonObject().get("dates").getAsJsonArray()) {
            @Override
            protected TextClickItem putPageItem(Object data) {
                String historySongsRecent = ((JsonPrimitive) data).getAsString();
                return new TextClickItem(
                        Text.literal("§b%s".formatted(historySongsRecent)),
                        Text.translatable(IdUtil.getShowInfo("page.history.songs.recent"), historySongsRecent),
                        "/cloudmusic my recommend history " + historySongsRecent);
            }
        };
    }

    /**
     * 历史每日推荐歌曲
     * @param date 历史推荐日期
     * @return 歌曲列表
     */
    public List<IMusic> recommendHistorySongs(String date){
        Map<String, Object> data = new HashMap<>();
        data.put("date", date);
        JsonObject recommendHistorySongsData = this.api.POST_API("/api/discovery/recommend/songs/history/detail", data);

        List<IMusic> musics = new ArrayList<>();
        recommendHistorySongsData.get("data").getAsJsonObject().get("songs").getAsJsonArray().forEach(musicData -> musics.add(new Music(api, musicData.getAsJsonObject(), null)));
        return musics;
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page.playlist." + op), playList.get("name").getAsString(), musicId),
                        "/cloudmusic playlist %s %s %s".formatted(op, playList.get("id").getAsLong(), musicId)
                );
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject album = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        album.get("name").getAsString(),
                                        album.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString(),
                                        album.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), album.get("name").getAsString()),
                        "/cloudmusic album " + album.get("id").getAsLong()
                );
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject djRadios = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        djRadios.get("name").getAsString(),
                                        djRadios.getAsJsonObject("dj").get("nickname").getAsString(),
                                        djRadios.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), djRadios.get("name").getAsString()),
                        "/cloudmusic dj " + djRadios.get("id").getAsLong()
                );
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
            protected TextClickItem putPageItem(Object data) {
                JsonObject style = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        style.get("tagName").getAsString(),
                                        style.get("enName").getAsString(),
                                        style.get("tagId").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page.style"), style.get("tagName").getAsString()),
                        "/cloudmusic style " + style.get("tagId").getAsInt()
                );
            }
        };
    }

    /**
     * 最近播放歌曲
     * @return 歌曲列表
     */
    public List<IMusic> recordPlayMusic(){
        JsonObject json = this.api.POST_API("/api/play-record/song/list", null);
        List<IMusic> musics = new ArrayList<>();
        json.getAsJsonObject("data").getAsJsonArray("list").forEach(music -> {
            musics.add(new Music(this.api, ((JsonObject) music).getAsJsonObject("data"), null));
        });
        return musics;
    }

    /**
     * 最近播放声音
     * @return 歌曲列表
     */
    public List<IMusic> recordPlayDjMusic(){
        JsonObject json = this.api.POST_API("/api/play-record/voice/list", null);
        List<IMusic> musics = new ArrayList<>();
        json.getAsJsonObject("data").getAsJsonArray("list").forEach(music -> {
            musics.add(new DjMusic(this.api, ((JsonObject) music).getAsJsonObject("data").getAsJsonObject("pubDJProgramData")));
        });
        return musics;
    }

    /**
     * 最近播放歌单
     * @return 页对象
     */
    public Page recordPlayPlayList(){
        JsonObject json = this.api.POST_API("/api/play-record/playlist/list", null);
        return new Page(json.getAsJsonObject("data").getAsJsonArray("list")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = ((JsonObject) data).getAsJsonObject("data");
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.getAsJsonObject("creator").get("nickname").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), playList.get("name").getAsString()),
                        "/cloudmusic playlist " + playList.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 最近播放专辑
     * @return 页对象
     */
    public Page recordPlayAlbum(){
        JsonObject json = this.api.POST_API("/api/play-record/album/list", null);
        return new Page(json.getAsJsonObject("data").getAsJsonArray("list")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject album = ((JsonObject) data).getAsJsonObject("data");
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

    /**
     *  最近播放电台
     * @return 页对象
     */
    public Page recordPlayDj(){
        JsonObject json = this.api.POST_API("/api/play-record/djradio/list", null);
        return new Page(json.getAsJsonObject("data").getAsJsonArray("list")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject djRadios = ((JsonObject) data).getAsJsonObject("data");
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        djRadios.get("name").getAsString(),
                                        djRadios.has("nickname") ? djRadios.getAsJsonObject("dj").get("nickname").getAsString() : djRadios.get("name").getAsString(),
                                        djRadios.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), djRadios.get("name").getAsString()),
                        "/cloudmusic dj " + djRadios.get("id").getAsLong()
                );
            }
        };
    }

}
