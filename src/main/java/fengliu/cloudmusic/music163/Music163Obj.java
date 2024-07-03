package fengliu.cloudmusic.music163;

import com.google.gson.JsonObject;
import fengliu.cloudmusic.util.HttpClient;

public class Music163Obj {
    protected final HttpClient api;
    
    /**
     * 初始化对象
     * @param api HttpClient api
     * @param data 对象数据
     */
    public Music163Obj(HttpClient api, JsonObject data){
        this.api = api;
    }


}
