package fengliu.cloudmusic.util.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.util.TextClickItem;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
     *
     * @param data 当前页一项数据
     * @return 当前页所有数据
     */
    protected abstract TextClickItem putPageItem(Object data);

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

    public Page(JsonArray data) {
        this(data.asList());
    }

    public Page(JsonArray data, int dataCount) {
        this(data.asList(), dataCount);
    }

    /**
     * 查看当前页
     *
     * @param source Fabric 命令源
     */
    public void look(FabricClientCommandSource source) {
        List<?> pageDataList = this.data.get(this.pageIn);
        int offset = pageDataList.size() * this.pageIn;

        source.sendFeedback(Text.literal(""));
        if (this.infoText != null) {
            source.sendFeedback(this.infoText);
        }
        source.sendFeedback(Text.translatable("cloudmusic.info.page.count", this.pageIn + 1 + "§c§l/§r" + this.pageCount));

        for (Object data : pageDataList) {
            source.sendFeedback(Text.literal("[%s] ".formatted(offset + pageDataList.indexOf(data))).append(this.putPageItem(data).build()));
        }

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("page.prev", "/cloudmusic page prev"),
                new TextClickItem("page.next", "/cloudmusic page next"),
                new TextClickItem("page.to", "/cloudmusic page to")
        ));
    }

    public void look() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        List<?> pageDataList = this.data.get(this.pageIn);
        int offset = pageDataList.size() * this.pageIn;

        client.player.sendMessage(Text.literal(""));
        if (this.infoText != null) {
            client.player.sendMessage(this.infoText);
        }
        client.player.sendMessage(Text.translatable("cloudmusic.info.page.count", this.pageIn + 1 + "§c§l/§r" + this.pageCount));

        for (Object data : pageDataList) {
            client.player.sendMessage(Text.literal("[%s] ".formatted(offset + pageDataList.indexOf(data))).append(this.putPageItem(data).build()));
        }

        client.player.sendMessage(TextClickItem.combine(
                new TextClickItem("page.prev", "/cloudmusic page prev"),
                new TextClickItem("page.next", "/cloudmusic page next"),
                new TextClickItem("page.to", "/cloudmusic page to")
        ));
    }

    public JsonObject getJsonItem(Function<JsonObject, Boolean> get) {
        for (Object json : this.data.get(this.pageIn)) {
            if (get.apply((JsonObject) json)) {
                return (JsonObject) json;
            }
        }
        return null;
    }

    /**
     * 查看上一页
     *
     * @param source Fabric 命令源
     */
    public void prev(FabricClientCommandSource source) {
        if (this.pageIn <= 0) {
            return;
        }

        this.pageIn--;
        this.look(source);
    }

    /**
     * 查看下一页
     * @param source Fabric 命令源
     */
    public void next(FabricClientCommandSource source){
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
