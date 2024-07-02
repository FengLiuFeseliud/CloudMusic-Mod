package fengliu.cloudmusic.music163.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;

import java.util.HashMap;
import java.util.Map;

public abstract class StylePage extends ApiPage {
    public static Map<String, Object> getApiData(long id){
        Map<String, Object> data = new HashMap<>();
        data.put("tagId", id);
        data.put("size", 24);
        data.put("cursor", 0);
        data.put("sort", 0);

        return data;
    }

    /**
     * 动态页对象, 翻页超出数据时自动请求 Api
     *
     * @param data      起始数据
     * @param dataCount 一共多少数据, 包括没在起始数据中的
     * @param path      Api 路径
     * @param api       HttpClient 对象
     * @param id  id
     */
    public StylePage(JsonArray data, int dataCount, String path, HttpClient api, int id) {
        super(data, dataCount, path, api, getApiData(id));
    }

    @Override
    protected JsonArray getNewPageData() {
        this.getPageIn ++;

        JsonObject json = this.api.POST_API(this.path, postData);
        this.postData.put("cursor", json.getAsJsonObject("data").getAsJsonObject("page").get("cursor").getAsInt());
        return this.getNewPageDataJsonArray(json);
    }

}
