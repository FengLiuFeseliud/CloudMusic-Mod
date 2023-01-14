package fengliu.cloudmusic.util.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Map;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * 页对象, 处理翻页
 */
public abstract class Page {
    protected final int limit = Configs.GUI.PAGE_LIMIT.getIntegerValue();
    protected final List<List<?>> data;
    protected final int pageCount;
    protected int pageIn;
    private Text infoText;

    /**
     * 设置每一项的数据格式
     * @param newPageData 当前页所有数据
     * @param data 当前页一项数据
     * @return 当前页所有数据
     */
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
        if(this.infoText != null){
            source.sendFeedback(this.infoText);
        }
        source.sendFeedback(Text.translatable("cloudmusic.info.page.count", this.pageIn + 1 + "§c§l/§r" + this.pageCount));

        for(Entry<String, String> data: pageData.entrySet()){
            source.sendFeedback(TextClick.suggestText(data.getKey(), data.getValue()));
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.prev").getString(), "/cloudmusic page prev");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.next").getString(), "/cloudmusic page next");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.to").getString(), "/cloudmusic page to ");
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }

    protected void printToChatHud(Map<String, String> pageData) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null){
            return;
        }

        client.player.sendMessage(Text.literal(""));
        if(this.infoText != null){
            client.player.sendMessage(this.infoText);
        }
        client.player.sendMessage(Text.translatable("cloudmusic.info.page.count", this.pageIn + 1 + "§c§l/§r" + this.pageCount));

        for(Entry<String, String> data: pageData.entrySet()){
            client.player.sendMessage(TextClick.suggestText(data.getKey(), data.getValue()));
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.prev").getString(), "/cloudmusic page prev");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.next").getString(), "/cloudmusic page next");
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.page.to").getString(), "/cloudmusic page to ");
        client.player.sendMessage(TextClick.suggestTextMap(optionsTextData, " "));
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

    /**
     * 查看当前页
     * @param source Fabric 命令源
     */
    public void look(FabricClientCommandSource source){
        Map<String, String> pageData = this.setPageData(this.data.get(this.pageIn));
        this.printToChatHud(source, pageData);
    }

    public void look(){
        Map<String, String> pageData = this.setPageData(this.data.get(this.pageIn));
        this.printToChatHud(pageData);
    }
    
    /**
     * 查看上一页
     * @param source Fabric 命令源
     */
    public void up(FabricClientCommandSource source){
        if(this.pageIn <= 0){
            return;
        }

        this.pageIn --;
        this.look(source);
    }

    /**
     * 查看下一页
     * @param source Fabric 命令源
     */
    public void down(FabricClientCommandSource source){
        if(this.pageIn >= this.pageCount - 1){
            return;
        }

        this.pageIn ++;
        this.look(source);
    }

    /**
     * 跳转至
     * @param page 页索引
     * @param source Fabric 命令源
     */
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

    public void setInfoText(Text info){
        this.infoText = info;
    }

}
