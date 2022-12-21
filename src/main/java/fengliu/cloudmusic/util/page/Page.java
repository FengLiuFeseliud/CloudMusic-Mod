package fengliu.cloudmusic.util.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Map;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public abstract class Page {
    protected final int limit = CloudMusicClient.CONFIG.getOrDefault("page.limit", 8);
    protected final List<List<?>> data;
    protected final int pageCount;
    protected int pageIn;

    protected abstract Map<String, String> putPageItem(Map<String, String> newPageData, Object data);

    protected Map<String, String> setPageData(List<?> pageData) {
        Map<String, String> newPageData = new LinkedHashMap<>();
        for(Object data: pageData){
            newPageData = this.putPageItem(newPageData, data);
        }
        return newPageData;
    }

    protected void printToChatHud(FabricClientCommandSource source, Map<String, String> pageData) {
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.translatable("cloudmusic.info.page.count", this.pageIn + 1 + "§c§l/§r" + this.pageCount));

        for(Entry<String, String> data: pageData.entrySet()){
            source.sendFeedback(TextClick.suggestText(data.getKey(), data.getValue()));
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.up").getString(), "/cloudmusic page up");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.down").getString(), "/cloudmusic page down");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.to").getString(), "/cloudmusic page to ");
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }

    protected List<List<?>> splitData(List<?> data) {
        List<List<?>> splitData = new ArrayList<>();
        for(int index = 0; index <= this.pageCount - 1; index ++){
            try {
                splitData.add(data.subList(index * limit, (index + 1) * limit));
            } catch (IndexOutOfBoundsException e) {
                if(index * limit != data.size()){
                    splitData.add(data.subList(index * limit, data.size()));
                }
                break;
            }
        }
        return splitData;
    }

    public Page(List<?> data){
        this.pageCount = (int) Math.ceil(data.size() * 1.0f / limit);
        this.data = this.splitData(data);
    }

    public Page(List<?> data, int dataCount){
        this.pageCount = (int) Math.ceil(dataCount * 1.0f / limit);
        if(dataCount < data.size()){
            data = data.subList(0, dataCount);
        }
        this.data = this.splitData(data);
    }

    public void look(FabricClientCommandSource source){
        Map<String, String> pageData = this.setPageData(this.data.get(this.pageIn));
        this.printToChatHud(source, pageData);
    }
    
    public void up(FabricClientCommandSource source){
        if(this.pageIn <= 0){
            return;
        }

        this.pageIn --;
        this.look(source);
    }

    public void down(FabricClientCommandSource source){
        if(this.pageIn >= this.pageCount - 1){
            return;
        }

        this.pageIn ++;
        this.look(source);
    }

    public void to(int page, FabricClientCommandSource source){
        if(page < 0){
            page = 0;
        }

        if(page >= this.pageCount - 1){
            page = this.pageCount - 1;
        }

        if(this.pageIn == page){
            return;
        }
        
        this.pageIn = page;
        this.look(source);
    }

}
