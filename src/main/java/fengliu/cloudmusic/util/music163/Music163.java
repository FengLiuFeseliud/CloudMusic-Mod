package fengliu.cloudmusic.util.music163;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;


import fengliu.cloudmusic.util.HttpClient;

public class Music163 {
    private Map<String, String> Header = new HashMap<String, String>();
    private final HttpClient api;
    
    public Music163(@Nullable String cookies){
        if(cookies == null){
            cookies = "";
        }
        this.Header.put("Accept", "*/*");
        this.Header.put("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        this.Header.put("Connection", "keep-alive");
        this.Header.put("Content-Type", "application/x-www-form-urlencoded");
        this.Header.put("Referer", "http://music.163.com");
        this.Header.put("Host", "music.163.com");
        this.Header.put("Cookie", "appver=2.7.1.198277; os=pc; " + cookies);
        this.Header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
        this.api = new HttpClient("https://music.163.com", this.Header);
    }

    public HttpClient getHttpClient(){
        return this.api; 
    }

    public Music music(long id){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("c", "[{\"id\": " + id + "}]");

        JsonObject json = this.api.POST_API("/api/v3/song/detail", data);
        return new Music(getHttpClient(), json);
    }

    public PlayList playlist(long id){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);
        data.put("n", 100000);

        JsonObject json = this.api.POST_API("/api/v6/playlist/detail", data);
        return new PlayList(getHttpClient(), json);
    }

    public Artist artist(long id){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);

        JsonObject json = this.api.POST_API("/api/artist/head/info/get", data);
        return new Artist(this.api, json);
    }

    public Album album(long id){
        return new Album(getHttpClient(), this.api.POST_API("/api/v1/album/" + id, null));
    }

    public User user(long id){
        return new User(this.api, this.api.POST_API("/api/v1/user/detail/" + id, null));
    }

    public My my(){
        JsonObject json = this.api.POST_API("/api/w/nuser/account/get", null);
        if(json.get("account").isJsonNull()){
            return null;
        }
        return new My(this.api, this.api.POST_API("/api/v1/user/detail/" + json.getAsJsonObject("profile").get("userId").getAsLong(), null));
    }
}
