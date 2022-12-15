package fengliu.cloudmusic.util.music163;

import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;

public class Music163Object {
    protected final HttpClient api;
    
    public Music163Object(HttpClient api, JsonObject data){
        this.api = api;
    }
}
