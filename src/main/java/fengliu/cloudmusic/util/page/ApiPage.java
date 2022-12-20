package fengliu.cloudmusic.util.page;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public abstract class ApiPage extends JsonPage {
    private final String path;
    private final HttpClient api;
    private final Map<String, Object> postData;
    private int canUsePageCount = 0;
    private int getPageIn = 0;

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
        this.data.addAll(this.splitData(data.asList()));
        this.canUsePageCount = this.data.size();
    }

    private JsonArray getNewPageData(){
        this.getPageIn ++;
        this.postData.put("offset", (int) postData.get("limit") * this.getPageIn);

        return this.getNewPageDataJsonArray(this.api.POST_API(this.path, postData));
    }

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
    public void down(FabricClientCommandSource source) {
        if(this.pageIn + 1 >= this.canUsePageCount && this.canUsePageCount != this.pageCount){
            this.addCanUsePage(this.getNewPageData());
        }

        super.down(source);
    } 
    
}
