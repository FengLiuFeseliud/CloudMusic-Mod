package fengliu.cloudmusic.util.page;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.reflect.TypeToken;
import fengliu.cloudmusic.util.HttpClient;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

/**
 * 动态页对象, 翻页超出数据时自动请求 Api
 */
public abstract class ApiPage extends Page {
    protected final String path;
    protected final HttpClient api;
    protected final Map<String, Object> postData;
    private int canUsePageCount = 0;
    protected int getPageIn = 0;

    /**
     * 动态页对象, 翻页超出数据时自动请求 Api
     * @param data 起始数据
     * @param dataCount 一共多少数据, 包括没在起始数据中的
     * @param path Api 路径
     * @param api HttpClient 对象
     * @param postData Api 请求参数
     */
    public ApiPage(JsonArray data, int dataCount, String path, HttpClient api, Map<String, Object> postData) {
        super(data, dataCount);
        this.path = path;
        this.api = api;
        this.postData = postData;
        this.setCanUsePage(data);
    }

    private void setCanUsePage(JsonArray data){
        this.canUsePageCount = (int) Math.ceil(data.size() * 1.0f / this.limit);
    }

    private void addCanUsePage(JsonArray data){
        this.data.addAll(this.splitData(new Gson().fromJson(data, new TypeToken<List<JsonElement>>(){}.getType())));
        this.canUsePageCount = this.data.size();
    }

    protected JsonArray getNewPageData(){
        this.getPageIn ++;
        this.postData.put("offset", (int) postData.get("limit") * this.getPageIn);

        return this.getNewPageDataJsonArray(this.api.POST_API(this.path, postData));
    }

    /**
     * 设置每次请求 Api 后所需的数据 JsonArray
     * @param result Api 数据
     * @return 页所需的数据
     */
    protected abstract JsonArray getNewPageDataJsonArray(JsonObject result);

    @Override
    public void to(int page, FabricClientCommandSource source) {
        if(page >= this.pageCount - 1){
            page = this.pageCount - 1;
        }
        
        if(page >= this.canUsePageCount && this.canUsePageCount != this.pageCount){
            this.addCanUsePage(this.getNewPageData());
            this.to(page, source);
            return;
        }
        super.to(page, source);
    }

    @Override
    public void next(FabricClientCommandSource source) {
        if(this.pageIn + 1 >= this.canUsePageCount && this.canUsePageCount != this.pageCount){
            this.addCanUsePage(this.getNewPageData());
        }

        super.next(source);
    } 
    
}
