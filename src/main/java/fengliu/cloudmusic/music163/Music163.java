package fengliu.cloudmusic.music163;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.data.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.Page;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Music163 api
 */
public class Music163 {
    private final HttpClient api;
    
    public Music163(@Nullable String cookies) {
        if (cookies == null) {
            cookies = "";
        }
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "*/*");
        header.put("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        header.put("Connection", "keep-alive");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Referer", "http://music.163.com");
        header.put("Host", "music.163.com");
        header.put("Cookie", "appver=2.7.1.198277; os=pc; " + cookies);
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
        this.api = new HttpClient("https://music.163.com", header);
    }

    public HttpClient getHttpClient(){
        return this.api; 
    }

    /**
     * 获取歌曲
     * @param id 歌曲 id
     * @return 歌曲对象
     */
    public Music music(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("c", "[{\"id\": " + id + "}]");

        JsonArray json = this.api.POST_API("/api/v3/song/detail", data).getAsJsonArray("songs");
        if(json.isEmpty()){
            throw new ActionException(Text.translatable("cloudmusic.exception.music.id"));
        }
        return new Music(getHttpClient(), json.get(0).getAsJsonObject(), null);
    }

    /**
     * 获取歌单
     * @param id 歌单 id
     * @return 歌单对象
     */
    public PlayList playlist(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("n", 100000);

        JsonObject json = this.api.POST_API("/api/v6/playlist/detail", data);
        return new PlayList(getHttpClient(), json);
    }

    /**
     * 获取歌手
     * @param id 歌手 id
     * @return 歌手对象
     */
    public Artist artist(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);

        JsonObject json = this.api.POST_API("/api/artist/head/info/get", data);
        return new Artist(this.api, json);
    }

    /**
     * 获取专辑
     * @param id 专辑 id
     * @return 专辑对象
     */
    public Album album(long id){
        JsonObject json = this.api.POST_API("/api/v1/album/" + id, null);
        if(!json.get("resourceState").getAsBoolean()){
            throw new ActionException(Text.translatable("cloudmusic.exception.album.id"));
        }

        return new Album(getHttpClient(), json);
    }

    /**
     * 获取电台
     * @param id 电台 id
     * @return 电台对象
     */
    public DjRadio djRadio(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);

        return new DjRadio(this.api, this.api.POST_API("/api/djradio/v2/get", data).getAsJsonObject("data"));
    }

    public DjMusic djMusic(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);

        return new DjMusic(this.api, this.api.POST_API("/api/dj/program/detail", data).getAsJsonObject("program"));
    }

    /**
     * 获取用户
     * @param id 用户 id
     * @return 用户对象
     */
    public User user(long id){
        JsonObject json = this.api.POST_API("/api/v1/user/detail/" + id, null);
        if(json.get("code").getAsInt() != 200){
            throw new ActionException(Text.translatable("cloudmusic.exception.user.id"));
        }

        return new User(this.api, json);
    }

    /**
     * 获取 cookie 用户
     * @return cookie 用户对象
     */
    public My my(){
        JsonObject json = this.api.POST_API("/api/w/nuser/account/get", null);
        if(json.get("account").isJsonNull()){
            throw new ActionException(Text.translatable("cloudmusic.exception.cookie"));
        }
        return new My(this.api, this.api.POST_API("/api/v1/user/detail/" + json.getAsJsonObject("profile").get("userId").getAsLong(), null));
    }

    /**
     * 获取曲风
     * @param id 曲风 id
     * @return 曲风对象
     */
    public StyleTag style(int id){
        Map<String, Object> data = new HashMap<>();
        data.put("tagId", id);

        return new StyleTag(this.api, this.api.POST_API("/api/style-tag/home/head", data).getAsJsonObject("data"));
    }

    /**
     * 获取搜索数据
     * @param key 搜索内容
     * @param type 搜索类型 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户
        1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频
     * @return POST_API data
     */
    private Map<String, Object> searchData(String key, int type){
        Map<String, Object> data = new HashMap<>();
        data.put("s", key);
        data.put("type", type);
        data.put("limit", 30);
        data.put("offset", 0);
        data.put("total", true);
        return data;
    }

    /**
     * 搜索歌曲
     * @param key 搜索内容
     * @return 页对象
     */
    public ApiPage searchMusic(String key){
        Map<String, Object> data = this.searchData(key, 1);
        JsonObject json = this.api.POST_API("/api/cloudsearch/pc", data);
        return new ApiPage(json.getAsJsonObject("result").getAsJsonArray("songs"), json.getAsJsonObject("result").get("songCount").getAsInt(), "/api/cloudsearch/pc", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("result").getAsJsonArray("songs");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject music = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        music.get("name").getAsString(),
                                        Music.getArtistsName(music.getAsJsonArray("ar")),
                                        music.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), music.get("name").getAsString()),
                        "/cloudmusic music " + music.get("id").getAsLong()
                );
            }
        }; 
    }

    /**
     * 搜索歌单
     * @param key 搜索内容
     * @return 页对象
     */
    public ApiPage searchPlayList(String key){
        Map<String, Object> data = this.searchData(key, 1000);
        JsonObject json = this.api.POST_API("/api/cloudsearch/pc", data);
        return new ApiPage(json.getAsJsonObject("result").getAsJsonArray("playlists"), json.getAsJsonObject("result").get("playlistCount").getAsInt(), "/api/cloudsearch/pc", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("result").getAsJsonArray("playlists");
            }

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
     * 搜索专辑
     * @param key 搜索内容
     * @return 页对象
     */
    public ApiPage searchAlbum(String key){
        Map<String, Object> data = this.searchData(key, 10);
        JsonObject json = this.api.POST_API("/api/cloudsearch/pc", data);
        return new ApiPage(json.getAsJsonObject("result").getAsJsonArray("albums"), json.getAsJsonObject("result").get("albumCount").getAsInt(), "/api/cloudsearch/pc", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("result").getAsJsonArray("albums");
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

    /**
     * 搜索歌手
     * @param key 搜索内容
     * @return 页对象
     */
    public ApiPage searchArtist(String key){
        Map<String, Object> data = this.searchData(key, 100);
        JsonObject json = this.api.POST_API("/api/cloudsearch/pc", data);
        return new ApiPage(json.getAsJsonObject("result").getAsJsonArray("artists"), json.getAsJsonObject("result").get("artistCount").getAsInt(), "/api/cloudsearch/pc", this.api, data) {

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("result").getAsJsonArray("artists");
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
     * 搜索电台
     * @param key 搜索内容
     * @return 页对象
     */
    public  ApiPage searchDjRadio(String key){
        Map<String, Object> data = this.searchData(key, 1009);
        JsonObject json = this.api.POST_API("/api/cloudsearch/pc", data);
        return new ApiPage(json.getAsJsonObject("result").getAsJsonArray("djRadios"), json.getAsJsonObject("result").get("djRadiosCount").getAsInt(), "/api/cloudsearch/pc", this.api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("result").getAsJsonArray("djRadios");
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

    public JsonArray styles(){
        return this.api.POST_API("/api/tag/list/get", null).getAsJsonArray("data");
    }

    /**
     * 曲风列表
     * @return 页对象
     */
    public Page styleList(){
        return new Page(this.styles()) {
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
     * 歌单标签
     * @return 页对象
     */
    public Page playListTags(){
        return new Page(this.api.POST_API("/api/playlist/catalogue", null).getAsJsonArray("sub")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject tags = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b" + tags.get("name").getAsString()),
                        Text.translatable(IdUtil.getShowInfo("page.playlist.tags"), tags.get("name").getAsString()),
                        "/cloudmusic top playlist \"" + tags.get("name").getAsString() + "\""
                );
            }
        };
    }

    /**
     * 热门歌单标签
     * @return 页对象
     */
    public Page playListTagsHot(){
        return new Page(this.api.POST_API("/api/playlist/hottags", null).getAsJsonArray("tags")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject tags = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b" + tags.get("name").getAsString()),
                        Text.translatable(IdUtil.getShowInfo("page.playlist.tags"), tags.get("name").getAsString()),
                        "/cloudmusic top playlist \"" + tags.get("name").getAsString() + "\""
                );
            }
        };
    }

    /**
     * 精品歌单标签
     * @return 页对象
     */
    public Page playListHighQualityTags(){
        return new Page(this.api.POST_API("/api/playlist/highquality/tags", null).getAsJsonArray("tags")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject tags = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b" + tags.get("name").getAsString()),
                        Text.translatable(IdUtil.getShowInfo("page.playlist.tags"), tags.get("name").getAsString()),
                        "/cloudmusic top playlist highquality \"" + tags.get("name").getAsString() + "\""
                );
            }
        };
    }

    /**
     * 获取精品歌单
     * @param highQualityTags 标签 playListHighQualityTags()
     * @return 页对象
     */
    public Page topPlayListHighQuality(String highQualityTags){
        Map<String, Object> data = new HashMap<>();
        data.put("cat", highQualityTags);
        data.put("limit", 48);
        data.put("lasttime", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/playlist/highquality/list", data);
        if (!json.has("total")){
            return null;
        }
        return new ApiPage(json.getAsJsonArray("playlists"), json.get("total").getAsInt(), "/api/playlist/highquality/list", this.api, data) {
            @Override
            protected JsonArray getNewPageData() {
                this.getPageIn ++;

                JsonObject json = this.api.POST_API(this.path, postData);
                this.postData.put("lasttime", json.get("lasttime").getAsLong());
                return this.getNewPageDataJsonArray(json);
            }

            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return json.getAsJsonArray("playlists");
            }

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
     * 歌单 (网友精选碟)
     * @param tag 标签 topPlayListTags() / topPlayListTagsHot()
     * @return 页对象
     */
    public Page topPlayList(String tag){
        Map<String, Object> data = new HashMap<>();
        data.put("cat", tag);
        data.put("limit", 48);
        data.put("offset", 0);
        data.put("order", "hot");
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/playlist/list", data);
        return new ApiPage(json.getAsJsonArray("playlists"), json.get("total").getAsInt(), "/api/playlist/list", this.api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return json.getAsJsonArray("playlists");
            }

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
     * 所有榜单
     * @return 页对象
     */
    public Page topList(){
        JsonObject json = this.api.POST_API("/api/toplist", null);
        return new Page(json.getAsJsonArray("list")) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.get("updateFrequency").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), playList.get("name").getAsString()),
                        "/cloudmusic playlist " + playList.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 歌手榜
     * @return 页对象
     */
    public Page topArtistList(){
        Map<String, Object> data = new HashMap<>();
        data.put("type", 1);
        data.put("limit", 100);
        data.put("offset", 0);
        data.put("total", true);

        JsonObject json = this.api.POST_API("/api/toplist/artist", data);
        return new Page(json.getAsJsonObject("list").getAsJsonArray("artists")) {

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

}
